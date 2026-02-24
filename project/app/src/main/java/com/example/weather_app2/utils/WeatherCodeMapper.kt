package com.example.weather_app2.utils

object WeatherCodeMapper {

    fun getIconCode(wmoCode: Int, isDay: Boolean): String {
        val suffix = if (isDay) "d" else "n"
        return when (wmoCode) {
            0 -> "clear_$suffix"
            1 -> "mainly_clear_$suffix"
            2 -> "partly_cloudy_$suffix"
            3 -> "overcast_$suffix"
            45, 48 -> "fog_$suffix"
            51 -> "light_drizzle_$suffix"
            53, 55 -> "drizzle_$suffix"
            56, 57 -> "freezing_drizzle_$suffix"
            61, 80 -> "light_rain_$suffix"
            63, 81 -> "rain_$suffix"
            65, 82 -> "heavy_rain_$suffix"
            66, 67 -> "sleet_$suffix"
            71, 85 -> "light_snow_$suffix"
            73, 77 -> "snow_$suffix"
            75, 86 -> "heavy_snow_$suffix"
            95, 96, 99 -> "thunderstorm_$suffix"
            else -> "partly_cloudy_$suffix"
        }
    }

    fun getDescription(wmoCode: Int): String {
        return when (wmoCode) {
            0 -> "Clear sky"
            1 -> "Mainly clear"
            2 -> "Partly cloudy"
            3 -> "Overcast"
            45 -> "Fog"
            48 -> "Depositing rime fog"
            51 -> "Light drizzle"
            53 -> "Drizzle"
            55 -> "Dense drizzle"
            56 -> "Light freezing drizzle"
            57 -> "Dense freezing drizzle"
            61 -> "Slight rain"
            63 -> "Moderate rain"
            65 -> "Heavy rain"
            66 -> "Light sleet"
            67 -> "Heavy sleet"
            71 -> "Slight snow fall"
            73 -> "Moderate snow fall"
            75 -> "Heavy snow fall"
            77 -> "Snow grains"
            80 -> "Slight rain showers"
            81 -> "Moderate rain showers"
            82 -> "Violent rain showers"
            85 -> "Slight snow showers"
            86 -> "Heavy snow showers"
            95 -> "Thunderstorm"
            96 -> "Thunderstorm with slight hail"
            99 -> "Thunderstorm with heavy hail"
            else -> "Unknown"
        }
    }

    fun getMainDescription(wmoCode: Int): String {
        return when (wmoCode) {
            0 -> "Clear"
            1 -> "Mainly Clear"
            2 -> "Partly Cloudy"
            3 -> "Overcast"
            45, 48 -> "Fog"
            51 -> "Light Drizzle"
            53, 55 -> "Drizzle"
            56, 57 -> "Freezing Drizzle"
            61, 80 -> "Light Rain"
            63, 81 -> "Rain"
            65, 82 -> "Heavy Rain"
            66, 67 -> "Sleet"
            71, 85 -> "Light Snow"
            73, 77 -> "Snow"
            75, 86 -> "Heavy Snow"
            95, 96, 99 -> "Thunderstorm"
            else -> "Partly Cloudy"
        }
    }
}
