package com.tashrif.jarvis.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val JarvisBackground = Color(0xFF0A0E14)
val JarvisSurface = Color(0xFF131A24)
val JarvisAccent = Color(0xFF3DDCFF)
val JarvisAccentDim = Color(0xFF1E5A66)
val JarvisTextPrimary = Color(0xFFE6F7FF)
val JarvisTextSecondary = Color(0xFF7A8B99)

private val JarvisColorScheme = darkColorScheme(
    primary = JarvisAccent,
    secondary = JarvisAccentDim,
    background = JarvisBackground,
    surface = JarvisSurface,
    onBackground = JarvisTextPrimary,
    onSurface = JarvisTextPrimary,
)

@Composable
fun JarvisTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = JarvisColorScheme,
        content = content
    )
}
