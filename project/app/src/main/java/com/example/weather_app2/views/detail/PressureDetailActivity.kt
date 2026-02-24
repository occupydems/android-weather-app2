package com.example.weather_app2.views.detail

class PressureDetailActivity : DetailBaseActivity() {
    override fun getCardTitle(): String = "Pressure"

    override fun buildDetailContent() {
        val pressureStr = weatherData?.value?.replace(Regex("[^0-9.]"), "") ?: "0"
        val pressure = pressureStr.toDoubleOrNull() ?: 0.0
        val isInHg = weatherData?.value?.contains("inHg") == true

        val trend = if (isInHg) {
            when {
                pressure > 30.27 -> "High pressure \u2014 Generally associated with clear, calm weather."
                pressure > 29.92 -> "Above average \u2014 Fair weather is likely."
                pressure > 29.53 -> "Below average \u2014 Clouds or precipitation possible."
                else -> "Low pressure \u2014 Stormy or unsettled weather is likely."
            }
        } else {
            when {
                pressure > 1025 -> "High pressure \u2014 Generally associated with clear, calm weather."
                pressure > 1013 -> "Above average \u2014 Fair weather is likely."
                pressure > 1000 -> "Below average \u2014 Clouds or precipitation possible."
                else -> "Low pressure \u2014 Stormy or unsettled weather is likely."
            }
        }
        addInfoCard("PRESSURE TREND", trend)

        if (isInHg) {
            addInfoCard("REFERENCE VALUES",
                "Standard sea-level: 29.92 inHg\n" +
                "High pressure: > 30.27 inHg\n" +
                "Low pressure: < 29.53 inHg\n" +
                "Hurricane center: < 28.35 inHg")
        } else {
            addInfoCard("REFERENCE VALUES",
                "Standard sea-level: 1013.25 hPa\n" +
                "High pressure: > 1025 hPa\n" +
                "Low pressure: < 1000 hPa\n" +
                "Hurricane center: < 960 hPa")
        }

        addInfoCard("ABOUT",
            "Atmospheric pressure is the weight of air above you. " +
            "Rising pressure usually signals improving weather, while falling " +
            "pressure often precedes storms. Rapid changes indicate significant weather shifts.")
    }
}
