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
            99 -> "hail_$suffix"
            95, 96 -> "thunderstorm_$suffix"
            else -> "partly_cloudy_$suffix"
        }
    }

    fun getIconCode(wmoCode: Int, isDay: Boolean, hourOfDay: Int): String {
        return when (wmoCode) {
            0 -> {
                when (hourOfDay) {
                    in 5..6 -> "clear_dawn"
                    in 7..9 -> "clear_morning"
                    in 10..13 -> "clear_d"
                    in 14..16 -> "clear_afternoon"
                    in 17..18 -> "clear_sunset"
                    in 19..20 -> "clear_evening"
                    else -> "clear_n"
                }
            }
            1 -> {
                when (hourOfDay) {
                    in 5..9 -> "mainly_clear_morning"
                    in 10..16 -> "mainly_clear_d"
                    in 17..18 -> "mainly_clear_sunset"
                    else -> "mainly_clear_n"
                }
            }
            2 -> {
                when (hourOfDay) {
                    in 5..9 -> "partly_cloudy_morning"
                    in 10..16 -> "partly_cloudy_d"
                    in 17..18 -> "partly_cloudy_sunset"
                    else -> "partly_cloudy_n"
                }
            }
            3 -> {
                when (hourOfDay) {
                    in 5..9 -> "overcast_morning"
                    in 10..16 -> "overcast_d"
                    in 17..18 -> "overcast_sunset"
                    else -> "overcast_n"
                }
            }
            99 -> if (isDay) "hail_d" else "hail_n"
            else -> getIconCode(wmoCode, isDay)
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
