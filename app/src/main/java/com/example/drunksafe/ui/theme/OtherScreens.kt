package com.example.drunksafe.ui

import androidx.compose. foundation.layout.*
import androidx.compose. foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose. material.icons.filled.ExitToApp
import androidx.compose.runtime.*
import androidx. compose.ui. Alignment
import androidx. compose.ui. Modifier
import androidx. compose.ui.graphics.Color
import androidx.compose.ui. text.font.FontWeight
import androidx.compose.ui.unit. dp
import androidx. compose.ui.unit.sp

private val GoldYellow = Color(0xFFD4A84B)
private val DarkBlue = Color(0xFF0A1929)
private val AlertRed = Color(0xFFD94A4A)

@Composable
fun HomeScreen(
    onNavigateToContacts: () -> Unit = {},
    onNavigateToEmergency: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    var showLogoutDialog by remember { mutableStateOf(false) }

    Surface(modifier = Modifier.fillMaxSize(), color = DarkBlue) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16. dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header com botão Logout
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = { showLogoutDialog = true }) {
                    Icon(
                        Icons. Default.ExitToApp,
                        contentDescription = "Logout",
                        tint = Color. White
                    )
                }
            }

            Spacer(modifier = Modifier.weight(0.3f))

            // Título
            Text(
                "DrunkSafe",
                color = GoldYellow,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8. dp))

            Text(
                "Stay Safe, Stay Connected",
                color = Color.Gray,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier. weight(0.3f))

            // Botão Trusted Contacts
            Button(
                onClick = onNavigateToContacts,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = GoldYellow),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Trusted Contacts", color = DarkBlue, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botão Emergency
            Button(
                onClick = onNavigateToEmergency,
                modifier = Modifier
                    . fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = AlertRed),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("🆘 Emergency", color = Color.White, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.weight(0.5f))
        }
    }

    // Dialog de confirmação de Logout
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            backgroundColor = DarkBlue,
            title = { Text("Logout", color = Color.White) },
            text = { Text("Are you sure you want to logout? ", color = Color.Gray) },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = AlertRed)
                ) {
                    Text("Logout", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel", color = GoldYellow)
                }
            }
        )
    }
}

@Composable
fun NavigationScreen() {
    Text("Navigation - integrar mapas + ExternalApiClient")
}