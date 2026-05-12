package com.example.weathersnap.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_reports")
data class WeatherReportEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val cityName: String,
    val temperature: Double,
    val condition: String,
    val humidity: Int,
    val windSpeed: Double,
    val pressure: Int,
    val imagePath: String,
    val notes: String,
    val originalSizeKb: Long,
    val compressedSizeKb: Long,
    val timestamp: Long = System.currentTimeMillis()
)