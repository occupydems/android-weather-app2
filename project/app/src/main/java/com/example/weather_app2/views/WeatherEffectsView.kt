package com.example.weather_app2.views

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.example.weather_app2.R
import kotlin.math.*
import kotlin.random.Random

class WeatherEffectsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private data class StarParticle(
        var x: Float, var y: Float, var size: Float,
        var brightness: Float, var twinkleDuration: Long, var twinklePhase: Float,
        var driftSpeed: Float,
        var fadeAlpha: Float = 1f,
        var isFading: Boolean = false,
        var fadeTarget: Float = 1f,
        var fadeSpeed: Float = 0f,
        var fadeTimer: Float = 0f,
        var fadeCooldown: Float = 0f
    )

    private data class RainDrop(
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

    private data class SnowFlake(
        var x: Float, var y: Float, var size: Float,
        var speed: Float, var opacity: Float,
        var driftAmplitude: Float, var driftFrequency: Float, var driftPhase: Float,
        var rotation: Float, var rotationSpeed: Float,
        var imageIndex: Int
    )

    private data class CloudShape(
        var x: Float, var y: Float, var width: Float, var height: Float,
        var opacity: Float, var speed: Float
    )

    private data class FogLayer(
        var x: Float, var y: Float, var width: Float, var height: Float,
        var opacity: Float, var speed: Float
    )

    private val density = resources.displayMetrics.density
    var statusBarInset: Int = 0

    private var showStars = false
    private var showRain = false
    private var showSnow = false
    private var showClouds = false
    private var showLightning = false
    private var showFog = false
    private var showSun = false
    private var isCompact = false
    private var landingLineY: Float = -1f

    private var cloudCount = 5
    private var rainCount = 60
    private var snowCount = 120
    private var heavyRain = false
    private var overcastClouds = false

    private val stars = mutableListOf<StarParticle>()
    private val rainDrops = mutableListOf<RainDrop>()
    private val snowFlakes = mutableListOf<SnowFlake>()
    private val clouds = mutableListOf<CloudShape>()
    private val fogLayers = mutableListOf<FogLayer>()

    private var raindropBitmap: Bitmap? = null
    private var snowflakeBitmap1: Bitmap? = null
    private var snowflakeBitmap2: Bitmap? = null
    private var sunBaseBitmap: Bitmap? = null
    private var sunRaysBitmap: Bitmap? = null
    private var sunRotation: Float = 0f

    private val starPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.FILL
    }

    private val bitmapPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val rainTintFilter = PorterDuffColorFilter(Color.argb(255, 200, 215, 240), PorterDuff.Mode.SRC_IN)
    private val reusableSrcRect = Rect()
    private val reusableDstRect = RectF()

    private val cloudPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.FILL
    }
    private val fogPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.FILL
    }
    private val lightningPaint = Paint().apply {
        color = Color.parseColor("#E8E0FF")
        style = Paint.Style.FILL
    }

    private val handler = Handler(Looper.getMainLooper())
    private var isAnimating = false
    private var lastFrameTime = 0L
    private var animationStartTime = 0L

    private var lightningCycleMs: Long = 6000L
    private var lightningPhase: Float = Random.nextFloat()

    private val starPath = Path()

    companion object {
        private const val SMALL_DROP_MAX = 20f
        private const val FADE_START_SMALL = 0.30f
        private const val FADE_END_SMALL = 0.40f
        private const val FADE_START_BIG = 0.50f
        private const val FADE_END_BIG = 0.60f
    }

    private val animationRunnable = object : Runnable {
        override fun run() {
            if (!isAnimating || !isAttachedToWindow) return
            invalidate()
            handler.postDelayed(this, 33L)
        }
    }

    init {
        setLayerType(LAYER_TYPE_HARDWARE, null)
        loadBitmaps()
    }

    private fun loadBitmaps() {
        try {
            val rd = ContextCompat.getDrawable(context, R.drawable.raindrop_bulb)
            if (rd is BitmapDrawable) {
                raindropBitmap = rd.bitmap
            }
        } catch (_: Exception) {}

        try {
            val sf1 = ContextCompat.getDrawable(context, R.drawable.snowflake_1)
            if (sf1 is BitmapDrawable) {
                snowflakeBitmap1 = sf1.bitmap
            }
        } catch (_: Exception) {}

        try {
            val sf2 = ContextCompat.getDrawable(context, R.drawable.snowflake_2)
            if (sf2 is BitmapDrawable) {
                snowflakeBitmap2 = sf2.bitmap
            }
        } catch (_: Exception) {}

        try {
            val sb = ContextCompat.getDrawable(context, R.drawable.sun_base)
            if (sb is BitmapDrawable) {
                sunBaseBitmap = sb.bitmap
            }
        } catch (_: Exception) {}

        try {
            val sr = ContextCompat.getDrawable(context, R.drawable.sun_rays)
            if (sr is BitmapDrawable) {
                sunRaysBitmap = sr.bitmap
            }
        } catch (_: Exception) {}
    }

    fun setWeatherCondition(iconTag: String) {
        showStars = false
        showRain = false
        showSnow = false
        showClouds = false
        showLightning = false
        showFog = false
        showSun = false
        cloudCount = 5
        rainCount = 60
        snowCount = 120
        heavyRain = false
        overcastClouds = false

        when (iconTag) {
            "clear_d", "clear_morning", "clear_afternoon" -> {
                showSun = true
            }
            "clear_n", "clear_dawn", "clear_evening", "clear_sunset" -> {
                showStars = true
            }
            "mainly_clear_d", "mainly_clear_morning" -> {
                showClouds = true
                cloudCount = 2
                showSun = true
            }
            "mainly_clear_sunset", "mainly_clear_dawn" -> {
                showClouds = true
                cloudCount = 2
            }
            "mainly_clear_n" -> {
                showClouds = true
                cloudCount = 2
                showStars = true
            }
            "partly_cloudy_d", "partly_cloudy_morning" -> {
                showClouds = true
                cloudCount = 4
                showSun = true
            }
            "partly_cloudy_sunset", "partly_cloudy_dawn" -> {
                showClouds = true
                cloudCount = 4
            }
            "partly_cloudy_n" -> {
                showClouds = true
                cloudCount = 4
                showStars = true
            }
            "overcast_d", "overcast_morning", "overcast_sunset" -> {
                showClouds = true
                cloudCount = 10
                overcastClouds = true
            }
            "overcast_n" -> {
                showClouds = true
                cloudCount = 10
                overcastClouds = true
            }
            "fog_d", "fog_n" -> {
                showFog = true
            }
            "light_drizzle_d", "light_drizzle_n" -> {
                showRain = true
                showClouds = true
                cloudCount = 3
                rainCount = 30
            }
            "drizzle_d", "drizzle_n" -> {
                showRain = true
                showClouds = true
                cloudCount = 4
                rainCount = 50
            }
            "freezing_drizzle_d", "freezing_drizzle_n" -> {
                showRain = true
                showClouds = true
                cloudCount = 4
                rainCount = 40
            }
            "light_rain_d", "light_rain_n" -> {
                showRain = true
                showClouds = true
                cloudCount = 5
                rainCount = 50
            }
            "rain_d", "rain_n" -> {
                showRain = true
                showClouds = true
                cloudCount = 6
                rainCount = 68
                heavyRain = false
            }
            "heavy_rain_d", "heavy_rain_n" -> {
                showRain = true
                showClouds = true
                cloudCount = 8
                rainCount = 90
                heavyRain = true
            }
            "sleet_d", "sleet_n" -> {
                showRain = true
                showSnow = true
                showClouds = true
                cloudCount = 5
                rainCount = 30
                snowCount = 40
            }
            "light_snow_d", "light_snow_n" -> {
                showSnow = true
                showClouds = true
                cloudCount = 4
                snowCount = 60
            }
            "snow_d", "snow_n" -> {
                showSnow = true
                showClouds = true
                cloudCount = 5
                snowCount = 120
            }
            "heavy_snow_d", "heavy_snow_n" -> {
                showSnow = true
                showClouds = true
                cloudCount = 7
                snowCount = 200
            }
            "thunderstorm_d", "thunderstorm_n", "hail_d", "hail_n" -> {
                showRain = true
                showClouds = true
                showLightning = true
                cloudCount = 8
                rainCount = 90
                heavyRain = true
                lightningPhase = Random.nextFloat()
            }
        }

        regenerateParticles()
        invalidate()
    }

    fun setCompactMode(compact: Boolean) {
        isCompact = compact
        regenerateParticles()
        invalidate()
    }

    fun setLandingLine(y: Float) {
        landingLineY = y
    }

    fun startAnimations() {
        if (isAnimating) return
        isAnimating = true
        lastFrameTime = System.currentTimeMillis()
        animationStartTime = lastFrameTime
        handler.post(animationRunnable)
    }

    fun stopAnimations() {
        isAnimating = false
        handler.removeCallbacks(animationRunnable)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        startAnimations()
    }

    override fun onDetachedFromWindow() {
        stopAnimations()
        super.onDetachedFromWindow()
    }

    override fun onWindowVisibilityChanged(visibility: Int) {
        super.onWindowVisibilityChanged(visibility)
        if (visibility == VISIBLE) {
            startAnimations()
        } else {
            stopAnimations()
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (w > 0 && h > 0) {
            regenerateParticles()
        }
    }

    private fun regenerateParticles() {
        val w = width.toFloat()
        val h = height.toFloat()
        if (w <= 0 || h <= 0) return

        val scale = if (isCompact) 0.3f else 1f

        stars.clear()
        rainDrops.clear()
        snowFlakes.clear()
        clouds.clear()
        fogLayers.clear()

        if (showStars) {
            val starDriftSpeed = -2.5f * density
            val starSizes = floatArrayOf(1.125f, 2.5f, 3.75f, 5.25f, 5.06f)
            val baseCounts = intArrayOf(25, 20, 15, 10, 7)
            for (tier in starSizes.indices) {
                val baseCount = (baseCounts[tier] * scale).toInt()
                val count = baseCount + Random.nextInt((baseCount * 0.4f).toInt().coerceAtLeast(1) + 1)
                val baseSize = starSizes[tier]
                val sizeVariation = baseSize * 0.15f
                for (i in 0 until count) {
                    stars.add(StarParticle(
                        x = Random.nextFloat() * w,
                        y = Random.nextFloat() * h * 0.85f,
                        size = (baseSize - sizeVariation + Random.nextFloat() * sizeVariation * 2f) * density,
                        brightness = Random.nextFloat(),
                        twinkleDuration = 2000L + (Random.nextFloat() * 3000L).toLong(),
                        twinklePhase = Random.nextFloat(),
                        driftSpeed = starDriftSpeed,
                        fadeCooldown = 1f + Random.nextFloat() * 4f
                    ))
                }
            }
        }

        if (showRain) {
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

                rainDrops.add(RainDrop(
                    x = x,
                    y = topY + Random.nextFloat() * travelDist,
                    speed = speed,
                    dropW = length * 0.35f,
                    dropH = length,
                    dropOpacity = opacity,
                    isBig = isBig,
                    topY = topY,
                    bottomY = bottomY
                ))
            }
        }

        if (showSnow) {
            val count = (snowCount * scale).toInt()
            for (i in 0 until count) {
                snowFlakes.add(SnowFlake(
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
                ))
            }
        }

        if (showClouds) {
            val count = if (isCompact) max(2, (cloudCount * 0.3f).toInt()) else cloudCount
            if (overcastClouds) {
                for (i in 0 until count) {
                    val yPos = Random.nextFloat() * h * 0.9f
                    clouds.add(CloudShape(
                        x = Random.nextFloat() * w * 1.5f - w * 0.25f,
                        y = yPos,
                        width = (200f + Random.nextFloat() * 300f) * density,
                        height = (60f + Random.nextFloat() * 80f) * density,
                        opacity = 0.15f + Random.nextFloat() * 0.20f,
                        speed = (5f + Random.nextFloat() * 12f) * density
                    ))
                }
                val topCount = if (isCompact) 3 else 6
                for (i in 0 until topCount) {
                    val yPos = -Random.nextFloat() * 80f * density
                    clouds.add(CloudShape(
                        x = Random.nextFloat() * w * 1.5f - w * 0.25f,
                        y = yPos,
                        width = (250f + Random.nextFloat() * 350f) * density,
                        height = (70f + Random.nextFloat() * 90f) * density,
                        opacity = 0.20f + Random.nextFloat() * 0.25f,
                        speed = (4f + Random.nextFloat() * 10f) * density
                    ))
                }
            } else {
                val yRange = when {
                    cloudCount >= 6 -> 0.7f
                    cloudCount >= 5 -> 0.6f
                    else -> 0.4f
                }
                val baseOpacity = when {
                    cloudCount >= 6 -> 0.12f
                    cloudCount >= 5 -> 0.10f
                    else -> 0.06f
                }
                val opacityRange = when {
                    cloudCount >= 6 -> 0.18f
                    cloudCount >= 5 -> 0.14f
                    else -> 0.10f
                }
                val baseWidth = when {
                    cloudCount >= 6 -> 160f
                    else -> 120f
                }
                val widthRange = when {
                    cloudCount >= 6 -> 220f
                    else -> 180f
                }
                for (i in 0 until count) {
                    val yPos = if (cloudCount >= 5) {
                        val topBias = Random.nextFloat()
                        topBias * topBias * h * yRange
                    } else {
                        Random.nextFloat() * h * yRange
                    }
                    clouds.add(CloudShape(
                        x = Random.nextFloat() * w * 1.5f - w * 0.25f,
                        y = yPos,
                        width = (baseWidth + Random.nextFloat() * widthRange) * density,
                        height = (40f + Random.nextFloat() * 60f) * density,
                        opacity = baseOpacity + Random.nextFloat() * opacityRange,
                        speed = (8f + Random.nextFloat() * 20f) * density
                    ))
                }
                if (cloudCount >= 5) {
                    val topCount = if (isCompact) 2 else 4
                    for (i in 0 until topCount) {
                        val yPos = -Random.nextFloat() * 60f * density
                        clouds.add(CloudShape(
                            x = Random.nextFloat() * w * 1.5f - w * 0.25f,
                            y = yPos,
                            width = (baseWidth + Random.nextFloat() * widthRange) * density,
                            height = (50f + Random.nextFloat() * 70f) * density,
                            opacity = baseOpacity + Random.nextFloat() * opacityRange,
                            speed = (6f + Random.nextFloat() * 15f) * density
                        ))
                    }
                }
            }
        }

        if (showFog) {
            val count = ((6 + Random.nextInt(3)) * scale).toInt().coerceAtLeast(3)
            for (i in 0 until count) {
                fogLayers.add(FogLayer(
                    x = Random.nextFloat() * w * 0.3f - w * 0.15f,
                    y = h * 0.1f + (i.toFloat() / count) * h * 0.8f,
                    width = w * 1.8f,
                    height = (100f + Random.nextFloat() * 80f) * density,
                    opacity = 0.06f + Random.nextFloat() * 0.09f,
                    speed = (3f + Random.nextFloat() * 8f) * density * (if (i % 2 == 0) 1f else -1f)
                ))
            }
        }

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val currentTime = System.currentTimeMillis()
        val deltaTime = if (lastFrameTime > 0) (currentTime - lastFrameTime) / 1000f else 0.033f
        lastFrameTime = currentTime
        val clampedDelta = deltaTime.coerceAtMost(0.1f)

        val w = width.toFloat()
        val h = height.toFloat()
        if (w <= 0 || h <= 0) return

        if (showSun) drawSun(canvas, w, h, clampedDelta)
        if (showClouds) drawClouds(canvas, w, h, clampedDelta)
        if (showFog) drawFog(canvas, w, h, clampedDelta, currentTime)
        if (showStars) drawStars(canvas, w, h, clampedDelta, currentTime)
        if (showRain) drawRain(canvas, w, h, clampedDelta)
        if (showSnow) drawSnow(canvas, w, h, clampedDelta, currentTime)
        if (showLightning) drawLightning(canvas, w, h, currentTime)
    }

    private val minFadeDistance = 80f

    private fun canStartFading(candidate: StarParticle): Boolean {
        val minDist = minFadeDistance * density
        val minDistSq = minDist * minDist
        for (other in stars) {
            if (other === candidate || !other.isFading) continue
            val dx = candidate.x - other.x
            val dy = candidate.y - other.y
            if (dx * dx + dy * dy < minDistSq) return false
        }
        return true
    }

    private fun drawStars(canvas: Canvas, w: Float, h: Float, delta: Float, currentTime: Long) {
        val maxFading = (stars.size * 0.08f).toInt().coerceAtLeast(2)
        var currentlyFading = 0
        for (star in stars) {
            if (star.isFading) currentlyFading++
        }

        for (star in stars) {
            star.x += star.driftSpeed * delta
            if (star.x < 0) star.x += w

            if (star.isFading) {
                if (star.fadeAlpha > star.fadeTarget) {
                    star.fadeAlpha -= star.fadeSpeed * delta
                    if (star.fadeAlpha <= star.fadeTarget) {
                        star.fadeAlpha = star.fadeTarget
                        star.fadeTarget = 1f
                        star.fadeSpeed = 0.08f + Random.nextFloat() * 0.1f
                    }
                } else {
                    star.fadeAlpha += star.fadeSpeed * delta
                    if (star.fadeAlpha >= 1f) {
                        star.fadeAlpha = 1f
                        star.isFading = false
                        star.fadeCooldown = 1f + Random.nextFloat() * 5f
                    }
                }
            } else {
                star.fadeCooldown -= delta
                if (star.fadeCooldown <= 0f && Random.nextFloat() < 0.06f) {
                    if (currentlyFading < maxFading && canStartFading(star)) {
                        star.isFading = true
                        star.fadeTarget = 0.1f + Random.nextFloat() * 0.2f
                        star.fadeSpeed = 0.08f + Random.nextFloat() * 0.12f
                        currentlyFading++
                    } else {
                        star.fadeCooldown = 1f + Random.nextFloat() * 3f
                    }
                }
            }

            starPaint.alpha = (star.fadeAlpha * 255).toInt()

            starPath.reset()
            val halfSize = star.size / 2f
            starPath.moveTo(star.x, star.y - halfSize)
            starPath.lineTo(star.x + halfSize * 0.35f, star.y - halfSize * 0.35f)
            starPath.lineTo(star.x + halfSize, star.y)
            starPath.lineTo(star.x + halfSize * 0.35f, star.y + halfSize * 0.35f)
            starPath.lineTo(star.x, star.y + halfSize)
            starPath.lineTo(star.x - halfSize * 0.35f, star.y + halfSize * 0.35f)
            starPath.lineTo(star.x - halfSize, star.y)
            starPath.lineTo(star.x - halfSize * 0.35f, star.y - halfSize * 0.35f)
            starPath.close()

            canvas.drawPath(starPath, starPaint)
        }
    }

    private fun drawRain(canvas: Canvas, w: Float, h: Float, delta: Float) {
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

    private fun drawSnow(canvas: Canvas, w: Float, h: Float, delta: Float, currentTime: Long) {
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

    private fun drawClouds(canvas: Canvas, w: Float, h: Float, delta: Float) {
        for (cloud in clouds) {
            cloud.x += cloud.speed * delta

            if (cloud.x > w + cloud.width / 2f) {
                cloud.x = -cloud.width
            }

            cloudPaint.alpha = (cloud.opacity * 255).toInt()

            val rect = RectF(
                cloud.x,
                cloud.y,
                cloud.x + cloud.width,
                cloud.y + cloud.height
            )
            canvas.drawOval(rect, cloudPaint)
        }
    }

    private fun drawLightning(canvas: Canvas, w: Float, h: Float, currentTime: Long) {
        val adjustedTime = currentTime + (lightningPhase * lightningCycleMs).toLong()
        val cyclePos = (adjustedTime % lightningCycleMs).toFloat()

        val alpha: Float = when {
            cyclePos < 50f -> {
                (cyclePos / 50f) * 0.7f
            }
            cyclePos < 150f -> {
                0.7f * (1f - (cyclePos - 50f) / 100f)
            }
            cyclePos < 300f -> {
                0f
            }
            cyclePos < 350f -> {
                ((cyclePos - 300f) / 50f) * 0.3f
            }
            cyclePos < 550f -> {
                0.3f * (1f - (cyclePos - 350f) / 200f)
            }
            else -> {
                0f
            }
        }

        if (alpha > 0.001f) {
            lightningPaint.alpha = (alpha * 255).toInt()
            canvas.drawRect(0f, 0f, w, h, lightningPaint)
        }
    }

    private fun drawSun(canvas: Canvas, w: Float, h: Float, delta: Float) {
        if (isCompact) return
        val baseBmp = sunBaseBitmap
        val raysBmp = sunRaysBitmap
        if (baseBmp == null && raysBmp == null) return

        sunRotation += 3f * delta
        if (sunRotation >= 360f) sunRotation -= 360f

        val cx = w * 0.12f
        val cy = h * 0.08f + statusBarInset
        val baseSize = 90f * density
        val raysSize = baseSize * 6.4f

        bitmapPaint.alpha = 255

        if (raysBmp != null) {
            val dotFracX = 0.4501f
            val dotFracY = 0.4951f
            val dotOffX = dotFracX * raysSize
            val dotOffY = dotFracY * raysSize
            bitmapPaint.alpha = 150
            canvas.save()
            canvas.translate(cx, cy)
            canvas.rotate(sunRotation)
            reusableSrcRect.set(0, 0, raysBmp.width, raysBmp.height)
            reusableDstRect.set(-dotOffX, -dotOffY, raysSize - dotOffX, raysSize - dotOffY)
            canvas.drawBitmap(raysBmp, reusableSrcRect, reusableDstRect, bitmapPaint)
            canvas.restore()
            bitmapPaint.alpha = 255
        }

        if (baseBmp != null) {
            val halfBase = baseSize / 2f
            reusableSrcRect.set(0, 0, baseBmp.width, baseBmp.height)
            reusableDstRect.set(cx - halfBase, cy - halfBase, cx + halfBase, cy + halfBase)
            canvas.drawBitmap(baseBmp, reusableSrcRect, reusableDstRect, bitmapPaint)
        }
    }

    private fun drawFog(canvas: Canvas, w: Float, h: Float, delta: Float, currentTime: Long) {
        for (fog in fogLayers) {
            fog.x += fog.speed * delta

            val maxDrift = w * 0.15f
            if (fog.x > maxDrift) {
                fog.speed = -abs(fog.speed)
            } else if (fog.x < -maxDrift) {
                fog.speed = abs(fog.speed)
            }

            fogPaint.alpha = (fog.opacity * 255).toInt()

            canvas.drawRect(
                fog.x,
                fog.y - fog.height / 2f,
                fog.x + fog.width,
                fog.y + fog.height / 2f,
                fogPaint
            )
        }
    }

}
