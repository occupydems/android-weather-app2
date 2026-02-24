package com.example.weather_app2.utils

import java.util.*

object ClockUtils {

    var deviceTimezone = TimeZone.getDefault().rawOffset / 1000

    private fun getTimeZoneFromOffsetMs(offsetMs: Long): TimeZone {
        val totalMinutes = (offsetMs / 1000 / 60).toInt()
        val sign = if (totalMinutes >= 0) "+" else "-"
        val absMinutes = Math.abs(totalMinutes)
        val hours = absMinutes / 60
        val minutes = absMinutes % 60
        val tzId = String.format("GMT%s%02d:%02d", sign, hours, minutes)
        return TimeZone.getTimeZone(tzId)
    }

    fun getDayFromUnixTimestamp(
        unixTimeStamp: Long,
        timeZone: Long,
        deviceTimezone: Long
    ): String {
        val tz = getTimeZoneFromOffsetMs(timeZone)
        val calendar = Calendar.getInstance(tz, Locale.ENGLISH)
        calendar.timeInMillis = unixTimeStamp
        val todayCal = Calendar.getInstance(tz, Locale.ENGLISH)
        val isToday = calendar.get(Calendar.DAY_OF_YEAR) == todayCal.get(Calendar.DAY_OF_YEAR) &&
                      calendar.get(Calendar.YEAR) == todayCal.get(Calendar.YEAR)
        if (isToday) return "Today"
        return when(val day = calendar.get(Calendar.DAY_OF_WEEK)){
            1 -> "Sun"
            2 -> "Mon"
            3 -> "Tue"
            4 -> "Wed"
            5 -> "Thu"
            6 -> "Fri"
            7 -> "Sat"
            else -> "Day: $day"
        }
    }

    private fun get12HourClockPeriod(hour: Int): String{
        return when(hour){
            in 0..11, 24 -> "AM"
            in 12..23 -> "PM"
            else -> hour.toString()
        }
    }

    fun getTimeFromUnixTimestamp(
        unixTimeStamp: Long,
        timeZone: Long,
        deviceTimezone: Long,
        minutesMode: Boolean,
        clockPeriodMode: Boolean
    ): String{
        val tz = getTimeZoneFromOffsetMs(timeZone)
        val calendar = Calendar.getInstance(tz, Locale.ENGLISH)
        calendar.timeInMillis = unixTimeStamp

        var hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minutes = calendar.get(Calendar.MINUTE)
        if (clockPeriodMode) {
            val period = get12HourClockPeriod(hour)
            hour = when {
                hour == 0 -> 12
                hour > 12 -> hour - 12
                else -> hour
            }
            if (minutesMode) {
                return "${getClockString(hour)}:${getClockString(minutes)} $period"
            }
            return "$hour$period"
        }
        if (minutesMode) {
            return "${getClockString(hour)}:${getClockString(minutes)}"
        }
        return "$hour"
    }

    private fun getClockString(timeUnit: Int): String{
        if(timeUnit<10){
            return "0$timeUnit"
        }
        return "$timeUnit"
    }
}
