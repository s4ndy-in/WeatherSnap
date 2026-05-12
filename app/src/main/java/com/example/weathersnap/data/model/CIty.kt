package com.example.weathersnap.data.model

data class City(
    val name: String,
    val country: String,
    val latitude: Double,
    val longitude: Double,
    val displayName: String = "$name, $country"
)