package com.example.weather_app2.views.detail

class WindDetailActivity : DetailBaseActivity() {
    override fun getCardTitle(): String = "Wind"

    override fun buildDetailContent() {
        addInfoCard("CURRENT CONDITIONS",
            weatherData?.value ?: "No data available")

        if (weatherData?.subtitle?.isNotEmpty() == true) {
            addInfoCard("GUSTS", weatherData!!.subtitle)
        }

        addInfoCard("BEAUFORT SCALE",
            "0-1 km/h: Calm\n" +
            "2-11 km/h: Light breeze\n" +
            "12-28 km/h: Gentle to moderate\n" +
            "29-49 km/h: Fresh to strong\n" +
            "50-74 km/h: Near gale to gale\n" +
            "75-117 km/h: Storm to violent storm\n" +
            "118+ km/h: Hurricane force")

        addInfoCard("ABOUT",
            "Wind speed is measured at 10 meters above ground level. " +
            "Direction indicates where the wind is blowing FROM. " +
            "Gusts are brief increases in wind speed that can be 30-50% stronger than sustained winds.")
    }
}
