package com.example.drunksafe.data

import android.content.Context

enum class ThemeMode { SYSTEM, LIGHT, DARK }

class ThemePreferences(context: Context) {
    private val prefs = context.getSharedPreferences("drunksafe_prefs", Context.MODE_PRIVATE)

    fun getThemeMode(): ThemeMode {
        val raw = prefs.getString("theme_mode", ThemeMode.SYSTEM.name) ?: ThemeMode.SYSTEM.name
        return runCatching { ThemeMode.valueOf(raw) }.getOrDefault(ThemeMode.SYSTEM)
    }

    fun setThemeMode(mode: ThemeMode) {
        prefs.edit().putString("theme_mode", mode.name).apply()
    }
}
