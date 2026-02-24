package com.example.weather_app2.engine.inert

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class InertSceneRenderer {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val starPositions = mutableListOf<Triple<Float, Float, Float>>()
    private var lastStarSeed: Long = 0

    fun drawBackground(canvas: Canvas, width: Int, height: Int, wmoCode: Int, isNight: Boolean) {
        val (topColor, bottomColor) = getGradientColors(wmoCode, isNight)
        paint.shader = LinearGradient(
            0f, 0f, 0f, height.toFloat(),
            topColor, bottomColor,
            Shader.TileMode.CLAMP
        )
        paint.style = Paint.Style.FILL
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
        paint.shader = null

        if (isNight) {
            drawStars(canvas, width, height)
            drawMoon(canvas, width, height)
        } else {
            val isClear = wmoCode in setOf(0, 1)
            if (isClear) {
                drawSun(canvas, width, height)
            }
        }

        val isCloudy = wmoCode in setOf(2, 3, 45, 48, 51, 53, 55, 56, 57, 61, 63, 65, 66, 67,
            71, 73, 75, 77, 80, 81, 82, 85, 86, 95, 96, 99)
        if (isCloudy) {
            drawClouds(canvas, width, height)
        }
    }

    fun drawSun(canvas: Canvas, width: Int = 1080, height: Int = 1920) {
        val cx = width * 0.75f
        val cy = height * 0.12f
        val radius = width * 0.06f

        paint.shader = null
        paint.style = Paint.Style.FILL
        paint.color = Color.argb(60, 255, 220, 100)
        canvas.drawCircle(cx, cy, radius * 2.5f, paint)

        paint.color = Color.argb(100, 255, 230, 120)
        canvas.drawCircle(cx, cy, radius * 1.6f, paint)

        paint.color = Color.argb(255, 255, 240, 180)
        canvas.drawCircle(cx, cy, radius, paint)

        paint.color = Color.argb(80, 255, 230, 100)
        paint.strokeWidth = 3f
        paint.style = Paint.Style.STROKE
        val rayCount = 12
        for (i in 0 until rayCount) {
            val angle = (i * 360f / rayCount) * (Math.PI.toFloat() / 180f)
            val startR = radius * 1.3f
            val endR = radius * 2.0f
            canvas.drawLine(
                cx + cos(angle) * startR,
                cy + sin(angle) * startR,
                cx + cos(angle) * endR,
                cy + sin(angle) * endR,
                paint
            )
        }
        paint.style = Paint.Style.FILL
    }

    fun drawMoon(canvas: Canvas, width: Int = 1080, height: Int = 1920) {
        val cx = width * 0.8f
        val cy = height * 0.1f
        val radius = width * 0.04f

        paint.shader = null
        paint.style = Paint.Style.FILL
        paint.color = Color.argb(30, 200, 210, 255)
        canvas.drawCircle(cx, cy, radius * 3f, paint)

        paint.color = Color.argb(60, 210, 220, 255)
        canvas.drawCircle(cx, cy, radius * 1.8f, paint)

        paint.color = Color.argb(230, 230, 235, 255)
        canvas.drawCircle(cx, cy, radius, paint)

        paint.color = Color.argb(40, 180, 190, 210)
        canvas.drawCircle(cx - radius * 0.3f, cy - radius * 0.2f, radius * 0.2f, paint)
        canvas.drawCircle(cx + radius * 0.2f, cy + radius * 0.25f, radius * 0.15f, paint)
    }

    fun drawStars(canvas: Canvas, width: Int = 1080, height: Int = 1920) {
        val seed = (width.toLong() * 31 + height.toLong())
        if (seed != lastStarSeed || starPositions.isEmpty()) {
            lastStarSeed = seed
            starPositions.clear()
            val rng = Random(seed)
            val count = 40 + rng.nextInt(30)
            for (i in 0 until count) {
                starPositions.add(
                    Triple(
                        rng.nextFloat() * width,
                        rng.nextFloat() * height * 0.5f,
                        1f + rng.nextFloat() * 2.5f
                    )
                )
            }
        }

        paint.shader = null
        paint.style = Paint.Style.FILL
        for ((sx, sy, sr) in starPositions) {
            val alpha = (120 + (sr * 40).toInt()).coerceAtMost(255)
            paint.color = Color.argb(alpha, 240, 245, 255)
            canvas.drawCircle(sx, sy, sr, paint)
        }
    }

    fun drawClouds(canvas: Canvas, width: Int = 1080, height: Int = 1920) {
        paint.shader = null
        paint.style = Paint.Style.FILL

        drawCloudGroup(canvas, width * 0.15f, height * 0.08f, width * 0.35f, 140)
        drawCloudGroup(canvas, width * 0.55f, height * 0.15f, width * 0.30f, 100)
        drawCloudGroup(canvas, width * 0.05f, height * 0.22f, width * 0.25f, 80)
    }

    private fun drawCloudGroup(canvas: Canvas, cx: Float, cy: Float, cloudWidth: Float, alpha: Int) {
        paint.color = Color.argb(alpha, 220, 225, 235)
        val h = cloudWidth * 0.3f

        val rect = RectF(cx, cy, cx + cloudWidth, cy + h)
        canvas.drawOval(rect, paint)

        val rect2 = RectF(cx + cloudWidth * 0.15f, cy - h * 0.4f, cx + cloudWidth * 0.65f, cy + h * 0.6f)
        canvas.drawOval(rect2, paint)

        val rect3 = RectF(cx + cloudWidth * 0.4f, cy - h * 0.2f, cx + cloudWidth * 0.9f, cy + h * 0.7f)
        canvas.drawOval(rect3, paint)
    }

    private fun getGradientColors(wmoCode: Int, isNight: Boolean): Pair<Int, Int> {
        if (isNight) {
            return when (wmoCode) {
                0, 1 -> Pair(Color.parseColor("#0B1026"), Color.parseColor("#1A2344"))
                2, 3 -> Pair(Color.parseColor("#1A1F3A"), Color.parseColor("#2C3255"))
                45, 48 -> Pair(Color.parseColor("#2A2E45"), Color.parseColor("#3D415A"))
                in 51..57 -> Pair(Color.parseColor("#1C2233"), Color.parseColor("#2E3548"))
                in 61..67 -> Pair(Color.parseColor("#151B2E"), Color.parseColor("#252D44"))
                in 71..77 -> Pair(Color.parseColor("#202838"), Color.parseColor("#3A4258"))
                in 80..82 -> Pair(Color.parseColor("#141A2C"), Color.parseColor("#222A40"))
                in 85..86 -> Pair(Color.parseColor("#1E2638"), Color.parseColor("#343C52"))
                in 95..99 -> Pair(Color.parseColor("#0D101E"), Color.parseColor("#1A1E34"))
                else -> Pair(Color.parseColor("#0F1428"), Color.parseColor("#1E2340"))
            }
        }
        return when (wmoCode) {
            0 -> Pair(Color.parseColor("#4A90D9"), Color.parseColor("#87CEEB"))
            1 -> Pair(Color.parseColor("#5A9BE0"), Color.parseColor("#8ED1EE"))
            2 -> Pair(Color.parseColor("#6E9EC8"), Color.parseColor("#A0BDD8"))
            3 -> Pair(Color.parseColor("#7A8EA5"), Color.parseColor("#9EAAB8"))
            45, 48 -> Pair(Color.parseColor("#8C939E"), Color.parseColor("#B0B5BC"))
            in 51..55 -> Pair(Color.parseColor("#6B7F99"), Color.parseColor("#8FA3B8"))
            56, 57 -> Pair(Color.parseColor("#607590"), Color.parseColor("#8595A8"))
            in 61..65 -> Pair(Color.parseColor("#556A85"), Color.parseColor("#7A8FA5"))
            66, 67 -> Pair(Color.parseColor("#5A7088"), Color.parseColor("#7E92A8"))
            in 71..77 -> Pair(Color.parseColor("#7888A0"), Color.parseColor("#A5B2C2"))
            in 80..82 -> Pair(Color.parseColor("#4A6080"), Color.parseColor("#6E8AA5"))
            in 85..86 -> Pair(Color.parseColor("#6E7E98"), Color.parseColor("#98A8BB"))
            95 -> Pair(Color.parseColor("#3A4A65"), Color.parseColor("#5A6A82"))
            96, 99 -> Pair(Color.parseColor("#2E3E58"), Color.parseColor("#4E5E75"))
            else -> Pair(Color.parseColor("#5A90D0"), Color.parseColor("#88C8E8"))
        }
    }
}
