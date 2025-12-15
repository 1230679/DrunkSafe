package com.example.drunksafe.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

private val BackgroundDark = Color(0xFF001524)
private val CardBlue = Color(0xFF062135)
private val Gold = Color(0xFFE0AA4E)
private val Green = Color(0xFF4CAF50)

@Composable
fun TermsPrivacyScreen(
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                backgroundColor = BackgroundDark,
                elevation = 0.dp,
                title = { Text("Terms & Privacy", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Green)
                    }
                }
            )
        },
        backgroundColor = BackgroundDark
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            PolicyCard(
                title = "Summary",
                body = """
DrunkSafe is a safety-focused navigation app. It helps you get home, contact trusted people, and access emergency actions.
This document explains what data is processed, why, and what choices you have.
Last updated: 2025-12-15.
""".trimIndent()
            )

            PolicyCard(
                title = "Data We Collect",
                body = """
Depending on how you use the app, we may process:
• Account data: email, user ID, display name.
• Profile data you optionally add: date of birth and phone number.
• Home address you store (address text and/or coordinates).
• Trusted contacts you save (names and phone numbers).
• Location data (only if you grant location permission): used to show your position and calculate routes.
• Device/app data: basic diagnostics and crash information (when available through platform services).
""".trimIndent()
            )

            PolicyCard(
                title = "Why We Use Your Data",
                body = """
We use your data to:
• Authenticate you and keep your account secure.
• Save your settings (home address, profile info, trusted contacts).
• Provide navigation and routing features.
• Let you quickly call or message trusted contacts from within the app.
• Improve stability and fix bugs.
We do not sell your personal data.
""".trimIndent()
            )

            PolicyCard(
                title = "Location and Maps",
                body = """
If you enable location access, the app can:
• Show your position on the map.
• Calculate a route to your home address or a searched destination.
You can deny location permission; the app may still work with reduced functionality.
Route calculations and map rendering may involve Google Maps services.
""".trimIndent()
            )

            PolicyCard(
                title = "Where Data Is Stored",
                body = """
Your account/profile data is stored in Firebase (Google) services used by the app (e.g., Firebase Authentication and Firestore/Database).
Trusted contacts and home address may be stored in Firebase and/or local storage on your device, depending on implementation.
Data is transmitted using standard secure connections (TLS) when supported by the platform.
""".trimIndent()
            )

            PolicyCard(
                title = "Sharing and Disclosure",
                body = """
We share data only when needed to provide the service:
• Google/Firebase: authentication and cloud data storage.
• Google Maps: mapping and routing features.
We may disclose data if required by law or to protect safety and integrity of users.
""".trimIndent()
            )

            PolicyCard(
                title = "Your Choices",
                body = """
You can:
• Edit or remove profile fields (date of birth, phone number).
• Add/remove trusted contacts.
• Change or clear your saved home address.
• Disable location permission in system settings.
• Sign out at any time.
""".trimIndent()
            )

            PolicyCard(
                title = "Data Retention",
                body = """
We keep your account data while your account remains active.
If you stop using the app, your data may remain stored until it is deleted through account deletion processes (if supported) or by the project administrators.
Locally stored data on your device remains until you clear app storage or uninstall the app.
""".trimIndent()
            )

            PolicyCard(
                title = "Safety Notice",
                body = """
DrunkSafe is not an emergency service. In an immediate danger situation, call local emergency services.
Navigation results depend on device sensors, map data, and network conditions.
""".trimIndent()
            )

            PolicyCard(
                title = "Contact",
                body = """
For questions about this policy, contact the project team through your course/institution channel.
""".trimIndent()
            )

            Spacer(Modifier.height(8.dp))
            Text(
                text = "By using the app, you acknowledge these terms and this privacy notice.",
                color = Color.Gray
            )
        }
    }
}

@Composable
private fun PolicyCard(title: String, body: String) {
    Card(backgroundColor = CardBlue, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(title, color = Gold, fontWeight = FontWeight.Bold)
            Text(body, color = Color.White)
        }
    }
}