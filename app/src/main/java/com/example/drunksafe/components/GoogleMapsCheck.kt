package com.example.drunksafe.components

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight


/**
 * A Composable that checks for the presence of the Google Maps application on the device.
 *
 * This component runs a check immediately upon composition. It displays an [AlertDialog] informing
 * the user of the requirement and providing a direct link to the Play Store to install it.
 *
 */
@Composable
fun GoogleMapsCheckDialog() {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        showDialog = true
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text(text = "Google Maps Required", fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    "DrunkSafe relies on Google Maps to guide you home safely.\n\n" +
                            "Please ensure that the official Google Maps app is installed on your device for the application to work correctly."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.google.android.apps.maps"))
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            // Fallback para browser se a Play Store falhar
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.maps"))
                            context.startActivity(intent)
                        }
                    }
                ) {
                    Text("INSTALL NOW")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("I UNDERSTAND")
                }
            }
        )
    }
}

