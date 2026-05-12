package com.example.weathersnap.data.model

data class WeatherData(
    val cityName: String,
    val temperature: Double,
    val condition: String,
    val humidity: Int,
    val windSpeed: Double,
    val pressure: Int
)