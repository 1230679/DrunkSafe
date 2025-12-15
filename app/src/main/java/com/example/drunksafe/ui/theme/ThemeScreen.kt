package com.example.drunksafe.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.drunksafe.data.ThemeMode
import com.example.drunksafe.data.ThemePreferences

private val BackgroundDark = Color(0xFF001524)
private val CardBlue = Color(0xFF062135)
private val Gold = Color(0xFFE0AA4E)
private val Green = Color(0xFF4CAF50)

@Composable
fun ThemeScreen(
    onNavigateBack: () -> Unit,
    onThemeChanged: (ThemeMode) -> Unit
) {
    val context = LocalContext.current
    val prefs = remember { ThemePreferences(context) }

    var selected by remember { mutableStateOf(prefs.getThemeMode()) }

    fun choose(mode: ThemeMode) {
        selected = mode
        prefs.setThemeMode(mode)
        onThemeChanged(mode)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                backgroundColor = BackgroundDark,
                elevation = 0.dp,
                title = { Text("Theme", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Green)
                    }
                }
            )
        },
        backgroundColor = BackgroundDark
    ) { padding ->
        Card(
            backgroundColor = CardBlue,
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Choose appearance", color = Gold, fontWeight = FontWeight.Bold)

                ThemeOptionRow(
                    title = "System default",
                    selected = selected == ThemeMode.SYSTEM,
                    onClick = { choose(ThemeMode.SYSTEM) }
                )
                ThemeOptionRow(
                    title = "Light",
                    selected = selected == ThemeMode.LIGHT,
                    onClick = { choose(ThemeMode.LIGHT) }
                )
                ThemeOptionRow(
                    title = "Dark",
                    selected = selected == ThemeMode.DARK,
                    onClick = { choose(ThemeMode.DARK) }
                )

                Text("Applies immediately.", color = Color.Gray)
            }
        }
    }
}

@Composable
private fun ThemeOptionRow(
    title: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(title, color = Color.White)
        RadioButton(selected = selected, onClick = onClick)
    }
}