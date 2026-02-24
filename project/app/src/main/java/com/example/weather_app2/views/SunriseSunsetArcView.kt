package com.example.weather_app2.views

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.math.PI
import kotlin.math.sin

class SunriseSunsetArcView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var sunriseMs: Long = 0
    private var sunsetMs: Long = 0
    private var currentMs: Long = 0
    private var tzOffsetSec: Int = 0

    private val arcPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#FFFFC107")
        style = Paint.Style.STROKE
        strokeWidth = 2f
    }

    private val arcDashedPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#55FFFFFF")
        style = Paint.Style.STROKE
        strokeWidth = 2f
        pathEffect = DashPathEffect(floatArrayOf(8f, 6f), 0f)
    }

    private val horizonPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#55FFFFFF")
        style = Paint.Style.STROKE
        strokeWidth = 1.5f
    }

    private val dotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#FFFFC107")
        style = Paint.Style.FILL
    }

    private val dotBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 2f
    }

    fun setSunTimes(sunrise: Long, sunset: Long, current: Long, timezoneOffsetSeconds: Int) {
        sunriseMs = sunrise
        sunsetMs = sunset
        currentMs = current
        tzOffsetSec = timezoneOffsetSeconds
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val w = width.toFloat()
        val h = height.toFloat()
        val padding = 16f
        val horizonY = h * 0.72f
        val arcHeight = h * 0.55f

        canvas.drawLine(padding, horizonY, w - padding, horizonY, horizonPaint)

        val arcWidth = w - padding * 2
        val path = Path()
        val sunDuration = (sunsetMs - sunriseMs).toFloat()
        if (sunDuration <= 0) return

        val segments = 100
        for (i in 0..segments) {
            val fraction = i.toFloat() / segments
            val x = padding + fraction * arcWidth
            val y = horizonY - sin(fraction * PI.toFloat()) * arcHeight
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }

        val progress = if (currentMs in sunriseMs..sunsetMs) {
            ((currentMs - sunriseMs).toFloat() / sunDuration).coerceIn(0f, 1f)
        } else {
            -1f
        }

        if (progress >= 0) {
            val litPath = Path()
            val litSegments = (progress * segments).toInt()
            for (i in 0..litSegments) {
                val fraction = i.toFloat() / segments
                val x = padding + fraction * arcWidth
                val y = horizonY - sin(fraction * PI.toFloat()) * arcHeight
                if (i == 0) litPath.moveTo(x, y) else litPath.lineTo(x, y)
            }
            canvas.drawPath(litPath, arcPaint)

            val remainPath = Path()
            for (i in litSegments..segments) {
                val fraction = i.toFloat() / segments
                val x = padding + fraction * arcWidth
                val y = horizonY - sin(fraction * PI.toFloat()) * arcHeight
                if (i == litSegments) remainPath.moveTo(x, y) else remainPath.lineTo(x, y)
            }
            canvas.drawPath(remainPath, arcDashedPaint)

            val dotX = padding + progress * arcWidth
            val dotY = horizonY - sin(progress * PI.toFloat()) * arcHeight
            canvas.drawCircle(dotX, dotY, 6f, dotPaint)
            canvas.drawCircle(dotX, dotY, 6f, dotBorderPaint)
        } else {
            canvas.drawPath(path, arcDashedPaint)
        }
    }
}
