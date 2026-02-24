package com.example.weather_app2.utils

import com.example.weather_app2.database.entities.CityShortcut
import com.example.weather_app2.webservices.entities.currentweatherdata.*
import com.example.weather_app2.webservices.entities.weatherforecastdata.*

object DummyWeatherData {

    const val DUMMY_PREFIX = "DUMMY:"
    const val DUMMY_ID_START = 3000

    data class DummyCondition(
        val iconCode: String,
        val label: String,
        val description: String,
        val temp: Int,
        val highTemp: Int,
        val lowTemp: Int,
        val feelsLike: Int,
        val humidity: Int,
        val visibility: Int,
        val clouds: Int,
        val windSpeed: Double,
        val pressure: Int
    )

    val conditions = listOf(
        DummyCondition("clear_d", "Clear", "Clear sky", 28, 31, 22, 30, 35, 10000, 5, 8.0, 1015),
        DummyCondition("mainly_clear_d", "Mostly Clear", "Mainly clear", 26, 29, 20, 27, 40, 10000, 15, 10.0, 1014),
        DummyCondition("partly_cloudy_d", "Partly Cloudy", "Partly cloudy", 22, 25, 17, 22, 50, 9000, 45, 12.0, 1013),
        DummyCondition("overcast_d", "Overcast", "Overcast clouds", 18, 20, 14, 17, 65, 7000, 90, 15.0, 1010),
        DummyCondition("fog_d", "Fog", "Fog", 13, 16, 10, 12, 95, 500, 80, 3.0, 1018),
        DummyCondition("light_drizzle_d", "Light Drizzle", "Light drizzle", 14, 17, 11, 13, 85, 5000, 85, 8.0, 1011),
        DummyCondition("drizzle_d", "Drizzle", "Drizzle", 13, 16, 10, 12, 88, 4000, 90, 10.0, 1010),
        DummyCondition("freezing_drizzle_d", "Freezing Drizzle", "Freezing drizzle", -1, 1, -4, -3, 90, 3000, 92, 7.0, 1020),
        DummyCondition("light_rain_d", "Light Rain", "Light rain", 16, 19, 12, 15, 78, 6000, 75, 14.0, 1008),
        DummyCondition("rain_d", "Rain", "Moderate rain", 13, 16, 10, 11, 82, 4000, 85, 18.0, 1005),
        DummyCondition("heavy_rain_d", "Heavy Rain", "Heavy rain", 11, 14, 8, 9, 90, 2000, 95, 25.0, 1000),
        DummyCondition("sleet_d", "Sleet", "Sleet", 1, 3, -2, -1, 88, 3000, 90, 12.0, 1015),
        DummyCondition("light_snow_d", "Light Snow", "Light snow", -2, 0, -5, -4, 80, 4000, 85, 8.0, 1020),
        DummyCondition("snow_d", "Snow", "Moderate snow", -4, -1, -8, -6, 85, 2000, 90, 12.0, 1018),
        DummyCondition("heavy_snow_d", "Heavy Snow", "Heavy snow", -7, -4, -12, -10, 90, 800, 95, 20.0, 1015),
        DummyCondition("thunderstorm_d", "Thunderstorm", "Thunderstorm", 24, 28, 19, 25, 75, 3000, 80, 30.0, 998)
    )

    fun isDummyId(id: Int): Boolean {
        return id >= DUMMY_ID_START && id < DUMMY_ID_START + conditions.size
    }

    fun isDummyLocation(locationName: String): Boolean {
        return locationName.startsWith(DUMMY_PREFIX)
    }

    fun getIconCodeFromDummyLocation(locationName: String): String {
        return locationName.removePrefix(DUMMY_PREFIX)
    }

    fun getDummyCityShortcuts(): List<CityShortcut> {
        return conditions.mapIndexed { index, condition ->
            CityShortcut(
                id = DUMMY_ID_START + index,
                cityName = condition.label,
                localTime = "",
                temp = condition.temp,
                icon = condition.iconCode,
                highTemp = condition.highTemp,
                lowTemp = condition.lowTemp
            )
        }
    }

    fun generateCurrentWeatherData(iconCode: String): CurrentWeatherDataResponse {
        val condition = conditions.find { it.iconCode == iconCode } ?: conditions[0]
        val nowSeconds = (System.currentTimeMillis() / 1000).toInt()
        val sunriseOffset = -6 * 3600
        val sunsetOffset = 6 * 3600

        return CurrentWeatherDataResponse(
            base = "dummy",
            clouds = Clouds(condition.clouds),
            cod = 200,
            coord = Coord(0.0, 0.0),
            dt = nowSeconds,
            id = 0,
            main = Main(
                feels_like = condition.feelsLike.toDouble(),
                humidity = condition.humidity,
                pressure = condition.pressure,
                temp = condition.temp.toDouble(),
                temp_max = condition.highTemp.toDouble(),
                temp_min = condition.lowTemp.toDouble()
            ),
            name = condition.label,
            sys = Sys(
                country = "XX",
                id = 0,
                sunrise = nowSeconds + sunriseOffset,
                sunset = nowSeconds + sunsetOffset,
                type = 0
            ),
            timezone = 0,
            visibility = condition.visibility,
            weather = listOf(
                Weather(
                    description = condition.description,
                    icon = condition.iconCode,
                    id = 0,
                    main = condition.label
                )
            ),
            wind = Wind(
                deg = 180,
                speed = condition.windSpeed,
                gust = condition.windSpeed * 1.4
            ),
            uv_index = 5.0
        )
    }

    fun generateForecastData(iconCode: String): WeatherForecastDataResponse {
        val condition = conditions.find { it.iconCode == iconCode } ?: conditions[0]
        val nowSeconds = (System.currentTimeMillis() / 1000).toInt()
        val sunriseOffset = -6 * 3600
        val sunsetOffset = 6 * 3600

        val hourlyList = mutableListOf<Hourly>()
        for (i in 0 until 48) {
            val hourOffset = i * 3600
            val tempVariation = condition.temp + (Math.sin(i * 0.5) * 3).toInt()
            hourlyList.add(
                Hourly(
                    clouds = condition.clouds,
                    dew_point = condition.temp - 5.0,
                    dt = nowSeconds + hourOffset,
                    feels_like = condition.feelsLike.toDouble(),
                    humidity = condition.humidity,
                    pop = if (condition.iconCode.contains("rain") || condition.iconCode.contains("drizzle") || condition.iconCode.contains("thunderstorm")) 0.8 else 0.1,
                    pressure = condition.pressure,
                    rain = Rain(0.0),
                    temp = tempVariation.toDouble(),
                    uvi = 5.0,
                    visibility = condition.visibility,
                    weather = listOf(
                        WeatherXX(
                            description = condition.description,
                            icon = condition.iconCode,
                            id = 0,
                            main = condition.label
                        )
                    ),
                    wind_deg = 180,
                    wind_gust = condition.windSpeed * 1.4,
                    wind_speed = condition.windSpeed
                )
            )
        }

        val dailyList = mutableListOf<Daily>()
        for (i in 0 until 10) {
            val dayOffset = i * 86400
            val tempVariation = (Math.sin(i * 0.8) * 2).toInt()
            dailyList.add(
                Daily(
                    clouds = condition.clouds,
                    dew_point = condition.temp - 5.0,
                    dt = nowSeconds + dayOffset,
                    feels_like = FeelsLike(
                        day = condition.feelsLike.toDouble(),
                        eve = (condition.feelsLike - 2).toDouble(),
                        morn = (condition.feelsLike - 3).toDouble(),
                        night = (condition.feelsLike - 5).toDouble()
                    ),
                    humidity = condition.humidity,
                    moon_phase = 0.5,
                    moonrise = nowSeconds + dayOffset + sunriseOffset - 3600,
                    moonset = nowSeconds + dayOffset + sunsetOffset + 3600,
                    pop = 0.3,
                    pressure = condition.pressure,
                    rain = 0.0,
                    sunrise = nowSeconds + dayOffset + sunriseOffset,
                    sunset = nowSeconds + dayOffset + sunsetOffset,
                    temp = Temp(
                        day = condition.temp.toDouble(),
                        eve = (condition.temp - 2).toDouble(),
                        max = (condition.highTemp + tempVariation).toDouble(),
                        min = (condition.lowTemp + tempVariation).toDouble(),
                        morn = (condition.temp - 3).toDouble(),
                        night = (condition.temp - 5).toDouble()
                    ),
                    uvi = 5.0,
                    weather = listOf(
                        WeatherX(
                            description = condition.description,
                            icon = condition.iconCode,
                            id = 0,
                            main = condition.label
                        )
                    ),
                    wind_deg = 180,
                    wind_gust = condition.windSpeed * 1.4,
                    wind_speed = condition.windSpeed
                )
            )
        }

        return WeatherForecastDataResponse(
            daily = dailyList,
            hourly = hourlyList,
            lat = 0.0,
            lon = 0.0,
            timezone = "UTC",
            timezone_offset = 0
        )
    }
}
