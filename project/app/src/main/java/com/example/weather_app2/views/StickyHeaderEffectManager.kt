package com.example.weather_app2.views

import android.view.View
import java.util.concurrent.atomic.AtomicBoolean

class StickyHeaderEffectManager {

    private var effectView: CardWeatherEffectView? = null
    private var currentPinnedHeader: View? = null
    private var currentWeatherCode: String = ""
    private val isEnabled = AtomicBoolean(true)

    fun onHeaderPinned(headerView: View, weatherCode: String) {
        currentWeatherCode = weatherCode

        if (currentPinnedHeader == headerView) {
            effectView?.updateWeatherCondition(weatherCode)
            return
        }

        if (effectView == null) {
            effectView = CardWeatherEffectView(headerView.context)
        }

        if (currentPinnedHeader != null && effectView != null) {
            effectView!!.transferToNextHeader(headerView)
        } else {
            effectView?.attachToStickyHeader(headerView, weatherCode)
        }

        currentPinnedHeader = headerView
    }

    fun onHeaderUnpinned() {
        effectView?.detachFromHeader()
        currentPinnedHeader = null
    }

    fun setEnabled(enabled: Boolean) {
        isEnabled.set(enabled)
        if (enabled) {
            effectView?.resumeAnimation()
        } else {
            effectView?.pauseAnimation()
        }
    }

    fun updateWeatherCondition(weatherCode: String) {
        currentWeatherCode = weatherCode
        effectView?.updateWeatherCondition(weatherCode)
    }

    fun getPinnedHeader(): View? = currentPinnedHeader

    fun getCurrentWeatherCode(): String = currentWeatherCode

    fun isEnabled(): Boolean = isEnabled.get()

    fun cleanup() {
        onHeaderUnpinned()
        effectView = null
        currentPinnedHeader = null
        currentWeatherCode = ""
    }
}
