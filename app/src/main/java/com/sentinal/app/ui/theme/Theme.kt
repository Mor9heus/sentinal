package com.sentinal.app.ui.theme

import android.app.Application
import android.content.Context
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color

private val Light = lightColorScheme(
    primary = Color(0xFF2C6DF2),
    onPrimary = Color.White,
    secondary = Color(0xFF6B6F76),
    background = Color(0xFFF8F7FB),
    surface = Color.White
)

private val Dark = darkColorScheme(
    primary = Color(0xFF95B4FF),
    onPrimary = Color(0xFF0D1B3D),
    secondary = Color(0xFFB3B8C2),
    background = Color(0xFF0F1115),
    surface = Color(0xFF16181D)
)

@Composable
fun SentinalTheme(content: @Composable () -> Unit) {
    val ctx = androidx.compose.ui.platform.LocalContext.current
    val sp = remember { ctx.getSharedPreferences("profile_prefs", Context.MODE_PRIVATE) }
    val mode = sp.getString("ui_theme", "system") ?: "system"
    val isDark = when (mode) {
        "light" -> false
        "dark" -> true
        else -> androidx.compose.foundation.isSystemInDarkTheme()
    }
    MaterialTheme(colorScheme = if (isDark) Dark else Light, typography = Typography, content = content)
}
