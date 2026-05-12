package com.example.weathersnap.data.remote.dto

import com.google.gson.annotations.SerializedName

data class GeocodingResponse(
    @SerializedName("results") val results: List<CityResult>?
)

data class CityResult(
    @SerializedName("name") val name: String,
    @SerializedName("country") val country: String,
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double
)