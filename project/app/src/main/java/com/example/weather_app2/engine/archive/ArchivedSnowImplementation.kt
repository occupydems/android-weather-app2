package com.example.weather_app2.engine.archive

import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import androidx.core.content.ContextCompat
import kotlin.math.*
import kotlin.random.Random

/**
 * ArchivedSnowImplementation
 *
 * This is the original CPU Canvas-based snow renderer preserved for reference.
 * It was extracted from WeatherEffectsView.kt which rendered snowflakes using
 * bitmap sprites (snowflake_1.png, snowflake_2.png), per-frame position updates,
 * drift oscillation, rotation, and fade-out near the bottom of the screen.
 *
 * This implementation has been superseded by the new Lottie/OpenGL-based
 * particle engine but is kept here as a reference for the original behaviour,
 * particle counts, physics constants, and visual tuning values.
 */
class ArchivedSnowImplementation {

    // ── Data class ──────────────────────────────────────────────────────
    data class SnowFlake(
        var x: Float, var y: Float, var size: Float,
        var speed: Float, var opacity: Float,
        var driftAmplitude: Float, var driftFrequency: Float, var driftPhase: Float,
        var rotation: Float, var rotationSpeed: Float,
        var imageIndex: Int
    )

    // ── Snow-related fields ─────────────────────────────────────────────
    private var showSnow = false
    private var snowCount = 120
    private val snowFlakes = mutableListOf<SnowFlake>()

    private var snowflakeBitmap1: Bitmap? = null
    private var snowflakeBitmap2: Bitmap? = null

    private val bitmapPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val reusableSrcRect = Rect()
    private val reusableDstRect = RectF()

    // ── Snowflake initialisation (from regenerateParticles) ─────────────
    fun initSnowFlakes(w: Float, h: Float, density: Float, scale: Float) {
        snowFlakes.clear()
        if (!showSnow) return
        val count = (snowCount * scale).toInt()
        for (i in 0 until count) {
            snowFlakes.add(
                SnowFlake(
                    x = Random.nextFloat() * w,
                    y = Random.nextFloat() * h * 1.2f - h * 0.2f,
                    size = (6f + Random.nextFloat() * 16f) * density,
                    speed = (40f + Random.nextFloat() * 80f) * density,
                    opacity = 0.5f + Random.nextFloat() * 0.5f,
                    driftAmplitude = (10f + Random.nextFloat() * 30f) * density,
                    driftFrequency = 0.5f + Random.nextFloat() * 1.5f,
                    driftPhase = Random.nextFloat() * 2f * PI.toFloat(),
                    rotation = Random.nextFloat() * 360f,
                    rotationSpeed = (Random.nextFloat() * 60f - 30f),
                    imageIndex = if (Random.nextBoolean()) 0 else 1
                )
            )
        }
    }

    // ── Draw method (verbatim from WeatherEffectsView) ──────────────────
    fun drawSnow(canvas: Canvas, w: Float, h: Float, delta: Float, currentTime: Long) {
        val bmp1 = snowflakeBitmap1
        val bmp2 = snowflakeBitmap2
        if (bmp1 == null && bmp2 == null) return

        val timeSeconds = currentTime / 1000f
        val effectiveFloor = h

        for (flake in snowFlakes) {
            flake.y += flake.speed * delta
            flake.rotation += flake.rotationSpeed * delta

            val xOffset = flake.driftAmplitude * sin(
                timeSeconds * flake.driftFrequency + flake.driftPhase
            )

            if (flake.y > effectiveFloor) {
                flake.y = -flake.size
                flake.x = Random.nextFloat() * w
                flake.driftPhase = Random.nextFloat() * 2f * PI.toFloat()
            }

            var alpha = flake.opacity
            val fadeStart = effectiveFloor - effectiveFloor * 0.1f
            if (flake.y > fadeStart) {
                val fadeProgress = (flake.y - fadeStart) / (effectiveFloor - fadeStart)
                alpha *= (1f - fadeProgress).coerceAtLeast(0f)
            }

            bitmapPaint.alpha = (alpha * 255).toInt()

            val bitmap = if (flake.imageIndex == 0 && bmp1 != null) bmp1
                         else bmp2 ?: bmp1 ?: continue

            val drawX = flake.x + xOffset
            val halfSize = flake.size / 2f

            canvas.save()
            canvas.translate(drawX, flake.y)
            canvas.rotate(flake.rotation)

            reusableSrcRect.set(0, 0, bitmap.width, bitmap.height)
            reusableDstRect.set(-halfSize, -halfSize, halfSize, halfSize)
            canvas.drawBitmap(bitmap, reusableSrcRect, reusableDstRect, bitmapPaint)

            canvas.restore()
        }
    }

    // ── Weather condition snow counts (from setWeatherCondition) ─────────
    // "sleet_d/n"       -> snowCount = 40
    // "light_snow_d/n"  -> snowCount = 60
    // "snow_d/n"        -> snowCount = 120  (default)
    // "heavy_snow_d/n"  -> snowCount = 200
}
