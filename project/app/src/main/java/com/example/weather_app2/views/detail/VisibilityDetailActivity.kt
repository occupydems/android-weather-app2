package com.example.weather_app2.views.detail

class VisibilityDetailActivity : DetailBaseActivity() {
    override fun getCardTitle(): String = "Visibility"

    override fun buildDetailContent() {
        val visStr = weatherData?.value?.replace(Regex("[^0-9.]"), "") ?: "0"
        val visibility = visStr.toDoubleOrNull() ?: 10.0

        val condition = when {
            visibility >= 10 -> "Excellent \u2014 Clear conditions with unrestricted visibility."
            visibility >= 5 -> "Good \u2014 Minor haze or distant clouds may be present."
            visibility >= 2 -> "Moderate \u2014 Reduced by fog, mist, rain, or pollution."
            visibility >= 1 -> "Poor \u2014 Significant obstruction. Use caution when driving."
            else -> "Very Poor \u2014 Dense fog or heavy precipitation. Travel may be hazardous."
        }
        addInfoCard("CURRENT CONDITIONS", condition)

        addInfoCard("VISIBILITY SCALE",
            "10+ km: Excellent visibility\n" +
            "5-10 km: Good visibility\n" +
            "2-5 km: Moderate visibility\n" +
            "1-2 km: Poor visibility\n" +
            "< 1 km: Very poor (fog/heavy rain)")

        addInfoCard("COMMON CAUSES OF REDUCED VISIBILITY",
            "Fog and mist, heavy rain or snow, dust storms, " +
            "smoke from wildfires, urban smog, and low cloud cover.")
    }
}
