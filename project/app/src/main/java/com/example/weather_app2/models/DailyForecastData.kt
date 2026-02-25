package com.example.weather_app2.models

data class DailyForecastData(
    val day: String,
    val tempH: Int,
    val tempL: Int,
    val icon: Int,
    val pop: Int
)
