package com.example.weather_app2.views.detail

class PressureDetailActivity : DetailBaseActivity() {
    override fun getCardTitle(): String = "Pressure"

    override fun buildDetailContent() {
        val pressureStr = weatherData?.value?.replace(Regex("[^0-9.]"), "") ?: "0"
        val pressure = pressureStr.toDoubleOrNull() ?: 1013.0

        val trend = when {
            pressure > 1025 -> "High pressure \u2014 Generally associated with clear, calm weather."
            pressure > 1013 -> "Above average \u2014 Fair weather is likely."
            pressure > 1000 -> "Below average \u2014 Clouds or precipitation possible."
            else -> "Low pressure \u2014 Stormy or unsettled weather is likely."
        }
        addInfoCard("PRESSURE TREND", trend)

        addInfoCard("REFERENCE VALUES",
            "Standard sea-level: 1013.25 hPa\n" +
            "High pressure: > 1025 hPa\n" +
            "Low pressure: < 1000 hPa\n" +
            "Hurricane center: < 960 hPa")

        addInfoCard("ABOUT",
            "Atmospheric pressure is the weight of air above you. " +
            "Rising pressure usually signals improving weather, while falling " +
            "pressure often precedes storms. Rapid changes indicate significant weather shifts.")
    }
}
