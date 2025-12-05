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
import com.google.firebase.storage.FirebaseStorage

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar Firebase
        FirebaseApp.initializeApp(this)

        // Testar conexões
        testFirebaseConnection()

        setContent {
            DrunkSafeTheme {
                AppNavHost { userId ->
                    // Opcional: guardar userId ou iniciar serviços
                    Log.d("MainActivity", "User ID recebido: $userId")
                }
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