package com.example.drunksafe.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.drunksafe.R


// EXEMPLO DE IMPLEMENTAÇÃO PODEM SEMPRE MUDAR QUEM FOR RESPONSÁVEL POR ISTO!!!

// --- Definições de Cores ---
private val GoldYellow = Color(0xFFD4A84B)
private val DarkBlue = Color(0xFF0A1929)
private val AlertRed = Color(0xFFD94A4A)

@Composable
fun ProfileScreen(
    userName: String = "Utilizador DrunkSafe",
    homeAddress: String = "Rua Trøjborg, 8200 Aarhus, DK",
    onLogout: () -> Unit,
    onNavigateBack: () -> Unit
) {
    var showLogoutDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile", color = Color.White) },
                backgroundColor = DarkBlue,
                contentColor = Color.White,
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                }
            )
        },
        backgroundColor = DarkBlue
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- Ícone / Avatar ---
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(GoldYellow.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.Person,
                    contentDescription = "User Avatar",
                    modifier = Modifier.size(60.dp),
                    tint = DarkBlue
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- Nome do Utilizador ---
            Text(
                userName,
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(32.dp))

            // --- Cartão da Morada de Casa ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = DarkBlue,
                elevation = 4.dp,
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Filled.Home,
                        contentDescription = "Home Address",
                        tint = GoldYellow,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("Home Address", color = Color.Gray, fontSize = 14.sp)
                        Text(
                            homeAddress,
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f)) // Empurra o botão para baixo

            // --- Botão de Logout ---
            Button(
                onClick = { showLogoutDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = AlertRed),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Logout, contentDescription = "Logout", tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("LOGOUT", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }

    // --- Dialog de Confirmação de Logout ---
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            backgroundColor = DarkBlue,
            title = { Text("Logout", color = Color.White) },
            text = { Text("Are you sure you want to log out?", color = Color.Gray) },
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