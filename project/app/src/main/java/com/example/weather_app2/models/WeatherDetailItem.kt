package com.example.weather_app2.models

import java.io.Serializable

data class WeatherDetailItem(
    val header: String,
    val value: String,
    val subtitle: String = "",
    val type: DetailCardType = DetailCardType.STANDARD,
    val sunriseTimestamp: Long = 0,
    val sunsetTimestamp: Long = 0,
    val currentTimestamp: Long = 0,
    val timezoneOffset: Int = 0
) : Serializable

enum class DetailCardType : Serializable {
    STANDARD,
    WIND,
    HUMIDITY,
    PRESSURE,
    VISIBILITY,
    FEELS_LIKE,
    PRECIPITATION,
    UV_INDEX,
    SUNRISE_SUNSET
}
