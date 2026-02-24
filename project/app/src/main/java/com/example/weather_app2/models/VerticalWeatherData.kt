package com.example.weather_app2.models

class VerticalWeatherData(
    val dailyForecastList: List<DailyForecastData>,
    val currentWeatherDataList: List<CurrentWeatherData>
) {

    fun getSize(): Int = dailyForecastList.size + currentWeatherDataList.size
}
