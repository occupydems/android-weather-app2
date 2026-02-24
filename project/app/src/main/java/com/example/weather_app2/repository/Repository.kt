package com.example.weather_app2.repository

import com.example.weather_app2.database.entities.CityShortcut
import com.example.weather_app2.webservices.entities.currentweatherdata.CurrentWeatherDataResponse
import com.example.weather_app2.webservices.entities.weatherforecastdata.WeatherForecastDataResponse
import retrofit2.Response

interface Repository {

    suspend fun getCurrentWeatherDataResponse(
        forecastLocation: String,
        unitsSystem: String
    ) : Response<CurrentWeatherDataResponse>

    suspend fun getCurrentWeatherByCoords(
        lat: Double,
        lon: Double,
        cityName: String,
        countryCode: String,
        unitsSystem: String
    ) : Response<CurrentWeatherDataResponse>

    suspend fun getWeatherForecastDataResponse(
        lat: Double,
        lon: Double,
        exclude: String,
        unitsSystem: String
    ) : Response<WeatherForecastDataResponse>

    suspend fun getGeocodingSuggestions(
        query: String
    ) : List<String>

    suspend fun addCityShortcutToDatabase(cityShortcut: CityShortcut)

    suspend fun deleteCityShortcutFromDatabase(cityShortcut: CityShortcut)

}
