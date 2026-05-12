package com.example.weathersnap.di

import android.content.Context
import androidx.room.Room
import com.example.weathersnap.data.local.database.WeatherDatabase
import com.example.weathersnap.data.local.dao.WeatherReportDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): WeatherDatabase =
        Room.databaseBuilder(
            context,
            WeatherDatabase::class.java,
            "weather_snap_db"
        ).build()

    @Provides
    @Singleton
    fun provideWeatherReportDao(database: WeatherDatabase): WeatherReportDao =
        database.weatherReportDao()
}