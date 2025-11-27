package com.example.drunksafe.sos

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.CountDownTimer
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.drunksafe.R
import com.example.drunksafe.ui.theme.DrunkSafeTheme

/**
 * Emergency SOS Activity that provides:
 * - A visual SOS trigger button
 * - 3-second countdown before executing emergency actions
 * - Automatic phone call to emergency services (112)
 * - SMS with location to all emergency contacts
 * 
 * Required permissions: CALL_PHONE, SEND_SMS, ACCESS_FINE_LOCATION
 */
class SosActivity : ComponentActivity() {

    private var countDownTimer: CountDownTimer? = null
    private var currentLocation: Location? = null

    // Permission request launchers
    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        
        if (fineLocationGranted || coarseLocationGranted) {
            // Location permission granted, try to get location
            currentLocation = SosHelpers.getLastKnownLocation(this)
        }
        
        // After location, check call permission
        checkAndRequestCallPermission()
    }

    private val callPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (!granted) {
            SosHelpers.showToast(this, R.string.sos_error_call_permission)
        }
        // After call permission, check SMS permission
        checkAndRequestSmsPermission()
    }

    private val smsPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (!granted) {
            SosHelpers.showToast(this, R.string.sos_error_sms_permission)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request permissions on start
        checkAndRequestPermissions()

        setContent {
            DrunkSafeTheme {
                SosScreen(
                    onTriggerSos = { onCountdownComplete ->
                        startCountdown(onCountdownComplete)
                    },
                    onCancelCountdown = {
                        cancelCountdown()
                    }
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }

    /**
     * Checks and requests all required permissions in sequence.
     */
    private fun checkAndRequestPermissions() {
        // First check location permissions
        if (!hasLocationPermission()) {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        } else {
            // Already have location permission, get location
            currentLocation = SosHelpers.getLastKnownLocation(this)
            checkAndRequestCallPermission()
        }
    }

    private fun checkAndRequestCallPermission() {
        if (!hasCallPermission()) {
            callPermissionLauncher.launch(Manifest.permission.CALL_PHONE)
        } else {
            checkAndRequestSmsPermission()
        }
    }

    private fun checkAndRequestSmsPermission() {
        if (!hasSmsPermission()) {
            smsPermissionLauncher.launch(Manifest.permission.SEND_SMS)
        }
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    private fun hasCallPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CALL_PHONE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun hasSmsPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.SEND_SMS
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Starts the 3-second countdown before executing emergency actions.
     */
    private fun startCountdown(onComplete: () -> Unit) {
        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(3000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                // Countdown is managed by composable state
            }

            override fun onFinish() {
                onComplete()
                executeEmergencyActions()
            }
        }.start()
    }

    /**
     * Cancels the countdown timer.
     */
    private fun cancelCountdown() {
        countDownTimer?.cancel()
        countDownTimer = null
    }

    /**
     * Executes all emergency actions:
     * 1. Gets current location
     * 2. Sends SMS to all emergency contacts
     * 3. Initiates emergency call
     */
    private fun executeEmergencyActions() {
        // Try to get the latest location
        if (hasLocationPermission()) {
            currentLocation = SosHelpers.getLastKnownLocation(this)
        }

        if (currentLocation == null && hasLocationPermission()) {
            SosHelpers.showToast(this, R.string.sos_error_location)
        }

        // Check if there are emergency contacts
        val contacts = SosHelpers.getEmergencyContacts(this)
        if (contacts.isEmpty()) {
            SosHelpers.showToast(this, R.string.sos_error_no_contacts)
        } else if (hasSmsPermission()) {
            // Send SMS to all contacts
            val sentCount = SosHelpers.sendEmergencySmsToAllContacts(this, currentLocation)
            if (sentCount > 0) {
                SosHelpers.showToast(this, R.string.sos_sms_sent)
            } else {
                SosHelpers.showToast(this, R.string.sos_error_sms_failed)
            }
        } else {
            SosHelpers.showToast(this, R.string.sos_error_sms_permission)
        }

        // Initiate emergency call
        if (hasCallPermission()) {
            if (!SosHelpers.initiateEmergencyCall(this)) {
                SosHelpers.showToast(this, R.string.sos_error_call_failed)
            }
        } else {
            SosHelpers.showToast(this, R.string.sos_error_call_permission)
        }
    }
}

/**
 * Main SOS screen composable with dark background, SOS icon, and trigger button.
 */
@Composable
fun SosScreen(
    onTriggerSos: (onCountdownComplete: () -> Unit) -> Unit,
    onCancelCountdown: () -> Unit
) {
    var showCountdownDialog by remember { mutableStateOf(false) }
    var countdownValue by remember { mutableIntStateOf(3) }
    var countdownTimer: CountDownTimer? by remember { mutableStateOf(null) }

    // Colors matching the mock design
    val backgroundColor = Color(0xFF1A1A1A)
    val sosRed = Color(0xFFFF0000)
    val textSecondary = Color(0xFFB0B0B0)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.weight(1f))

            // SOS Icon - circular red icon
            Box(
                modifier = Modifier
                    .size(180.dp)
                    .clip(CircleShape)
                    .background(sosRed),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "SOS",
                    color = Color.White,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // TRIGGER ALERT Button
            Button(
                onClick = {
                    showCountdownDialog = true
                    countdownValue = 3
                    
                    // Start internal countdown for UI
                    countdownTimer?.cancel()
                    countdownTimer = object : CountDownTimer(3000, 1000) {
                        override fun onTick(millisUntilFinished: Long) {
                            countdownValue = ((millisUntilFinished / 1000) + 1).toInt()
                        }

                        override fun onFinish() {
                            showCountdownDialog = false
                        }
                    }.start()
                    
                    // Start actual SOS countdown
                    onTriggerSos {
                        showCountdownDialog = false
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                shape = RoundedCornerShape(32.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = sosRed
                )
            ) {
                Text(
                    text = stringResource(R.string.sos_trigger_alert),
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Explanation text
            Text(
                text = stringResource(R.string.sos_explanation),
                color = textSecondary,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(48.dp))
        }
    }

    // Countdown confirmation dialog
    if (showCountdownDialog) {
        AlertDialog(
            onDismissRequest = {
                showCountdownDialog = false
                countdownTimer?.cancel()
                onCancelCountdown()
            },
            title = {
                Text(
                    text = stringResource(R.string.sos_countdown_title),
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.sos_countdown_message, countdownValue)
                )
            },
            confirmButton = {},
            dismissButton = {
                TextButton(
                    onClick = {
                        showCountdownDialog = false
                        countdownTimer?.cancel()
                        onCancelCountdown()
                    }
                ) {
                    Text(
                        text = stringResource(R.string.sos_cancel),
                        color = sosRed
                    )
                }
            },
            containerColor = Color.White
        )
    }
}
