package com.example. drunksafe.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose. ui.Alignment
import androidx.compose. ui.Modifier
import androidx.compose. ui.unit.dp

@Composable
fun HomeScreen(onNavigateToContacts: () -> Unit = {}) {
    Column(
        modifier = Modifier. fillMaxSize(). padding(16.dp),
        horizontalAlignment = Alignment. CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Home - DrunkSafe")
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onNavigateToContacts) {
            Text("Trusted Contacts")
        }
    }
}

@Composable fun NavigationScreen() { Text("Navigation - integrar mapas + ExternalApiClient") }
@Composable fun EmergencyScreen() { Text("Emergency - notificar contactos / live location") }