package com.example.weathersnap.data.repository

import com.example.weathersnap.data.model.City
import com.example.weathersnap.data.model.WeatherData
import com.example.weathersnap.data.remote.api.GeocodingApi
import com.example.weathersnap.data.remote.api.WeatherApi
import com.example.weathersnap.utils.mapWeatherCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRepository @Inject constructor(
    private val geocodingApi: GeocodingApi,
    private val weatherApi: WeatherApi
) {
    // Cache for city suggestions
    private val suggestionCache = HashMap<String, List<City>>()

    suspend fun searchCities(query: String): Result<List<City>> =
        withContext(Dispatchers.IO) {
            // Return cached result if available
            suggestionCache[query]?.let { return@withContext Result.success(it) }

            try {
                val response = geocodingApi.searchCities(query)
                val cities = response.results?.map { result ->
                    City(
                        name = result.name,
                        country = result.country,
                        latitude = result.latitude,
                        longitude = result.longitude
                    )
                } ?: emptyList()

                // Store in cache
                suggestionCache[query] = cities
                Result.success(cities)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun getWeather(city: City): Result<WeatherData> =
        withContext(Dispatchers.IO) {
            try {
                val response = weatherApi.getWeather(
                    latitude = city.latitude,
                    longitude = city.longitude
                )
                val weather = WeatherData(
                    cityName = city.displayName,
                    temperature = response.current.temperature,
                    condition = mapWeatherCode(response.current.weatherCode),
                    humidity = response.current.humidity,
                    windSpeed = response.current.windSpeed,
                    pressure = response.current.pressure.toInt()
                )
                Result.success(weather)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}