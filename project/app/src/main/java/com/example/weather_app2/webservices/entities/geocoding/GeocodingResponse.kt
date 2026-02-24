package com.example.weather_app2.webservices.entities.geocoding

data class GeocodingResponse(
    val name: String,
    val lat: Double,
    val lon: Double,
    val country: String,
    val state: String?
) {
    fun getDisplayName(): String {
        val parts = mutableListOf(name)
        if (!state.isNullOrEmpty()) {
            parts.add(state)
        }
        parts.add(country)
        return parts.joinToString(", ")
    }
}
