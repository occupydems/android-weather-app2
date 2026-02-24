package com.example.weather_app2.webservices.entities.currentweatherdata

data class Wind(
    val deg: Int,
    val speed: Double,
    val gust: Double = 0.0
)
