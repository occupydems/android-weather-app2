package com.example.weather_app2.views.detail

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class SunriseSunsetDetailActivity : DetailBaseActivity() {
    override fun getCardTitle(): String = "Sunrise & Sunset"

    override fun buildDetailContent() {
        val data = weatherData ?: return

        val sdf = SimpleDateFormat("h:mm a", Locale.getDefault()).apply {
            val offsetMs = data.timezoneOffset * 1000L
            timeZone = TimeZone.getTimeZone("GMT").apply { rawOffset = offsetMs.toInt() }
        }

        if (data.sunriseTimestamp > 0) {
            addInfoCard("SUNRISE", sdf.format(Date(data.sunriseTimestamp * 1000)))
        }
        if (data.sunsetTimestamp > 0) {
            addInfoCard("SUNSET", sdf.format(Date(data.sunsetTimestamp * 1000)))
        }

        if (data.sunriseTimestamp > 0 && data.sunsetTimestamp > 0) {
            val daylightSeconds = data.sunsetTimestamp - data.sunriseTimestamp
            val hours = daylightSeconds / 3600
            val minutes = (daylightSeconds % 3600) / 60
            addInfoCard("DAYLIGHT", "${hours}h ${minutes}m of daylight today")

            val nightSeconds = 86400 - daylightSeconds
            val nightHours = nightSeconds / 3600
            val nightMinutes = (nightSeconds % 3600) / 60
            addInfoCard("NIGHTTIME", "${nightHours}h ${nightMinutes}m of darkness")
        }

        addInfoCard("ABOUT",
            "Sunrise and sunset times are calculated based on your location's latitude, " +
            "longitude, and the current date. Times shown are in local time.")
    }
}
