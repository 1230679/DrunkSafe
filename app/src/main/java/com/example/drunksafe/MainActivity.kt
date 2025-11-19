package com.example.drunksafe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.drunksafe.ui.AppNavHost
import com.example.drunksafe.ui.theme.DrunkSafeTheme
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)

        setContent {
            DrunkSafeTheme {
                AppNavHost { userId ->
                    // opcional: guardar userId ou iniciar serviços
                }
            }
        }
    }
}