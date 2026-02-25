package com.example.weather_app2.webservices.entities.openmeteo

data class OpenMeteoForecastResponse(
    val latitude: Double,
    val longitude: Double,
    val utc_offset_seconds: Int,
    val timezone: String,
    val elevation: Double?,
    val current: OpenMeteoCurrent?,
    val hourly: OpenMeteoHourly?,
    val daily: OpenMeteoDaily?
)

data class OpenMeteoCurrent(
    val time: String,
    val temperature_2m: Double,
    val relative_humidity_2m: Int?,
    val apparent_temperature: Double?,
    val weather_code: Int,
    val surface_pressure: Double?,
    val wind_speed_10m: Double?,
    val wind_direction_10m: Int?,
    val wind_gusts_10m: Double?,
    val visibility: Double?,
    val uv_index: Double?,
    val is_day: Int?
)

data class OpenMeteoHourly(
    val time: List<String>,
    val temperature_2m: List<Double>,
    val weather_code: List<Int>,
    val is_day: List<Int>?
)

data class OpenMeteoDaily(
    val time: List<String>,
    val weather_code: List<Int>,
    val temperature_2m_max: List<Double>,
    val temperature_2m_min: List<Double>,
    val sunrise: List<String>?,
    val sunset: List<String>?,
    val uv_index_max: List<Double>?,
    val precipitation_probability_max: List<Int>?
)
