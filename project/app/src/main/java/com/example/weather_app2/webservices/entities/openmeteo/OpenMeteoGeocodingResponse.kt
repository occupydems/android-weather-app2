package com.example.weather_app2.webservices.entities.openmeteo

data class OpenMeteoGeocodingResponse(
    val results: List<OpenMeteoGeocodingResult>?
)

data class OpenMeteoGeocodingResult(
    val id: Int,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val country: String?,
    val country_code: String?,
    val admin1: String?,
    val timezone: String?
)
