package com.example.weather_app2.views

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.example.weather_app2.R
import kotlin.math.*
import kotlin.random.Random

class StickyHeaderCardLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    var stickyHeaderCount: Int = 1

    var opaqueHeaders: Boolean = false
        set(value) {
            if (field != value) {
                field = value
                invalidate()
            }
        }

    private val cornerRadius = 15f * resources.displayMetrics.density
    private val clipPath = Path()
    private val tempRect = RectF()
    private val dp = resources.displayMetrics.density

    private var lastClingCount = 0

    var pinOffset: Float = 0f
        set(value) {
            val clamped = value.coerceAtLeast(0f)
            if (field != clamped) {
                val wasPinned = field > 0f
                field = clamped
                val headerCount = stickyHeaderCount.coerceAtMost(childCount)

                for (i in 0 until headerCount) {
                    getChildAt(i).translationY = clamped
                }

                if (clamped > 0f && !wasPinned) {
                    regenerateParticles()
                    startParticleAnimation()
                    lottieEffectView?.visibility = View.VISIBLE
                } else if (clamped <= 0f && wasPinned) {
                    stopParticleAnimation()
                    splashParticles.clear()
                    clingParticles.clear()
                    lastClingCount = 0
                    lottieEffectView?.visibility = View.GONE
                } else if (clamped > 0f && showSnowCling) {
                    growSnowAccumulation()
                }
                invalidate()
            }
        }

    private var showRainSplash = false
    private var showSnowCling = false

    private data class SplashParticle(
        var x: Float,
        var phase: Float,
        var maxBounceH: Float,
        var durationMs: Float,
        var spawnTime: Long,
        var dropSize: Float,
        var opacity: Float
    )

    private data class ClingParticle(
        var x: Float,
        var yOffset: Float,
        var size: Float,
        var rotation: Float,
        var rotationOsc: Float,
        var oscSpeed: Float,
        var oscPhase: Float,
        var opacity: Float,
        var imageIndex: Int
    )

    private val splashParticles = mutableListOf<SplashParticle>()
    private val clingParticles = mutableListOf<ClingParticle>()

    private var raindropBitmap: Bitmap? = null
    private var snowflakeBitmap1: Bitmap? = null
    private var snowflakeBitmap2: Bitmap? = null
    private val particlePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val rainTintFilter = PorterDuffColorFilter(
        Color.argb(255, 200, 215, 240), PorterDuff.Mode.SRC_IN
    )
    private val reusableSrcRect = Rect()
    private val reusableDstRect = RectF()

    private val particleHandler = Handler(Looper.getMainLooper())
    private var isParticleAnimating = false

    private val particleRunnable = object : Runnable {
        override fun run() {
            if (!isParticleAnimating || !isAttachedToWindow) return
            invalidate()
            particleHandler.postDelayed(this, 33L)
        }
    }

    init {
        clipChildren = true
        clipToPadding = true
        isChildrenDrawingOrderEnabled = true
        loadParticleBitmaps()
    }

    private fun loadParticleBitmaps() {
        try {
            val rd = ContextCompat.getDrawable(context, R.drawable.raindrop_bulb)
            if (rd is BitmapDrawable) raindropBitmap = rd.bitmap
        } catch (_: Exception) {}
        try {
            val sf1 = ContextCompat.getDrawable(context, R.drawable.snowflake_1)
            if (sf1 is BitmapDrawable) snowflakeBitmap1 = sf1.bitmap
        } catch (_: Exception) {}
        try {
            val sf2 = ContextCompat.getDrawable(context, R.drawable.snowflake_2)
            if (sf2 is BitmapDrawable) snowflakeBitmap2 = sf2.bitmap
        } catch (_: Exception) {}
    }

    fun setWeatherCondition(condition: String) {
        showRainSplash = condition.startsWith("rain") ||
                condition.startsWith("heavy_rain") ||
                condition.startsWith("thunderstorm") ||
                condition.startsWith("hail") ||
                condition.startsWith("light_rain") ||
                condition.startsWith("drizzle") ||
                condition.startsWith("light_drizzle") ||
                condition.startsWith("freezing_drizzle") ||
                condition.startsWith("sleet")

        showSnowCling = condition.startsWith("snow") ||
                condition.startsWith("light_snow") ||
                condition.startsWith("heavy_snow") ||
                condition.startsWith("sleet")

        if (pinOffset > 0f) {
            regenerateParticles()
            if (showRainSplash || showSnowCling) {
                startParticleAnimation()
            } else {
                stopParticleAnimation()
                splashParticles.clear()
                clingParticles.clear()
            }
        }
    }

    private fun startParticleAnimation() {
        if (isParticleAnimating) return
        if (!showRainSplash && !showSnowCling) return
        isParticleAnimating = true
        particleHandler.post(particleRunnable)
    }

    private fun stopParticleAnimation() {
        isParticleAnimating = false
        particleHandler.removeCallbacks(particleRunnable)
    }

    private fun regenerateParticles() {
        val w = width.toFloat()
        if (w <= 0f) return

        if (showRainSplash) {
            splashParticles.clear()
            val now = System.currentTimeMillis()
            val count = (8 + Random.nextInt(6)).coerceAtMost(14)
            for (i in 0 until count) {
                splashParticles.add(SplashParticle(
                    x = Random.nextFloat() * w,
                    phase = 0f,
                    maxBounceH = (6f + Random.nextFloat() * 10f) * dp,
                    durationMs = 600f + Random.nextFloat() * 600f,
                    spawnTime = now - (Random.nextFloat() * 1200f).toLong(),
                    dropSize = (3f + Random.nextFloat() * 4f) * dp,
                    opacity = 0.4f + Random.nextFloat() * 0.35f
                ))
            }
        }

        if (showSnowCling) {
            clingParticles.clear()
            val initialCount = 4
            lastClingCount = initialCount
            for (i in 0 until initialCount) {
                addClingParticle(w)
            }
        }
    }

    private fun addClingParticle(w: Float) {
        clingParticles.add(ClingParticle(
            x = dp * 8f + Random.nextFloat() * (w - dp * 16f),
            yOffset = Random.nextFloat() * 3f * dp,
            size = (4f + Random.nextFloat() * 6f) * dp,
            rotation = Random.nextFloat() * 360f,
            rotationOsc = 8f + Random.nextFloat() * 12f,
            oscSpeed = 0.3f + Random.nextFloat() * 0.5f,
            oscPhase = Random.nextFloat() * 2f * PI.toFloat(),
            opacity = 0.5f + Random.nextFloat() * 0.4f,
            imageIndex = if (Random.nextBoolean()) 0 else 1
        ))
    }

    private fun growSnowAccumulation() {
        val w = width.toFloat()
        if (w <= 0f) return
        val maxCling = 20
        val scrollFraction = (pinOffset / (height.toFloat().coerceAtLeast(1f))).coerceIn(0f, 1f)
        val targetCount = (4 + (scrollFraction * (maxCling - 4))).toInt().coerceAtMost(maxCling)
        if (targetCount > lastClingCount) {
            val toAdd = targetCount - lastClingCount
            for (i in 0 until toAdd) {
                addClingParticle(w)
            }
            lastClingCount = targetCount
        }
    }

    override fun onDetachedFromWindow() {
        stopParticleAnimation()
        super.onDetachedFromWindow()
    }

    override fun onWindowVisibilityChanged(visibility: Int) {
        super.onWindowVisibilityChanged(visibility)
        if (visibility != VISIBLE) {
            stopParticleAnimation()
        } else if (pinOffset > 0f && (showRainSplash || showSnowCling)) {
            startParticleAnimation()
        }
    }

    fun getHeaderLayoutBottom(): Int {
        val count = stickyHeaderCount.coerceAtMost(childCount)
        if (count == 0) return 0
        return getChildAt(count - 1).bottom
    }

    override fun getChildDrawingOrder(childCount: Int, drawingPosition: Int): Int {
        if (pinOffset <= 0f || childCount <= stickyHeaderCount) return drawingPosition

        val contentCount = childCount - stickyHeaderCount
        return if (drawingPosition < contentCount) {
            drawingPosition + stickyHeaderCount
        } else {
            drawingPosition - contentCount
        }
    }

    private var lottieEffectView: CardWeatherEffectView? = null

    fun setLottieEffectView(effectView: CardWeatherEffectView) {
        lottieEffectView?.let { removeView(it) }
        lottieEffectView = effectView
        effectView.visibility = View.GONE
        addView(effectView, LayoutParams(LayoutParams.MATCH_PARENT, 0))
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        val ev = lottieEffectView ?: return
        val headerBottom = getHeaderLayoutBottom()
        if (headerBottom > 0) {
            ev.layout(0, 0, r - l, headerBottom)
        }
    }

    fun setCardOpacityAlpha(alpha: Int) {
        background?.mutate()?.alpha = alpha.coerceIn(0, 255)
        invalidate()
    }

    override fun draw(canvas: Canvas) {
        val bg = background
        if (pinOffset > 0f && bg != null) {
            val headerVisualBottom = getHeaderLayoutBottom() + pinOffset

            bg.setBounds(0, 0, width, height)

            tempRect.set(0f, 0f, width.toFloat(), height.toFloat())
            clipPath.reset()
            clipPath.addRoundRect(tempRect, cornerRadius, cornerRadius, Path.Direction.CW)

            canvas.save()
            canvas.clipPath(clipPath)
            canvas.clipRect(0f, headerVisualBottom, width.toFloat(), height.toFloat())
            bg.draw(canvas)
            canvas.restore()

            canvas.save()
            canvas.clipPath(clipPath)
            canvas.clipRect(0f, pinOffset, width.toFloat(), headerVisualBottom)
            bg.draw(canvas)
            canvas.restore()

            dispatchDraw(canvas)

            val ev = lottieEffectView
            if (ev != null && ev.visibility == View.VISIBLE) {
                canvas.save()
                canvas.clipPath(clipPath)
                canvas.clipRect(0f, pinOffset, width.toFloat(), headerVisualBottom)
                canvas.translate(0f, pinOffset)
                ev.draw(canvas)
                canvas.restore()
            }

            if (showRainSplash || showSnowCling) {
                canvas.save()
                canvas.clipPath(clipPath)
                drawHeaderParticles(canvas)
                canvas.restore()
            }

            onDrawForeground(canvas)
            return
        }

        super.draw(canvas)
    }

    private fun drawHeaderParticles(canvas: Canvas) {
        val now = System.currentTimeMillis()
        val surfaceY = pinOffset

        if (showRainSplash) {
            drawRainSplashes(canvas, surfaceY, now)
        }
        if (showSnowCling) {
            drawSnowCling(canvas, surfaceY, now)
        }
    }

    private fun drawRainSplashes(canvas: Canvas, surfaceY: Float, now: Long) {
        val bitmap = raindropBitmap ?: return
        val w = width.toFloat()

        for (splash in splashParticles) {
            val elapsed = (now - splash.spawnTime).toFloat()
            val phase = (elapsed % splash.durationMs) / splash.durationMs

            if (elapsed > splash.durationMs && phase < 0.05f) {
                splash.x = Random.nextFloat() * w
                splash.maxBounceH = (6f + Random.nextFloat() * 10f) * dp
                splash.dropSize = (3f + Random.nextFloat() * 4f) * dp
                splash.opacity = 0.4f + Random.nextFloat() * 0.35f
            }

            val bounceY: Float
            val alpha: Float

            when {
                phase < 0.35f -> {
                    val t = phase / 0.35f
                    bounceY = splash.maxBounceH * sin(t * PI.toFloat())
                    alpha = splash.opacity
                }
                phase < 0.6f -> {
                    val t = (phase - 0.35f) / 0.25f
                    bounceY = splash.maxBounceH * 0.35f * sin(t * PI.toFloat())
                    alpha = splash.opacity * 0.8f
                }
                phase < 0.78f -> {
                    val t = (phase - 0.6f) / 0.18f
                    bounceY = splash.maxBounceH * 0.12f * sin(t * PI.toFloat())
                    alpha = splash.opacity * 0.5f
                }
                else -> {
                    bounceY = 0f
                    alpha = splash.opacity * (1f - (phase - 0.78f) / 0.22f).coerceAtLeast(0f) * 0.3f
                }
            }

            if (alpha < 0.01f) continue

            val drawY = surfaceY - bounceY
            val halfSize = splash.dropSize / 2f

            particlePaint.alpha = (alpha * 255).toInt()
            particlePaint.colorFilter = rainTintFilter

            reusableSrcRect.set(0, 0, bitmap.width, bitmap.height)
            reusableDstRect.set(
                splash.x - halfSize * 0.35f,
                drawY - halfSize,
                splash.x + halfSize * 0.35f,
                drawY + halfSize
            )
            canvas.drawBitmap(bitmap, reusableSrcRect, reusableDstRect, particlePaint)
        }
        particlePaint.colorFilter = null
    }

    private fun drawSnowCling(canvas: Canvas, surfaceY: Float, now: Long) {
        val bmp1 = snowflakeBitmap1
        val bmp2 = snowflakeBitmap2
        if (bmp1 == null && bmp2 == null) return

        val timeSeconds = now / 1000f

        for (cling in clingParticles) {
            val oscAngle = sin(timeSeconds * cling.oscSpeed + cling.oscPhase) * cling.rotationOsc
            val drawRotation = cling.rotation + oscAngle

            val drawY = surfaceY + cling.yOffset
            val halfSize = cling.size / 2f

            particlePaint.alpha = (cling.opacity * 255).toInt()

            val bitmap = if (cling.imageIndex == 0 && bmp1 != null) bmp1
                         else bmp2 ?: bmp1 ?: continue

            canvas.save()
            canvas.translate(cling.x, drawY)
            canvas.rotate(drawRotation)

            reusableSrcRect.set(0, 0, bitmap.width, bitmap.height)
            reusableDstRect.set(-halfSize, -halfSize, halfSize, halfSize)
            canvas.drawBitmap(bitmap, reusableSrcRect, reusableDstRect, particlePaint)

            canvas.restore()
        }
    }

    override fun drawChild(canvas: Canvas, child: View, drawingTime: Long): Boolean {
        val childIndex = indexOfChild(child)
        if (childIndex >= stickyHeaderCount && pinOffset > 0f) {
            val clipTop = getHeaderLayoutBottom() + pinOffset
            canvas.save()
            canvas.clipRect(0f, clipTop, width.toFloat(), height.toFloat())
            val result = super.drawChild(canvas, child, drawingTime)
            canvas.restore()
            return result
        }
        return super.drawChild(canvas, child, drawingTime)
    }
}
