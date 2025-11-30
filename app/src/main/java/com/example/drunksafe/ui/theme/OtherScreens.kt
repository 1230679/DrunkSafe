package com.example. drunksafe.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material. Button
import androidx.compose.material.ButtonDefaults
import androidx. compose.material.Text
import androidx.compose.runtime. Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui. unit.dp

private val GoldYellow = Color(0xFFD4A84B)
private val DarkBlue = Color(0xFF0A1929)
private val AlertRed = Color(0xFFD94A4A)

@Composable
fun HomeScreen(
    onNavigateToContacts: () -> Unit = {},
    onNavigateToEmergency: () -> Unit = {}
) {
    Column(
        modifier = Modifier. fillMaxSize(). padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Home - DrunkSafe", color = Color.White)

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onNavigateToContacts,
            colors = ButtonDefaults.buttonColors(backgroundColor = GoldYellow)
        ) {
            Text("Trusted Contacts", color = DarkBlue)
        }

        Spacer(modifier = Modifier. height(16.dp))

        Button(
            onClick = onNavigateToEmergency,
            colors = ButtonDefaults.buttonColors(backgroundColor = AlertRed)
        ) {
            Text("Emergency", color = Color. White)
        }
    }
}

@Composable fun NavigationScreen() { Text("Navigation - integrar mapas + ExternalApiClient")}