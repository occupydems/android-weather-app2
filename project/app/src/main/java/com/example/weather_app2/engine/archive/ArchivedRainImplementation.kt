package com.example.weather_app2.engine.archive

import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import androidx.core.content.ContextCompat
import kotlin.random.Random

/**
 * ArchivedRainImplementation
 *
 * This is the original CPU Canvas-based rain renderer preserved for reference.
 * It was extracted from WeatherEffectsView.kt which rendered rain drops using a
 * tinted bitmap sprite (raindrop_bulb.png), per-frame position updates,
 * lane-based big-drop distribution, and progressive fade-out as drops fall.
 *
 * This implementation has been superseded by the new Lottie/OpenGL-based
 * particle engine but is kept here as a reference for the original behaviour,
 * particle counts, physics constants, and visual tuning values.
 */
class ArchivedRainImplementation {

    // ── Data class ──────────────────────────────────────────────────────
    data class RainDrop(
        var x: Float,
        var y: Float,
        var speed: Float,
        var dropW: Float,
        var dropH: Float,
        var dropOpacity: Float,
        var isBig: Boolean,
        var topY: Float,
        var bottomY: Float
    )

    // ── Constants ───────────────────────────────────────────────────────
    companion object {
        private const val SMALL_DROP_MAX = 20f
        private const val FADE_START_SMALL = 0.30f
        private const val FADE_END_SMALL = 0.40f
        private const val FADE_START_BIG = 0.50f
        private const val FADE_END_BIG = 0.60f
    }

    // ── Rain-related fields ─────────────────────────────────────────────
    private var showRain = false
    private var rainCount = 60
    private var heavyRain = false
    private val rainDrops = mutableListOf<RainDrop>()

    private var raindropBitmap: Bitmap? = null

    private val bitmapPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val rainTintFilter = PorterDuffColorFilter(
        Color.argb(255, 200, 215, 240), PorterDuff.Mode.SRC_IN
    )
    private val reusableSrcRect = Rect()
    private val reusableDstRect = RectF()

    // ── Rain initialisation (from regenerateParticles) ──────────────────
    fun initRainDrops(w: Float, h: Float, density: Float, scale: Float) {
        rainDrops.clear()
        if (!showRain) return

        val baseCount = (rainCount * scale).toInt()
        val bigCount = (baseCount * 0.3f).toInt()
        val totalCount = baseCount + bigCount
        val totalW = w + 60f * density
        val bigLaneW = if (bigCount > 0) totalW / bigCount else totalW
        var bigIdx = 0

        val topY = -80f * density
        val bottomY = h + 30f * density
        val travelDist = bottomY - topY

        for (i in 0 until totalCount) {
            val isBig = i >= baseCount
            val length = if (isBig) {
                (24f + Random.nextFloat() * 14f) * density
            } else {
                (10f + Random.nextFloat() * 22f) * density
            }
            val durationMs = if (isBig) {
                (350f + Random.nextFloat() * 500f) * 1.667f
            } else {
                (450f + Random.nextFloat() * 950f) * 1.333f
            }
            val speed = travelDist / (durationMs / 1000f)

            val x = if (isBig) {
                val lx = bigLaneW * bigIdx + Random.nextFloat() * bigLaneW - 30f * density
                bigIdx++
                lx
            } else {
                Random.nextFloat() * totalW - 30f * density
            }

            val opacity = if (isBig) {
                0.5f + Random.nextFloat() * 0.3f
            } else {
                0.3f + Random.nextFloat() * 0.35f
            }

            rainDrops.add(
                RainDrop(
                    x = x,
                    y = topY + Random.nextFloat() * travelDist,
                    speed = speed,
                    dropW = length * 0.35f,
                    dropH = length,
                    dropOpacity = opacity,
                    isBig = isBig,
                    topY = topY,
                    bottomY = bottomY
                )
            )
        }
    }

    // ── Draw method (verbatim from WeatherEffectsView) ──────────────────
    fun drawRain(canvas: Canvas, w: Float, h: Float, delta: Float, density: Float) {
        val bitmap = raindropBitmap ?: return
        val totalW = w + 60f * density

        for (drop in rainDrops) {
            drop.y += drop.speed * delta

            if (drop.y > drop.bottomY) {
                drop.y = drop.topY
                drop.x = Random.nextFloat() * totalW - 30f * density
            }

            val travelDist = drop.bottomY - drop.topY
            val fallProgress = ((drop.y - drop.topY) / travelDist).coerceIn(0f, 1f)

            val isSmallDrop = drop.dropH < SMALL_DROP_MAX * density
            val fadeStart = if (isSmallDrop) FADE_START_SMALL else FADE_START_BIG
            val fadeEnd = if (isSmallDrop) FADE_END_SMALL else FADE_END_BIG

            val alpha = when {
                fallProgress < fadeStart -> drop.dropOpacity
                fallProgress < fadeEnd -> {
                    drop.dropOpacity * (1f - (fallProgress - fadeStart) / (fadeEnd - fadeStart))
                }
                else -> 0f
            }

            if (alpha < 0.01f) continue

            bitmapPaint.alpha = (alpha * 255).toInt()
            bitmapPaint.colorFilter = rainTintFilter

            reusableSrcRect.set(0, 0, bitmap.width, bitmap.height)
            reusableDstRect.set(
                drop.x - drop.dropW / 2f,
                drop.y - drop.dropH / 2f,
                drop.x + drop.dropW / 2f,
                drop.y + drop.dropH / 2f
            )
            canvas.drawBitmap(bitmap, reusableSrcRect, reusableDstRect, bitmapPaint)
        }
        bitmapPaint.colorFilter = null
    }

    // ── Weather condition rain counts (from setWeatherCondition) ─────────
    // "light_drizzle_d/n"    -> rainCount = 30
    // "drizzle_d/n"          -> rainCount = 50
    // "freezing_drizzle_d/n" -> rainCount = 40
    // "light_rain_d/n"       -> rainCount = 50
    // "rain_d/n"             -> rainCount = 68,  heavyRain = false
    // "heavy_rain_d/n"       -> rainCount = 90,  heavyRain = true
    // "sleet_d/n"            -> rainCount = 30
    // "thunderstorm_d/n"     -> rainCount = 90,  heavyRain = true
}
