package com.example.weather_app2.models

data class GeoSearchResult(
    val name: String,
    val admin1: String?,
    val country: String?,
    val latitude: Double,
    val longitude: Double
) {
    fun displayString(): String {
        val parts = mutableListOf(name)
        if (!admin1.isNullOrEmpty()) parts.add(admin1)
        if (!country.isNullOrEmpty()) parts.add(country)
        return parts.joinToString(", ")
    }
}
