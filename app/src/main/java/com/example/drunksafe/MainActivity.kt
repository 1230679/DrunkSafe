package com.example.drunksafe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.drunksafe.ui.AppNavHost
import com.example.drunksafe.ui.theme.DrunkSafeTheme
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.util.Log
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.drunksafe.data.ThemeMode
import com.example.drunksafe.data.ThemePreferences
import com.google.firebase.storage.FirebaseStorage

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar Firebase
        FirebaseApp.initializeApp(this)

        // Testar conexões
        testFirebaseConnection()

        setContent {
            val prefs = remember { ThemePreferences(this) }
            var themeMode by remember { mutableStateOf(prefs.getThemeMode()) }

            val dark = when (themeMode) {
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
            }

            DrunkSafeTheme(
                darkTheme = dark,
                dynamicColor = false
            ) {
                AppNavHost(
                    onLoggedIn = { userId -> Log.d("MainActivity", "User ID recebido: $userId") },
                    onThemeChanged = { mode -> themeMode = mode }
                )
            }
        }
    }

    private fun testFirebaseConnection() {
        try {
            val auth = FirebaseAuth.getInstance()
            val firestore = FirebaseFirestore.getInstance()
            val storage = FirebaseStorage.getInstance()

            Log.d("Firebase", "Auth inicializado: ${auth.app.name}")
            Log.d("Firebase", "Firestore inicializado: ${firestore.app.name}")
            Log.d("Firebase", "Storage inicializado: ${storage.app.name}")

        } catch (e: Exception) {
            Log.e("Firebase", " Erro ao inicializar Firebase: ${e.message}")
        }
    }
}