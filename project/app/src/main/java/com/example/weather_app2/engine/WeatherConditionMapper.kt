package com.example.weather_app2.engine

import android.graphics.Color

object WeatherConditionMapper {

    const val CONDITION_CLEAR = 1
    const val CONDITION_MAINLY_CLEAR = 2
    const val CONDITION_PARTLY_CLOUDY = 3
    const val CONDITION_OVERCAST = 4
    const val CONDITION_FOG = 5
    const val CONDITION_LIGHT_DRIZZLE = 6
    const val CONDITION_DRIZZLE = 7
    const val CONDITION_FREEZING_DRIZZLE = 8
    const val CONDITION_LIGHT_RAIN = 9
    const val CONDITION_RAIN = 10
    const val CONDITION_HEAVY_RAIN = 11
    const val CONDITION_SLEET = 12
    const val CONDITION_LIGHT_SNOW = 13
    const val CONDITION_SNOW = 14
    const val CONDITION_HEAVY_SNOW = 15
    const val CONDITION_THUNDERSTORM = 16

    data class GradientColors(
        val startColor: Int,
        val centerColor: Int,
        val endColor: Int
    )

    fun wmoCodeToConditionId(wmoCode: Int): Int {
        return when (wmoCode) {
            0 -> CONDITION_CLEAR
            1 -> CONDITION_MAINLY_CLEAR
            2 -> CONDITION_PARTLY_CLOUDY
            3 -> CONDITION_OVERCAST
            45, 48 -> CONDITION_FOG
            51 -> CONDITION_LIGHT_DRIZZLE
            53, 55 -> CONDITION_DRIZZLE
            56, 57 -> CONDITION_FREEZING_DRIZZLE
            61, 80 -> CONDITION_LIGHT_RAIN
            63, 81 -> CONDITION_RAIN
            65, 82 -> CONDITION_HEAVY_RAIN
            66, 67 -> CONDITION_SLEET
            71, 85 -> CONDITION_LIGHT_SNOW
            73, 77 -> CONDITION_SNOW
            75, 86 -> CONDITION_HEAVY_SNOW
            95, 96, 99 -> CONDITION_THUNDERSTORM
            else -> CONDITION_PARTLY_CLOUDY
        }
    }

    fun conditionName(conditionId: Int): String {
        return when (conditionId) {
            CONDITION_CLEAR -> "Clear"
            CONDITION_MAINLY_CLEAR -> "Mainly Clear"
            CONDITION_PARTLY_CLOUDY -> "Partly Cloudy"
            CONDITION_OVERCAST -> "Overcast"
            CONDITION_FOG -> "Fog"
            CONDITION_LIGHT_DRIZZLE -> "Light Drizzle"
            CONDITION_DRIZZLE -> "Drizzle"
            CONDITION_FREEZING_DRIZZLE -> "Freezing Drizzle"
            CONDITION_LIGHT_RAIN -> "Light Rain"
            CONDITION_RAIN -> "Rain"
            CONDITION_HEAVY_RAIN -> "Heavy Rain"
            CONDITION_SLEET -> "Sleet"
            CONDITION_LIGHT_SNOW -> "Light Snow"
            CONDITION_SNOW -> "Snow"
            CONDITION_HEAVY_SNOW -> "Heavy Snow"
            CONDITION_THUNDERSTORM -> "Thunderstorm"
            else -> "Unknown"
        }
    }

    fun hasRain(conditionId: Int): Boolean {
        return conditionId in setOf(
            CONDITION_LIGHT_DRIZZLE,
            CONDITION_DRIZZLE,
            CONDITION_FREEZING_DRIZZLE,
            CONDITION_LIGHT_RAIN,
            CONDITION_RAIN,
            CONDITION_HEAVY_RAIN,
            CONDITION_SLEET,
            CONDITION_THUNDERSTORM
        )
    }

    fun hasSnow(conditionId: Int): Boolean {
        return conditionId in setOf(
            CONDITION_SLEET,
            CONDITION_LIGHT_SNOW,
            CONDITION_SNOW,
            CONDITION_HEAVY_SNOW
        )
    }

    fun isHeavyRain(conditionId: Int): Boolean {
        return conditionId in setOf(
            CONDITION_HEAVY_RAIN,
            CONDITION_THUNDERSTORM
        )
    }

    fun getGradientColors(conditionId: Int, isDay: Boolean): GradientColors {
        return if (isDay) getDayGradient(conditionId) else getNightGradient(conditionId)
    }

    private fun getDayGradient(conditionId: Int): GradientColors {
        return when (conditionId) {
            CONDITION_CLEAR -> GradientColors(
                Color.parseColor("#87CEEB"),
                Color.parseColor("#6BAFE0"),
                Color.parseColor("#4A90D9")
            )
            CONDITION_MAINLY_CLEAR -> GradientColors(
                Color.parseColor("#7EC8E3"),
                Color.parseColor("#62A5D4"),
                Color.parseColor("#4A8BC8")
            )
            CONDITION_PARTLY_CLOUDY -> GradientColors(
                Color.parseColor("#A0B8D0"),
                Color.parseColor("#7A9CC0"),
                Color.parseColor("#5A7FA8")
            )
            CONDITION_OVERCAST -> GradientColors(
                Color.parseColor("#3D4856"),
                Color.parseColor("#4E5D6E"),
                Color.parseColor("#5F6E7F")
            )
            CONDITION_FOG -> GradientColors(
                Color.parseColor("#A0ADB8"),
                Color.parseColor("#8D9DAD"),
                Color.parseColor("#7E8E9E")
            )
            CONDITION_LIGHT_DRIZZLE -> GradientColors(
                Color.parseColor("#3A5068"),
                Color.parseColor("#4D6478"),
                Color.parseColor("#456070")
            )
            CONDITION_DRIZZLE -> GradientColors(
                Color.parseColor("#2B3A47"),
                Color.parseColor("#4A5568"),
                Color.parseColor("#3D4F5F")
            )
            CONDITION_FREEZING_DRIZZLE -> GradientColors(
                Color.parseColor("#2F4050"),
                Color.parseColor("#405565"),
                Color.parseColor("#4A6070")
            )
            CONDITION_LIGHT_RAIN -> GradientColors(
                Color.parseColor("#2F3D4C"),
                Color.parseColor("#445568"),
                Color.parseColor("#354555")
            )
            CONDITION_RAIN -> GradientColors(
                Color.parseColor("#1F2D3A"),
                Color.parseColor("#3D4F5F"),
                Color.parseColor("#2B3A47")
            )
            CONDITION_HEAVY_RAIN -> GradientColors(
                Color.parseColor("#151F2B"),
                Color.parseColor("#2A3845"),
                Color.parseColor("#1E2B38")
            )
            CONDITION_SLEET -> GradientColors(
                Color.parseColor("#3A4858"),
                Color.parseColor("#506070"),
                Color.parseColor("#445565")
            )
            CONDITION_LIGHT_SNOW -> GradientColors(
                Color.parseColor("#C0CDD8"),
                Color.parseColor("#A8B8C8"),
                Color.parseColor("#90A3B5")
            )
            CONDITION_SNOW -> GradientColors(
                Color.parseColor("#B0BFCC"),
                Color.parseColor("#96A7B8"),
                Color.parseColor("#7A8FA3")
            )
            CONDITION_HEAVY_SNOW -> GradientColors(
                Color.parseColor("#D0D8E0"),
                Color.parseColor("#B8C5D0"),
                Color.parseColor("#9EAFC0")
            )
            CONDITION_THUNDERSTORM -> GradientColors(
                Color.parseColor("#16213E"),
                Color.parseColor("#2D2D44"),
                Color.parseColor("#1A1A2E")
            )
            else -> GradientColors(
                Color.parseColor("#A0B8D0"),
                Color.parseColor("#7A9CC0"),
                Color.parseColor("#5A7FA8")
            )
        }
    }

    private fun getNightGradient(conditionId: Int): GradientColors {
        return when (conditionId) {
            CONDITION_CLEAR -> GradientColors(
                Color.parseColor("#0D1A47"),
                Color.parseColor("#0D1A47"),
                Color.parseColor("#0D1A47")
            )
            CONDITION_MAINLY_CLEAR -> GradientColors(
                Color.parseColor("#0F1E4A"),
                Color.parseColor("#0F1E4A"),
                Color.parseColor("#0F1E4A")
            )
            CONDITION_PARTLY_CLOUDY -> GradientColors(
                Color.parseColor("#0D1A47"),
                Color.parseColor("#0D1A47"),
                Color.parseColor("#0D1A47")
            )
            CONDITION_OVERCAST -> GradientColors(
                Color.parseColor("#1A2535"),
                Color.parseColor("#1A2535"),
                Color.parseColor("#1A2535")
            )
            CONDITION_FOG -> GradientColors(
                Color.parseColor("#0D1A47"),
                Color.parseColor("#0D1A47"),
                Color.parseColor("#0D1A47")
            )
            CONDITION_LIGHT_DRIZZLE -> GradientColors(
                Color.parseColor("#4A5A6D"),
                Color.parseColor("#4A5A6D"),
                Color.parseColor("#4A5A6D")
            )
            CONDITION_DRIZZLE -> GradientColors(
                Color.parseColor("#566173"),
                Color.parseColor("#566173"),
                Color.parseColor("#566173")
            )
            CONDITION_FREEZING_DRIZZLE -> GradientColors(
                Color.parseColor("#3A4A5D"),
                Color.parseColor("#3A4A5D"),
                Color.parseColor("#3A4A5D")
            )
            CONDITION_LIGHT_RAIN -> GradientColors(
                Color.parseColor("#4D5D70"),
                Color.parseColor("#4D5D70"),
                Color.parseColor("#4D5D70")
            )
            CONDITION_RAIN -> GradientColors(
                Color.parseColor("#566173"),
                Color.parseColor("#566173"),
                Color.parseColor("#566173")
            )
            CONDITION_HEAVY_RAIN -> GradientColors(
                Color.parseColor("#3D4D60"),
                Color.parseColor("#3D4D60"),
                Color.parseColor("#3D4D60")
            )
            CONDITION_SLEET -> GradientColors(
                Color.parseColor("#455565"),
                Color.parseColor("#455565"),
                Color.parseColor("#455565")
            )
            CONDITION_LIGHT_SNOW -> GradientColors(
                Color.parseColor("#708090"),
                Color.parseColor("#708090"),
                Color.parseColor("#708090")
            )
            CONDITION_SNOW -> GradientColors(
                Color.parseColor("#80909F"),
                Color.parseColor("#80909F"),
                Color.parseColor("#80909F")
            )
            CONDITION_HEAVY_SNOW -> GradientColors(
                Color.parseColor("#90A0AF"),
                Color.parseColor("#90A0AF"),
                Color.parseColor("#90A0AF")
            )
            CONDITION_THUNDERSTORM -> GradientColors(
                Color.parseColor("#0D1A47"),
                Color.parseColor("#0D1A47"),
                Color.parseColor("#0D1A47")
            )
            else -> GradientColors(
                Color.parseColor("#0D1A47"),
                Color.parseColor("#0D1A47"),
                Color.parseColor("#0D1A47")
            )
        }
    }
}
