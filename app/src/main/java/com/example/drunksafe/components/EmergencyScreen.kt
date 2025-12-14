package com.example.drunksafe.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation. background
import androidx.compose.foundation.clickable
import androidx. compose.foundation.layout.*
import androidx. compose.foundation.shape.CircleShape
import androidx.compose. foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material. icons.filled.ArrowBack
import androidx.compose.material.icons. filled. Notifications
import androidx.compose.runtime.*
import androidx. compose.ui. Alignment
import androidx. compose.ui. Modifier
import androidx. compose.ui.draw.clip
import androidx.compose.ui. graphics.Color
import androidx.compose.ui. platform.LocalContext
import androidx.compose. ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit. dp
import androidx. compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.drunksafe.viewmodel.TrustedContactsViewModel
import kotlinx.coroutines.delay

import com.example.drunksafe.ui.theme.AlertRed
import com.example.drunksafe.ui.theme.DarkBlue

@Composable
fun EmergencyScreen(
    onNavigateBack: () -> Unit = {},
    contactsViewModel: TrustedContactsViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by contactsViewModel.uiState. collectAsState()

    var tapCount by remember { mutableStateOf(0) }
    var isAlertTriggered by remember { mutableStateOf(false) }
    var showConfirmation by remember { mutableStateOf(false) }

    // Reset tap count after 2 seconds of inactivity
    LaunchedEffect(tapCount) {
        if (tapCount > 0 && tapCount < 3) {
            delay(2000)
            tapCount = 0
        }
    }

    // Function to trigger the alert
    fun triggerEmergencyAlert() {
        isAlertTriggered = true
        showConfirmation = true

        val contacts = uiState. contacts
        val message = "🆘 EMERGENCY ALERT! I need help urgently.  Please try to contact me or call emergency services.  - Sent via DrunkSafe"

        // Build SMS URI with all phone numbers
        if (contacts.isNotEmpty()) {
            val phoneNumbers = contacts
                .filter { it.phoneNumber.isNotBlank() }
                .joinToString(";") { it.phoneNumber }

            val smsIntent = Intent(Intent. ACTION_SENDTO).apply {
                data = Uri. parse("smsto:$phoneNumbers")
                putExtra("sms_body", message)
            }
            context.startActivity(smsIntent)
        }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = DarkBlue) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, "Back", tint = Color.White)
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "EMERGENCY",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.weight(1f))
                Box(modifier = Modifier.size(48.dp))
            }

            Spacer(modifier = Modifier.weight(0.3f))

            // SOS Circle Button
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .clip(CircleShape)
                    .background(AlertRed)
                    .clickable {
                        if (! isAlertTriggered) {
                            tapCount++
                            if (tapCount >= 3) {
                                triggerEmergencyAlert()
                            }
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.Notifications,
                        contentDescription = "Alert",
                        tint = Color.White,
                        modifier = Modifier.size(80.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "SOS",
                        color = Color. White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Tap counter indicator
            if (tapCount > 0 && tapCount < 3 && !isAlertTriggered) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Tap ${3 - tapCount} more time${if (3 - tapCount > 1) "s" else ""} to trigger alert",
                    color = AlertRed,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier. height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    repeat(3) { index ->
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(if (index < tapCount) AlertRed else Color. Gray)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier. weight(0.5f))

            // Trigger Alert Button
            Button(
                onClick = {
                    if (! isAlertTriggered) {
                        tapCount++
                        if (tapCount >= 3) {
                            triggerEmergencyAlert()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = AlertRed,
                    disabledBackgroundColor = Color.Gray
                ),
                shape = RoundedCornerShape(12. dp),
                enabled = !isAlertTriggered
            ) {
                Text(
                    text = if (isAlertTriggered) "ALERT SENT!" else "TRIGGER ALERT",
                    color = Color. White,
                    fontSize = 18. sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier. height(16.dp))

            Text(
                text = "By pressing the Trigger Alert button, your location will be shared and an automatic call to 112 (emergency services) will be initiated",
                color = Color.Gray,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Tap 3 times to confirm alert",
                color = AlertRed,
                fontSize = 14.sp,
                fontWeight = FontWeight. Medium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier. height(24.dp))
        }
    }

    // Confirmation Dialog
    if (showConfirmation) {
        AlertConfirmationDialog(
            contactCount = uiState. contacts.size,
            onDismiss = {
                showConfirmation = false
                tapCount = 0
                isAlertTriggered = false
            },
            onCall112 = {
                val callIntent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri. parse("tel:112")
                }
                context.startActivity(callIntent)
            }
        )
    }
}

@Composable
fun AlertConfirmationDialog(
    contactCount: Int,
    onDismiss: () -> Unit,
    onCall112: () -> Unit
) {
    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                . fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            backgroundColor = DarkBlue
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(AlertRed),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Notifications,
                        contentDescription = "Alert",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Emergency Alert Triggered! ",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "✓ SMS opened for $contactCount contact${if (contactCount != 1) "s" else ""}",
                    color = Color.White,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier. height(24.dp))

                // Call 112 Button
                Button(
                    onClick = onCall112,
                    colors = ButtonDefaults. buttonColors(backgroundColor = AlertRed),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("📞 CALL 112 NOW", color = Color. White, fontWeight = FontWeight. Bold)
                }

                Spacer(modifier = Modifier.height(12.dp))

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Close", color = Color. Gray)
                }
            }
        }
    }
}