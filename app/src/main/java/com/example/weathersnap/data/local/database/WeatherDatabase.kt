package com.example.weathersnap.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.weathersnap.data.local.dao.WeatherReportDao
import com.example.weathersnap.data.local.entity.WeatherReportEntity

@Database(entities = [WeatherReportEntity::class], version = 1, exportSchema = false)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun weatherReportDao(): WeatherReportDao
}
