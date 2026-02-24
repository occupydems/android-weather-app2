package com.example.weather_app2.views.detail

class PrecipitationDetailActivity : DetailBaseActivity() {
    override fun getCardTitle(): String = "Precipitation"

    override fun buildDetailContent() {
        addInfoCard("WHAT IS PRECIPITATION?",
            "Precipitation includes all forms of water that fall from clouds: " +
            "rain, snow, sleet, freezing rain, and hail.")

        addInfoCard("INTENSITY GUIDE",
            "Light: < 2.5 mm/hr (drizzle)\n" +
            "Moderate: 2.5-7.5 mm/hr\n" +
            "Heavy: 7.5-50 mm/hr\n" +
            "Violent: > 50 mm/hr (flash flood risk)")

        addInfoCard("ABOUT",
            "Precipitation data shows the amount of water equivalent that has fallen or " +
            "is expected. Snow accumulation is typically 10-15x the water equivalent amount.")
    }
}
