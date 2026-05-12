package com.example.weathersnap.utils

fun mapWeatherCode(code: Int): String = when (code) {
    0 -> "Clear sky"
    1, 2, 3 -> "Partly cloudy"
    45, 48 -> "Foggy"
    51, 53, 55 -> "Drizzle"
    61, 63, 65 -> "Rainy"
    71, 73, 75 -> "Snowy"
    80, 81, 82 -> "Rain showers"
    95 -> "Thunderstorm"
    96, 99 -> "Thunderstorm with hail"
    else -> "Unknown"
}