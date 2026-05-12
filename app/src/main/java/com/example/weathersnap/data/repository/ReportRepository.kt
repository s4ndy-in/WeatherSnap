package com.example.weathersnap.data.repository

import com.example.weathersnap.data.local.dao.WeatherReportDao
import com.example.weathersnap.data.local.entity.WeatherReportEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReportRepository @Inject constructor(
    private val dao: WeatherReportDao
) {
    fun getAllReports(): Flow<List<WeatherReportEntity>> = dao.getAllReports()

    suspend fun saveReport(report: WeatherReportEntity) =
        withContext(Dispatchers.IO) {
            dao.insertReport(report)
        }
}