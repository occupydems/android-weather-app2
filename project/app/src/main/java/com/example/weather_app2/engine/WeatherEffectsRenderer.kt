package com.example.weather_app2.engine

import android.content.Context
import android.util.AttributeSet
import com.oplus.vfxsdk.naive.parse.COEView
import java.util.concurrent.atomic.AtomicBoolean

class WeatherEffectsRenderer @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : COEView(context, attrs) {

    private val sceneManager = WeatherSceneManager(context)
    private val isInitialized = AtomicBoolean(false)
    private val isPaused = AtomicBoolean(false)

    private var currentWmoCode: Int = 0
    private var currentTimePeriod: Int = 2
    private var windDirection: Float = 0f
    private var windSpeed: Float = 0f

    fun initialize() {
        if (isInitialized.getAndSet(true)) return
        // COEView handles GL surface creation internally
    }

    fun setWeatherCondition(wmoCode: Int, timePeriod: Int) {
        if (wmoCode == currentWmoCode && timePeriod == currentTimePeriod) return
        currentWmoCode = wmoCode
        currentTimePeriod = timePeriod
        sceneManager.loadScene(this, wmoCode, timePeriod)
    }

    fun setWindData(directionDeg: Float, speedMs: Float) {
        windDirection = directionDeg
        windSpeed = speedMs
        sceneManager.setWindUniforms(this, directionDeg, speedMs)
    }

    fun onActivityResume() {
        isPaused.set(false)
    }

    fun onActivityPause() {
        isPaused.set(true)
    }

    fun onActivityDestroy() {
        isInitialized.set(false)
    }

    fun getSceneNameForCondition(wmoCode: Int, timePeriod: Int): String {
        return sceneManager.getSceneName(wmoCode, timePeriod)
    }
}
