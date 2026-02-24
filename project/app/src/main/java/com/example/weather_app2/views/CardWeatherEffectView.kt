package com.example.weather_app2.views

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.widget.FrameLayout
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import java.util.concurrent.atomic.AtomicBoolean

class CardWeatherEffectView(context: Context) : FrameLayout(context) {

    private val lottieView = LottieAnimationView(context).apply {
        repeatCount = LottieDrawable.INFINITE
        scaleType = android.widget.ImageView.ScaleType.CENTER_CROP
    }

    private var currentHeaderView: View? = null
    private val isAttached = AtomicBoolean(false)
    private var currentWeatherCode: String = ""
    private var isEnabled = true

    init {
        addView(lottieView, LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        ))
        setupLayout()
    }

    private fun setupLayout() {
        val density = resources.displayMetrics.density
        val marginTopPx = (-25 * density).toInt()
        val marginBottomPx = (-7 * density).toInt()
        val heightPx = (32 * density).toInt()
        val elevationPx = -1 * density

        layoutParams = ViewGroup.MarginLayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            heightPx
        ).apply {
            topMargin = marginTopPx
            bottomMargin = marginBottomPx
        }

        elevation = elevationPx
    }

    fun attachToStickyHeader(headerView: View, weatherCode: String = "") {
        if (!isAttached.compareAndSet(false, true)) {
            return
        }

        currentWeatherCode = weatherCode
        currentHeaderView = headerView

        val parent = headerView.parent as? ViewGroup
        if (parent != null) {
            val lp = when (parent) {
                is FrameLayout -> FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    (32 * resources.displayMetrics.density).toInt()
                )
                else -> ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    (32 * resources.displayMetrics.density).toInt()
                )
            }
            val indexOfHeader = parent.indexOfChild(headerView)
            if (indexOfHeader >= 0 && indexOfHeader + 1 <= parent.childCount) {
                parent.addView(this, indexOfHeader + 1, lp)
            } else {
                parent.addView(this, lp)
            }
        }

        updateAnimationForWeather(weatherCode)
        if (isEnabled) {
            lottieView.resumeAnimation()
        }
    }

    fun detachFromHeader() {
        if (!isAttached.compareAndSet(true, false)) {
            return
        }

        lottieView.pauseAnimation()
        val parent = parent as? ViewGroup
        parent?.removeView(this)
        currentHeaderView = null
    }

    fun transferToNextHeader(newHeaderView: View) {
        if (!isAttached.get()) {
            attachToStickyHeader(newHeaderView, currentWeatherCode)
            return
        }

        if (currentHeaderView == newHeaderView) {
            return
        }

        detachFromHeader()
        attachToStickyHeader(newHeaderView, currentWeatherCode)
    }

    fun updateWeatherCondition(weatherCode: String) {
        currentWeatherCode = weatherCode
        if (isAttached.get()) {
            updateAnimationForWeather(weatherCode)
        }
    }

    private fun updateAnimationForWeather(iconCode: String) {
        val isTablet = resources.configuration.smallestScreenWidthDp >= 600
        val lottieFileName = when {
            iconCode.startsWith("heavy_snow") -> if (isTablet) "snow_tablet.lottie" else "snow.lottie"
            iconCode.startsWith("snow") || iconCode.startsWith("light_snow") -> if (isTablet) "snow_tablet.lottie" else "snow.lottie"
            iconCode.startsWith("sleet") || iconCode.startsWith("freezing_drizzle") -> if (isTablet) "snow_tablet.lottie" else "snow.lottie"
            iconCode.startsWith("heavy_rain") -> "heavy_rain.lottie"
            iconCode.startsWith("thunderstorm") || iconCode.startsWith("hail") -> "heavy_rain.lottie"
            iconCode.startsWith("light_rain") || iconCode.startsWith("rain") -> if (isTablet) "rain_tablet.lottie" else "rain.lottie"
            iconCode.startsWith("drizzle") || iconCode.startsWith("light_drizzle") -> if (isTablet) "rain_tablet.lottie" else "rain.lottie"
            else -> {
                visibility = View.GONE
                return
            }
        }

        visibility = View.VISIBLE
        try {
            lottieView.setAnimation(lottieFileName)
            lottieView.repeatCount = LottieDrawable.INFINITE
            if (isAttached.get() && isEnabled) {
                lottieView.playAnimation()
            }
        } catch (e: Exception) {
            visibility = View.GONE
        }
    }

    fun setEffectEnabled(enabled: Boolean) {
        isEnabled = enabled
        if (isAttached.get()) {
            if (enabled) {
                lottieView.resumeAnimation()
            } else {
                lottieView.pauseAnimation()
            }
        }
    }

    fun pauseAnimation() {
        lottieView.pauseAnimation()
    }

    fun resumeAnimation() {
        if (isEnabled) {
            lottieView.resumeAnimation()
        }
    }

    override fun onDetachedFromWindow() {
        detachFromHeader()
        lottieView.pauseAnimation()
        super.onDetachedFromWindow()
    }
}
