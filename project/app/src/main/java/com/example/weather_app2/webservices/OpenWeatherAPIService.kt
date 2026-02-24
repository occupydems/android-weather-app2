package com.example.weather_app2.webservices

import com.example.weather_app2.webservices.entities.currentweatherdata.CurrentWeatherDataResponse
import com.example.weather_app2.webservices.entities.geocoding.GeocodingResponse
import com.example.weather_app2.webservices.entities.weatherforecastdata.WeatherForecastDataResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenWeatherAPIService {

    @GET("weather")
    suspend fun getCurrentWeatherData(
        @Query("q") city: String,
        @Query("appid") key: String,
        @Query("units") units: String
    ) : Response<CurrentWeatherDataResponse>

    @GET("onecall")
    suspend fun getWeatherForecastData(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("exclude") exclude: String,
        @Query("appid") key: String,
        @Query("units") units: String
    ) : Response<WeatherForecastDataResponse>

    @GET("https://api.openweathermap.org/geo/1.0/direct")
    suspend fun getGeocodingSuggestions(
        @Query("q") query: String,
        @Query("limit") limit: Int = 5,
        @Query("appid") key: String
    ) : Response<List<GeocodingResponse>>
}
