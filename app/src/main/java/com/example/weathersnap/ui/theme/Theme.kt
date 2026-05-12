package com.example.weathersnap.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = WeatherGreen,
    onPrimary = Color.White,
    primaryContainer = WeatherGreenDark,
    secondary = WeatherGreenDark,
    background = SurfaceLight,
    surface = CardBackground,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    error = ErrorColor
)

@Composable
fun WeatherSnapTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography(),
        content = content
    )
}