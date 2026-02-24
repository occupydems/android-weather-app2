package com.example.weather_app2.views.detail

class HumidityDetailActivity : DetailBaseActivity() {
    override fun getCardTitle(): String = "Humidity"

    override fun buildDetailContent() {
        val humidityStr = weatherData?.value?.replace(Regex("[^0-9]"), "") ?: "0"
        val humidity = humidityStr.toIntOrNull() ?: 0

        val comfortLevel = when {
            humidity < 25 -> "Very Dry \u2014 May cause skin irritation and static electricity."
            humidity < 40 -> "Dry \u2014 Comfortable for most people. Consider a humidifier indoors."
            humidity < 60 -> "Comfortable \u2014 Ideal humidity range for health and comfort."
            humidity < 70 -> "Slightly Humid \u2014 May feel sticky. Mold risk increases."
            humidity < 85 -> "Humid \u2014 Uncomfortable. Air conditioning recommended."
            else -> "Very Humid \u2014 Oppressive. Heat-related illness risk increases."
        }
        addInfoCard("COMFORT LEVEL", comfortLevel)

        if (weatherData?.subtitle?.isNotEmpty() == true) {
            addInfoCard("DEW POINT", weatherData!!.subtitle)
        }

        addInfoCard("ABOUT",
            "Relative humidity measures how much moisture is in the air compared to " +
            "the maximum it can hold at the current temperature. The dew point is the " +
            "temperature at which moisture begins to condense from the air.")
    }
}
