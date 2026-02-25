package com.example.weather_app2.utils

import com.example.weather_app2.R

object UiUtils {

    fun getWeatherIcon(iconTag: String): Int {
        return when(iconTag) {
            "clear_d", "clear_morning", "clear_afternoon" -> R.drawable.ic_weather_sunny
            "clear_dawn" -> R.drawable.ic_weather_sunrise
            "clear_sunset" -> R.drawable.ic_weather_sunset
            "clear_n", "clear_evening" -> R.drawable.ic_weather_clear_night
            "mainly_clear_d", "mainly_clear_morning" -> R.drawable.ic_weather_sunny
            "mainly_clear_dawn" -> R.drawable.ic_weather_sunrise
            "mainly_clear_sunset" -> R.drawable.ic_weather_sunset
            "mainly_clear_n" -> R.drawable.ic_weather_clear_night
            "partly_cloudy_d", "partly_cloudy_morning" -> R.drawable.few_clouds_day
            "partly_cloudy_dawn" -> R.drawable.ic_weather_sunrise
            "partly_cloudy_sunset" -> R.drawable.ic_weather_sunset
            "partly_cloudy_n" -> R.drawable.ic_weather_cloudy_night
            "overcast_d", "overcast_morning", "overcast_sunset", "overcast_dawn" -> R.drawable.broken_clouds
            "overcast_n" -> R.drawable.ic_weather_cloudy_night
            "fog_d", "fog_n" -> R.drawable.mist
            "light_drizzle_d" -> R.drawable.shower_rain
            "light_drizzle_n" -> R.drawable.ic_weather_rain_night
            "drizzle_d" -> R.drawable.shower_rain
            "drizzle_n" -> R.drawable.ic_weather_rain_night
            "freezing_drizzle_d" -> R.drawable.shower_rain
            "freezing_drizzle_n" -> R.drawable.ic_weather_rain_night
            "light_rain_d" -> R.drawable.shower_rain
            "light_rain_n" -> R.drawable.ic_weather_rain_night
            "rain_d" -> R.drawable.rain_day
            "rain_n" -> R.drawable.ic_weather_rain_night
            "heavy_rain_d" -> R.drawable.rain_day
            "heavy_rain_n" -> R.drawable.ic_weather_rain_night
            "sleet_d", "sleet_n" -> R.drawable.ic_weather_snow
            "light_snow_d", "light_snow_n" -> R.drawable.ic_weather_snow
            "snow_d", "snow_n" -> R.drawable.ic_weather_snow
            "heavy_snow_d", "heavy_snow_n" -> R.drawable.ic_weather_snow
            "thunderstorm_d" -> R.drawable.thunderstorm
            "thunderstorm_n" -> R.drawable.ic_weather_storm_night
            "hail_d" -> R.drawable.thunderstorm
            "hail_n" -> R.drawable.ic_weather_storm_night
            else -> R.drawable.few_clouds_day
        }
    }

    fun getWeatherForecastBackground(iconTag: String): Int {
        return when(iconTag) {
            "clear_d" -> R.drawable.gradient_background_clear_day
            "clear_n" -> R.drawable.gradient_background_clear_night
            "mainly_clear_d" -> R.drawable.gradient_background_mainly_clear_day
            "mainly_clear_n" -> R.drawable.gradient_background_mainly_clear_night
            "partly_cloudy_d" -> R.drawable.gradient_background_few_clouds_day
            "partly_cloudy_n" -> R.drawable.gradient_background_few_clouds_night
            "overcast_d" -> R.drawable.gradient_background_overcast_day
            "overcast_n" -> R.drawable.gradient_background_overcast_night
            "fog_d" -> R.drawable.gradient_background_mist_day
            "fog_n" -> R.drawable.gradient_background_mist_night
            "light_drizzle_d" -> R.drawable.gradient_background_light_drizzle_day
            "light_drizzle_n" -> R.drawable.gradient_background_light_drizzle_night
            "drizzle_d" -> R.drawable.gradient_background_drizzle_day
            "drizzle_n" -> R.drawable.gradient_background_drizzle_night
            "freezing_drizzle_d" -> R.drawable.gradient_background_freezing_drizzle_day
            "freezing_drizzle_n" -> R.drawable.gradient_background_freezing_drizzle_night
            "light_rain_d" -> R.drawable.gradient_background_light_rain_day
            "light_rain_n" -> R.drawable.gradient_background_light_rain_night
            "rain_d" -> R.drawable.gradient_background_rain_day
            "rain_n" -> R.drawable.gradient_background_rain_night
            "heavy_rain_d" -> R.drawable.gradient_background_heavy_rain_day
            "heavy_rain_n" -> R.drawable.gradient_background_heavy_rain_night
            "sleet_d" -> R.drawable.gradient_background_sleet_day
            "sleet_n" -> R.drawable.gradient_background_sleet_night
            "light_snow_d" -> R.drawable.gradient_background_light_snow_day
            "light_snow_n" -> R.drawable.gradient_background_light_snow_night
            "snow_d" -> R.drawable.gradient_background_snow_day
            "snow_n" -> R.drawable.gradient_background_snow_night
            "heavy_snow_d" -> R.drawable.gradient_background_heavy_snow_day
            "heavy_snow_n" -> R.drawable.gradient_background_heavy_snow_night
            "thunderstorm_d" -> R.drawable.gradient_background_thunderstorm
            "thunderstorm_n" -> R.drawable.gradient_background_thunderstorm_night
            "clear_dawn" -> R.drawable.gradient_background_clear_dawn
            "clear_morning" -> R.drawable.gradient_background_clear_morning
            "clear_afternoon" -> R.drawable.gradient_background_clear_afternoon
            "clear_sunset" -> R.drawable.gradient_background_clear_sunset
            "clear_evening" -> R.drawable.gradient_background_clear_evening
            "mainly_clear_morning" -> R.drawable.gradient_background_mainly_clear_morning
            "mainly_clear_sunset" -> R.drawable.gradient_background_mainly_clear_sunset
            "partly_cloudy_morning" -> R.drawable.gradient_background_partly_cloudy_morning
            "partly_cloudy_sunset" -> R.drawable.gradient_background_partly_cloudy_sunset
            "overcast_morning" -> R.drawable.gradient_background_overcast_morning
            "overcast_sunset" -> R.drawable.gradient_background_overcast_sunset
            "hail_d" -> R.drawable.gradient_background_hail_day
            "hail_n" -> R.drawable.gradient_background_hail_night
            else -> R.drawable.gradient_background_clear_day
        }
    }

    fun getCityShortcutBackground(iconTag: String): Int {
        return when(iconTag) {
            "clear_d" -> R.drawable.gradient_city_shortcut_clear_day
            "clear_n" -> R.drawable.gradient_city_shortcut_clear_night
            "mainly_clear_d" -> R.drawable.gradient_city_shortcut_mainly_clear_day
            "mainly_clear_n" -> R.drawable.gradient_city_shortcut_mainly_clear_night
            "partly_cloudy_d" -> R.drawable.gradient_city_shortcut_few_clouds_day
            "partly_cloudy_n" -> R.drawable.gradient_city_shortcut_few_clouds_night
            "overcast_d" -> R.drawable.gradient_city_shortcut_overcast_day
            "overcast_n" -> R.drawable.gradient_city_shortcut_overcast_night
            "fog_d" -> R.drawable.gradient_city_shortcut_mist_day
            "fog_n" -> R.drawable.gradient_city_shortcut_mist_night
            "light_drizzle_d" -> R.drawable.gradient_city_shortcut_light_drizzle_day
            "light_drizzle_n" -> R.drawable.gradient_city_shortcut_light_drizzle_night
            "drizzle_d" -> R.drawable.gradient_city_shortcut_drizzle
            "drizzle_n" -> R.drawable.gradient_city_shortcut_drizzle_night
            "freezing_drizzle_d" -> R.drawable.gradient_city_shortcut_freezing_drizzle_day
            "freezing_drizzle_n" -> R.drawable.gradient_city_shortcut_freezing_drizzle_night
            "light_rain_d" -> R.drawable.gradient_city_shortcut_light_rain_day
            "light_rain_n" -> R.drawable.gradient_city_shortcut_light_rain_night
            "rain_d" -> R.drawable.gradient_city_shortcut_rain_day
            "rain_n" -> R.drawable.gradient_city_shortcut_rain_night
            "heavy_rain_d" -> R.drawable.gradient_city_shortcut_heavy_rain_day
            "heavy_rain_n" -> R.drawable.gradient_city_shortcut_heavy_rain_night
            "sleet_d" -> R.drawable.gradient_city_shortcut_sleet_day
            "sleet_n" -> R.drawable.gradient_city_shortcut_sleet_night
            "light_snow_d" -> R.drawable.gradient_city_shortcut_light_snow_day
            "light_snow_n" -> R.drawable.gradient_city_shortcut_light_snow_night
            "snow_d" -> R.drawable.gradient_city_shortcut_snow_day
            "snow_n" -> R.drawable.gradient_city_shortcut_snow_night
            "heavy_snow_d" -> R.drawable.gradient_city_shortcut_heavy_snow_day
            "heavy_snow_n" -> R.drawable.gradient_city_shortcut_heavy_snow_night
            "thunderstorm_d" -> R.drawable.gradient_city_shortcut_thunderstorm
            "thunderstorm_n" -> R.drawable.gradient_city_shortcut_thunderstorm_night
            "clear_dawn", "clear_evening" -> R.drawable.gradient_city_shortcut_clear_night
            "clear_morning", "clear_afternoon", "clear_sunset" -> R.drawable.gradient_city_shortcut_clear_day
            "mainly_clear_morning" -> R.drawable.gradient_city_shortcut_mainly_clear_day
            "mainly_clear_sunset" -> R.drawable.gradient_city_shortcut_mainly_clear_night
            "partly_cloudy_morning" -> R.drawable.gradient_city_shortcut_few_clouds_day
            "partly_cloudy_sunset" -> R.drawable.gradient_city_shortcut_few_clouds_night
            "overcast_morning" -> R.drawable.gradient_city_shortcut_overcast_day
            "overcast_sunset" -> R.drawable.gradient_city_shortcut_overcast_night
            "hail_d" -> R.drawable.gradient_city_shortcut_thunderstorm
            "hail_n" -> R.drawable.gradient_city_shortcut_thunderstorm_night
            else -> R.drawable.gradient_city_shortcut_clear_day
        }
    }

    fun getStatusBarColor(iconTag: String): Int {
        return when(iconTag) {
            "clear_d" -> R.color.clear_day_status_bar
            "clear_n" -> R.color.clear_night_status_bar
            "mainly_clear_d" -> R.color.mainly_clear_day_status_bar
            "mainly_clear_n" -> R.color.mainly_clear_night_status_bar
            "partly_cloudy_d" -> R.color.partly_cloudy_day_status_bar
            "partly_cloudy_n" -> R.color.partly_cloudy_night_status_bar
            "overcast_d" -> R.color.overcast_day_status_bar
            "overcast_n" -> R.color.overcast_night_status_bar
            "fog_d" -> R.color.fog_day_status_bar
            "fog_n" -> R.color.fog_night_status_bar
            "light_drizzle_d" -> R.color.light_drizzle_day_status_bar
            "light_drizzle_n" -> R.color.light_drizzle_night_status_bar
            "drizzle_d" -> R.color.drizzle_day_status_bar
            "drizzle_n" -> R.color.drizzle_night_status_bar
            "freezing_drizzle_d" -> R.color.freezing_drizzle_day_status_bar
            "freezing_drizzle_n" -> R.color.freezing_drizzle_night_status_bar
            "light_rain_d" -> R.color.light_rain_day_status_bar
            "light_rain_n" -> R.color.light_rain_night_status_bar
            "rain_d" -> R.color.rain_day_status_bar
            "rain_n" -> R.color.rain_night_status_bar
            "heavy_rain_d" -> R.color.heavy_rain_day_status_bar
            "heavy_rain_n" -> R.color.heavy_rain_night_status_bar
            "sleet_d" -> R.color.sleet_day_status_bar
            "sleet_n" -> R.color.sleet_night_status_bar
            "light_snow_d" -> R.color.light_snow_day_status_bar
            "light_snow_n" -> R.color.light_snow_night_status_bar
            "snow_d" -> R.color.snow_day_status_bar
            "snow_n" -> R.color.snow_night_status_bar
            "heavy_snow_d" -> R.color.heavy_snow_day_status_bar
            "heavy_snow_n" -> R.color.heavy_snow_night_status_bar
            "thunderstorm_d" -> R.color.thunderstorm_status_bar
            "thunderstorm_n" -> R.color.thunderstorm_night_status_bar
            "clear_dawn" -> R.color.clear_dawn_status_bar
            "clear_morning" -> R.color.clear_morning_status_bar
            "clear_afternoon" -> R.color.clear_afternoon_status_bar
            "clear_sunset" -> R.color.clear_sunset_status_bar
            "clear_evening" -> R.color.clear_evening_status_bar
            "mainly_clear_morning" -> R.color.mainly_clear_morning_status_bar
            "mainly_clear_sunset" -> R.color.mainly_clear_sunset_status_bar
            "partly_cloudy_morning" -> R.color.partly_cloudy_morning_status_bar
            "partly_cloudy_sunset" -> R.color.partly_cloudy_sunset_status_bar
            "overcast_morning" -> R.color.overcast_morning_status_bar
            "overcast_sunset" -> R.color.overcast_sunset_status_bar
            "hail_d" -> R.color.hail_day_status_bar
            "hail_n" -> R.color.hail_night_status_bar
            else -> R.color.clear_day_status_bar
        }
    }

    fun getHeaderColor(iconTag: String): Int {
        return when(iconTag) {
            "clear_d" -> R.color.clear_day_header
            "clear_n" -> R.color.clear_night_header
            "mainly_clear_d" -> R.color.mainly_clear_day_header
            "mainly_clear_n" -> R.color.mainly_clear_night_header
            "partly_cloudy_d" -> R.color.partly_cloudy_day_header
            "partly_cloudy_n" -> R.color.partly_cloudy_night_header
            "overcast_d" -> R.color.overcast_day_header
            "overcast_n" -> R.color.overcast_night_header
            "fog_d" -> R.color.fog_day_header
            "fog_n" -> R.color.fog_night_header
            "light_drizzle_d" -> R.color.light_drizzle_day_header
            "light_drizzle_n" -> R.color.light_drizzle_night_header
            "drizzle_d" -> R.color.drizzle_day_header
            "drizzle_n" -> R.color.drizzle_night_header
            "freezing_drizzle_d" -> R.color.freezing_drizzle_day_header
            "freezing_drizzle_n" -> R.color.freezing_drizzle_night_header
            "light_rain_d" -> R.color.light_rain_day_header
            "light_rain_n" -> R.color.light_rain_night_header
            "rain_d" -> R.color.rain_day_header
            "rain_n" -> R.color.rain_night_header
            "heavy_rain_d" -> R.color.heavy_rain_day_header
            "heavy_rain_n" -> R.color.heavy_rain_night_header
            "sleet_d" -> R.color.sleet_day_header
            "sleet_n" -> R.color.sleet_night_header
            "light_snow_d" -> R.color.light_snow_day_header
            "light_snow_n" -> R.color.light_snow_night_header
            "snow_d" -> R.color.snow_day_header
            "snow_n" -> R.color.snow_night_header
            "heavy_snow_d" -> R.color.heavy_snow_day_header
            "heavy_snow_n" -> R.color.heavy_snow_night_header
            "thunderstorm_d" -> R.color.thunderstorm_header
            "thunderstorm_n" -> R.color.thunderstorm_night_header
            "clear_dawn" -> R.color.clear_dawn_header
            "clear_morning" -> R.color.clear_morning_header
            "clear_afternoon" -> R.color.clear_afternoon_header
            "clear_sunset" -> R.color.clear_sunset_header
            "clear_evening" -> R.color.clear_evening_header
            "mainly_clear_morning" -> R.color.mainly_clear_morning_header
            "mainly_clear_sunset" -> R.color.mainly_clear_sunset_header
            "partly_cloudy_morning" -> R.color.partly_cloudy_morning_header
            "partly_cloudy_sunset" -> R.color.partly_cloudy_sunset_header
            "overcast_morning" -> R.color.overcast_morning_header
            "overcast_sunset" -> R.color.overcast_sunset_header
            "hail_d" -> R.color.hail_day_header
            "hail_n" -> R.color.hail_night_header
            else -> R.color.clear_day_header
        }
    }

    fun getCardBackground(iconTag: String): Int {
        return when(iconTag) {
            "clear_d" -> R.drawable.rounded_card_clear_day
            "clear_n" -> R.drawable.rounded_card_clear_night
            "mainly_clear_d" -> R.drawable.rounded_card_mainly_clear_day
            "mainly_clear_n" -> R.drawable.rounded_card_mainly_clear_night
            "partly_cloudy_d" -> R.drawable.rounded_card_partly_cloudy_day
            "partly_cloudy_n" -> R.drawable.rounded_card_partly_cloudy_night
            "overcast_d" -> R.drawable.rounded_card_overcast_day
            "overcast_n" -> R.drawable.rounded_card_overcast_night
            "fog_d" -> R.drawable.rounded_card_mist
            "fog_n" -> R.drawable.rounded_card_mist_night
            "light_drizzle_d" -> R.drawable.rounded_card_light_drizzle_day
            "light_drizzle_n" -> R.drawable.rounded_card_light_drizzle_night
            "drizzle_d" -> R.drawable.rounded_card_rain
            "drizzle_n" -> R.drawable.rounded_card_rain_night
            "freezing_drizzle_d" -> R.drawable.rounded_card_freezing_drizzle_day
            "freezing_drizzle_n" -> R.drawable.rounded_card_freezing_drizzle_night
            "light_rain_d" -> R.drawable.rounded_card_light_rain_day
            "light_rain_n" -> R.drawable.rounded_card_light_rain_night
            "rain_d" -> R.drawable.rounded_card_rain
            "rain_n" -> R.drawable.rounded_card_rain_night
            "heavy_rain_d" -> R.drawable.rounded_card_heavy_rain_day
            "heavy_rain_n" -> R.drawable.rounded_card_heavy_rain_night
            "sleet_d" -> R.drawable.rounded_card_sleet_day
            "sleet_n" -> R.drawable.rounded_card_sleet_night
            "light_snow_d" -> R.drawable.rounded_card_light_snow_day
            "light_snow_n" -> R.drawable.rounded_card_light_snow_night
            "snow_d" -> R.drawable.rounded_card_snow
            "snow_n" -> R.drawable.rounded_card_snow_night
            "heavy_snow_d" -> R.drawable.rounded_card_heavy_snow_day
            "heavy_snow_n" -> R.drawable.rounded_card_heavy_snow_night
            "thunderstorm_d" -> R.drawable.rounded_card_thunderstorm
            "thunderstorm_n" -> R.drawable.rounded_card_thunderstorm_night
            "clear_dawn" -> R.drawable.rounded_card_clear_dawn
            "clear_morning" -> R.drawable.rounded_card_clear_morning
            "clear_afternoon" -> R.drawable.rounded_card_clear_afternoon
            "clear_sunset" -> R.drawable.rounded_card_clear_sunset
            "clear_evening" -> R.drawable.rounded_card_clear_evening
            "mainly_clear_morning" -> R.drawable.rounded_card_mainly_clear_morning
            "mainly_clear_sunset" -> R.drawable.rounded_card_mainly_clear_sunset
            "partly_cloudy_morning" -> R.drawable.rounded_card_partly_cloudy_morning
            "partly_cloudy_sunset" -> R.drawable.rounded_card_partly_cloudy_sunset
            "overcast_morning" -> R.drawable.rounded_card_overcast_morning
            "overcast_sunset" -> R.drawable.rounded_card_overcast_sunset
            "hail_d" -> R.drawable.rounded_card_hail_day
            "hail_n" -> R.drawable.rounded_card_hail_night
            else -> R.drawable.rounded_card_clear_day
        }
    }
}
