package com.example.drunksafe.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// --- Cores ---
private val BackgroundDark = Color(0xFF001524)
private val ItemBlue = Color(0xFF062135)
private val GoldYellow = Color(0xFFE0AA4E)
private val AlertRed = Color(0xFFD90000)
private val SearchGrey = Color(0xFFB0B0B0)
private val GreenArrow = Color(0xFF4CAF50)

@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onLogout: () -> Unit,
    onOpenProfile: () -> Unit,
    onAddressClick: () -> Unit,
    onThemeClick: () -> Unit,
    onTermsClick: () -> Unit,
    onTestEmergencyClick: () -> Unit
) {
    var showLogoutDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Settings",
                            color = Color.White,
                            fontSize = 26.sp,
                            modifier = Modifier.padding(end = 48.dp)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = GreenArrow)
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
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {

            // Botão Profile
            SettingsButton(
                text = "Profile",
                backgroundColor = ItemBlue,
                onClick = onOpenProfile
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Botão Home Address
            SettingsButton(
                text = "Home Address",
                backgroundColor = ItemBlue,
                onClick = onAddressClick
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Botão Theme
            SettingsButton(
                text = "Theme",
                backgroundColor = ItemBlue,
                onClick = onThemeClick
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Botão Test Emergency
            SettingsButton(
                text = "Test Emergency Button",
                backgroundColor = AlertRed,
                onClick = onTestEmergencyClick,
                showArrow = false
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Botão Terms
            SettingsButton(
                text = "Terms & Policy",
                backgroundColor = ItemBlue,
                onClick = onTermsClick
            )

            Spacer(modifier = Modifier.weight(1f))

            // 3. BOTÃO LOGOUT
            Button(
                onClick = { showLogoutDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
                    .padding(bottom = 16.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = GoldYellow),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "LOGOUT",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp
                )
            }
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            backgroundColor = ItemBlue,
            title = { Text("Logout", color = Color.White) },
            text = { Text("Are you sure you want to logout?", color = Color.Gray) },
            confirmButton = {
                Button(
                    onClick = { showLogoutDialog = false; onLogout() },
                    colors = ButtonDefaults.buttonColors(backgroundColor = GoldYellow)
                ) { Text("Logout", color = Color.White) }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) { Text("Cancel", color = GoldYellow) }
            }
        )
    }
}

@Composable
fun SettingsButton(
    text: String,
    backgroundColor: Color,
    onClick: () -> Unit,
    showArrow: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        colors = ButtonDefaults.buttonColors(backgroundColor = backgroundColor),
        shape = RoundedCornerShape(8.dp),
        elevation = ButtonDefaults.elevation(0.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Normal
            )
            if (showArrow) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }
    }
}