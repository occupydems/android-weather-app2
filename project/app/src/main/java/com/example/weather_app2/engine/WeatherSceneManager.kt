package com.example.weather_app2.engine

import android.content.Context
import com.oplus.vfxsdk.naive.COEEngine
import com.oplus.vfxsdk.naive.parse.COEView
import java.util.Calendar
import java.util.TimeZone

class WeatherSceneManager(private val context: Context) {

    private var coeView: COEView? = null
    private var currentScene: String? = null

    companion object {
        private const val ASSET_BUNDLE = "weather.coz2"

        private val WMO_TO_OPPO_CONDITION = mapOf(
            0 to 1,    // Clear sky -> sunny
            1 to 56,   // Mainly clear -> cloudy (light)
            2 to 58,   // Partly cloudy -> cloudy (medium)
            3 to 5,    // Overcast -> overcast
            45 to 17,  // Fog -> fog
            48 to 17,  // Depositing rime fog -> fog
            51 to 6,   // Light drizzle -> L-rain
            53 to 7,   // Moderate drizzle -> M-rain
            55 to 8,   // Dense drizzle -> H-rain
            56 to 16,  // Light freezing drizzle -> R-snow (rain+snow)
            57 to 16,  // Dense freezing drizzle -> R-snow
            61 to 6,   // Slight rain -> L-rain
            63 to 7,   // Moderate rain -> M-rain
            65 to 8,   // Heavy rain -> H-rain
            66 to 16,  // Light freezing rain -> R-snow
            67 to 16,  // Heavy freezing rain -> R-snow
            71 to 10,  // Slight snow -> L-snow
            73 to 11,  // Moderate snow -> M-snow
            75 to 12,  // Heavy snow -> H-snow
            77 to 10,  // Snow grains -> L-snow
            80 to 6,   // Slight rain showers -> L-rain
            81 to 7,   // Moderate rain showers -> M-rain
            82 to 8,   // Violent rain showers -> H-rain
            85 to 10,  // Slight snow showers -> L-snow
            86 to 12,  // Heavy snow showers -> H-snow
            95 to 9,   // Thunderstorm -> T-rain
            96 to 21,  // Thunderstorm with slight hail -> thunder
            99 to 2    // Thunderstorm with heavy hail -> hail
        )

        private val CONDITION_NAME_MAP = mapOf(
            1 to "sunny",
            2 to "hail",
            5 to "overcast",
            6 to "L-rain",
            7 to "M-rain",
            8 to "H-rain",
            9 to "T-rain",
            10 to "L-snow",
            11 to "M-snow",
            12 to "H-snow",
            14 to "dust",
            15 to "haze",
            16 to "R-snow",
            17 to "fog",
            18 to "rainhail",
            19 to "wind",
            20 to "storm",
            21 to "thunder",
            55 to "m_sunny",
            56 to "cloudy",
            58 to "cloudy",
            101 to "cloudy"
        )

        private val TIME_PERIOD_MAP = mapOf(
            0 to "dawn",
            1 to "morning",
            2 to "noon",
            3 to "afternoon",
            4 to "sunset",
            5 to "even",
            6 to "night"
        )

        private val CONDITION_HAS_FULL_PERIODS = setOf(1, 55)
        private val CONDITION_HAS_DAWN_SUNSET = setOf(101)
        private val CONDITION_HAS_MORNING_PERIODS = setOf(5, 56, 58)
    }

    fun getTimePeriod(hourOfDay: Int, timezoneId: String? = null): Int {
        val hour = if (hourOfDay >= 0) hourOfDay else {
            val cal = Calendar.getInstance(
                if (timezoneId != null) TimeZone.getTimeZone(timezoneId) else TimeZone.getDefault()
            )
            cal.get(Calendar.HOUR_OF_DAY)
        }
        return when (hour) {
            in 5..6 -> 0     // dawn
            in 7..9 -> 1     // morning
            in 10..13 -> 2   // noon
            in 14..16 -> 3   // afternoon
            in 17..18 -> 4   // sunset
            in 19..20 -> 5   // evening
            else -> 6        // night
        }
    }

    fun getSceneName(wmoCode: Int, timePeriod: Int): String {
        val oppoCondition = WMO_TO_OPPO_CONDITION[wmoCode] ?: 1
        val conditionName = CONDITION_NAME_MAP[oppoCondition] ?: "sunny"

        val effectivePeriod = when {
            CONDITION_HAS_FULL_PERIODS.contains(oppoCondition) -> timePeriod
            CONDITION_HAS_DAWN_SUNSET.contains(oppoCondition) -> {
                when (timePeriod) {
                    0 -> 1  // dawn -> dawn-s variant
                    4 -> 4  // sunset -> sunset-s variant
                    else -> return "0_0_empty"
                }
            }
            CONDITION_HAS_MORNING_PERIODS.contains(oppoCondition) -> {
                when (timePeriod) {
                    0, 1 -> 1       // morning
                    2, 3 -> 2       // noon
                    4 -> 4          // sunset
                    else -> 6       // night
                }
            }
            else -> {
                if (timePeriod <= 4) 2 else 6
            }
        }

        val periodName = TIME_PERIOD_MAP[effectivePeriod] ?: "noon"

        val prefix = when (oppoCondition) {
            101 -> "${oppoCondition}_${effectivePeriod}_${conditionName}-${periodName}-s"
            55 -> "${oppoCondition}_${effectivePeriod}_${conditionName}-${periodName}"
            else -> "${oppoCondition}_${effectivePeriod}_${conditionName}-${periodName}"
        }

        return prefix
    }

    fun getAllSceneNames(): List<String> {
        return WMO_TO_OPPO_CONDITION.flatMap { (wmoCode, _) ->
            (0..6).mapNotNull { period ->
                val name = getSceneName(wmoCode, period)
                if (name != "0_0_empty") name else null
            }
        }.distinct().sorted()
    }

    fun loadScene(coeView: COEView, wmoCode: Int, timePeriod: Int) {
        val sceneName = getSceneName(wmoCode, timePeriod)
        if (sceneName == currentScene) return
        currentScene = sceneName
        coeView.load(sceneName)
    }

    fun setWindUniforms(coeView: COEView, windDirectionDeg: Float, windSpeedMs: Float) {
        val windForce = (windSpeedMs / 5f).coerceIn(0f, 10f)
        // TODO: Set uniforms on rain effect0 sub-shader via engine
        // engine.nativeSetUniforms(handle, "windDirec", windDirectionDeg)
        // engine.nativeSetUniforms(handle, "windForce", windForce)
    }
}
