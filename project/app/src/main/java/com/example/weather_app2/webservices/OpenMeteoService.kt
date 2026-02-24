package com.example.weather_app2.webservices

import com.example.weather_app2.webservices.entities.openmeteo.OpenMeteoForecastResponse
import com.example.weather_app2.webservices.entities.openmeteo.OpenMeteoGeocodingResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenMeteoService {

    @GET("https://api.open-meteo.com/v1/forecast")
    suspend fun getForecast(
        @Query("latitude") lat: Double,
        @Query("longitude") lon: Double,
        @Query("current") current: String = "temperature_2m,relative_humidity_2m,apparent_temperature,weather_code,surface_pressure,wind_speed_10m,wind_direction_10m,wind_gusts_10m,visibility,uv_index,is_day",
        @Query("hourly") hourly: String = "temperature_2m,weather_code,is_day",
        @Query("daily") daily: String = "weather_code,temperature_2m_max,temperature_2m_min,sunrise,sunset,uv_index_max",
        @Query("temperature_unit") tempUnit: String = "celsius",
        @Query("wind_speed_unit") windUnit: String = "kmh",
        @Query("precipitation_unit") precipUnit: String = "mm",
        @Query("timezone") timezone: String = "auto",
        @Query("forecast_days") forecastDays: Int = 10
    ): Response<OpenMeteoForecastResponse>

    @GET("https://geocoding-api.open-meteo.com/v1/search")
    suspend fun searchCities(
        @Query("name") name: String,
        @Query("count") count: Int = 5,
        @Query("language") language: String = "en"
    ): Response<OpenMeteoGeocodingResponse>
}
