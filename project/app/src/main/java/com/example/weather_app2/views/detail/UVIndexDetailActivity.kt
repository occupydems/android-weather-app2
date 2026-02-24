package com.example.weather_app2.views.detail

class UVIndexDetailActivity : DetailBaseActivity() {
    override fun getCardTitle(): String = "UV Index"

    override fun buildDetailContent() {
        addInfoCard("WHAT IS UV INDEX?",
            "The UV Index measures the strength of ultraviolet radiation from the sun. " +
            "Values range from 0 (no risk) to 11+ (extreme risk). Higher values mean " +
            "faster potential skin damage.")

        addInfoCard("PROTECTION LEVELS",
            "0-2: Low \u2014 No protection needed\n" +
            "3-5: Moderate \u2014 Wear sunscreen\n" +
            "6-7: High \u2014 Reduce sun exposure\n" +
            "8-10: Very High \u2014 Extra protection\n" +
            "11+: Extreme \u2014 Avoid sun exposure")

        val uvVal = weatherData?.value?.replace(Regex("[^0-9.]"), "")?.toDoubleOrNull() ?: 0.0
        val recommendation = when {
            uvVal <= 2 -> "UV levels are low. Enjoy outdoor activities safely."
            uvVal <= 5 -> "Moderate UV. Apply SPF 30+ sunscreen if outside for extended periods."
            uvVal <= 7 -> "High UV levels. Seek shade during midday hours and wear protective clothing."
            uvVal <= 10 -> "Very high UV. Minimize sun exposure between 10am-4pm."
            else -> "Extreme UV levels. Avoid sun exposure. Stay indoors if possible."
        }
        addInfoCard("CURRENT RECOMMENDATION", recommendation)
    }
}
