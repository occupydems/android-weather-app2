package com.example.weather_app2.views.detail

class FeelsLikeDetailActivity : DetailBaseActivity() {
    override fun getCardTitle(): String = "Feels Like"

    override fun buildDetailContent() {
        val feelsLikeStr = weatherData?.value?.replace(Regex("[^0-9.-]"), "") ?: "0"
        val feelsLike = feelsLikeStr.toDoubleOrNull() ?: 20.0

        val explanation = when {
            feelsLike < 0 -> "Wind chill makes it feel significantly colder than the actual temperature. " +
                "Exposed skin is at risk of frostbite."
            feelsLike < 10 -> "Cool conditions. The combination of wind and temperature makes it feel chilly. " +
                "Dress in layers."
            feelsLike in 10.0..25.0 -> "Comfortable conditions. The apparent temperature is pleasant for outdoor activities."
            feelsLike in 25.0..35.0 -> "Warm conditions. Humidity may make it feel warmer than the actual temperature. " +
                "Stay hydrated."
            else -> "Hot and possibly humid. Heat index is elevated. Limit strenuous outdoor activity " +
                "and seek shade or air conditioning."
        }
        addInfoCard("HOW IT FEELS", explanation)

        addInfoCard("ABOUT",
            "\"Feels Like\" combines the actual air temperature with wind speed and humidity " +
            "to estimate how the weather actually feels on your skin. In cold weather, wind " +
            "chill makes it feel colder. In warm weather, humidity makes it feel hotter.")
    }
}
