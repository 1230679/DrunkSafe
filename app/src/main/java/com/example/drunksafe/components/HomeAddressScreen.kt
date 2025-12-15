package com.example.drunksafe.ui

import android.location.Geocoder
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.drunksafe.data.repositories.HomeAddressPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import com.example.drunksafe.ui.theme.BackgroundDark
import com.example.drunksafe.ui.theme.GoldYellow
import com.example.drunksafe.ui.theme.GreenArrow
import com.example.drunksafe.ui.theme.White


/**
 * Screen for editing and saving the User's Home Address.
 *
 * This screen performs two critical functions:
 * 1. **Input:** Allows the user to type their address as text.
 * 2. **Geocoding:** Uses the Android [Geocoder] API to convert that text into
 * Latitude and Longitude coordinates.
 *
 * These coordinates are then saved to SharedPreferences so the "Take Me Home"
 * feature can calculate a route without needing to look up the address again.
 *
 * @param onNavigateBack Callback triggered when the user saves successfully or clicks the back button.
 */
@Composable
fun HomeAddressScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val prefs = remember { HomeAddressPreferences(context) }

    var addressText by remember { mutableStateOf(prefs.getHomeAddress() ?: "") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Home Address", color = White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = GreenArrow)
                    }
                },
                backgroundColor = BackgroundDark,
                elevation = 0.dp
            )
        },
        backgroundColor = BackgroundDark
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp)
        ) {

            Icon(
                imageVector = Icons.Default.Home,
                contentDescription = null,
                tint = GoldYellow,
                modifier = Modifier
                    .size(80.dp)
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Enter your full address below:",
                color = Color.Gray,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = addressText,
                onValueChange = {
                    addressText = it
                    errorMessage = null
                },
                label = { Text("Street, Number, City", color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = White,
                    cursorColor = GoldYellow,
                    focusedBorderColor = GoldYellow,
                    unfocusedBorderColor = Color.Gray,
                    focusedLabelColor = GoldYellow,
                    unfocusedLabelColor = Color.Gray
                ),
                singleLine = true
            )

            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botão GUARDAR
            Button(
                onClick = {
                    if (addressText.isBlank()) {
                        errorMessage = "Address cannot be empty."
                        return@Button
                    }

                    isLoading = true
                    scope.launch {
                        try {
                            val geocoder = Geocoder(context, Locale.getDefault())

                            val results = withContext(Dispatchers.IO) {
                                try {
                                    geocoder.getFromLocationName(addressText, 1)
                                } catch (e: Exception) {
                                    null
                                }
                            }

                            if (!results.isNullOrEmpty()) {
                                val location = results[0]

                                prefs.saveHomeAddress(
                                    address = addressText,
                                    lat = location.latitude,
                                    lng = location.longitude
                                )

                                Toast.makeText(context, "Address Saved Successfully!", Toast.LENGTH_SHORT).show()

                                onNavigateBack()
                            } else {
                                errorMessage = "Address not found. Please try again."
                            }
                        } catch (e: Exception) {
                            errorMessage = "Error: ${e.message}"
                        } finally {
                            isLoading = false
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = GoldYellow),
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = BackgroundDark, modifier = Modifier.size(24.dp))
                } else {
                    Text(
                        text = "SAVE ADDRESS",
                        color = BackgroundDark,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}