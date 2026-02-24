package com.example.weather_app2.engine.inert

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class InertParticleSystem {

    enum class ParticleType { RAIN, SNOW, DUST }

    data class Particle(
        var x: Float,
        var y: Float,
        var velocityX: Float,
        var velocityY: Float,
        var size: Float,
        var opacity: Float,
        var lifetime: Float,
        var age: Float = 0f
    )

    private var particles = mutableListOf<Particle>()
    private var type: ParticleType = ParticleType.RAIN
    private var maxCount: Int = 0
    private var gravity: Float = 980f
    private var windX: Float = 0f
    private var windY: Float = 0f
    private var canvasWidth: Float = 1080f
    private var canvasHeight: Float = 1920f
    private var isInitialized: Boolean = false

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    fun init(type: ParticleType, count: Int) {
        this.type = type
        this.maxCount = count
        particles.clear()
        isInitialized = true
        for (i in 0 until count) {
            particles.add(spawnParticle(randomizeAge = true))
        }
    }

    fun setCanvasSize(width: Float, height: Float) {
        canvasWidth = width
        canvasHeight = height
    }

    fun setGravity(g: Float) {
        gravity = g
    }

    fun setWind(wx: Float, wy: Float) {
        windX = wx
        windY = wy
    }

    fun update(deltaTime: Float) {
        if (!isInitialized) return
        val dt = deltaTime.coerceIn(0f, 0.05f)

        val iterator = particles.listIterator()
        while (iterator.hasNext()) {
            val p = iterator.next()
            p.age += dt

            if (p.age >= p.lifetime || p.y > canvasHeight + 50f || p.x < -50f || p.x > canvasWidth + 50f) {
                iterator.set(spawnParticle(randomizeAge = false))
                continue
            }

            when (type) {
                ParticleType.RAIN -> {
                    p.velocityY += gravity * dt
                    p.velocityX += windX * dt * 0.5f
                }
                ParticleType.SNOW -> {
                    p.velocityY += gravity * 0.05f * dt
                    p.velocityX += (windX * 0.3f + sin(p.age * 2f) * 20f) * dt
                }
                ParticleType.DUST -> {
                    p.velocityX += (windX * 0.2f + cos(p.age * 1.5f) * 10f) * dt
                    p.velocityY += (sin(p.age * 0.8f) * 5f) * dt
                }
            }

            p.x += p.velocityX * dt
            p.y += p.velocityY * dt

            val lifeRatio = p.age / p.lifetime
            p.opacity = when {
                lifeRatio < 0.1f -> lifeRatio * 10f
                lifeRatio > 0.8f -> (1f - lifeRatio) * 5f
                else -> 1f
            }.coerceIn(0f, 1f)
        }
    }

    fun draw(canvas: Canvas) {
        if (!isInitialized) return

        for (p in particles) {
            val alpha = (p.opacity * 255).toInt().coerceIn(0, 255)
            if (alpha == 0) continue

            when (type) {
                ParticleType.RAIN -> {
                    paint.color = Color.argb(alpha, 180, 210, 240)
                    paint.strokeWidth = p.size * 0.5f
                    paint.style = Paint.Style.STROKE
                    canvas.drawLine(p.x, p.y, p.x + p.velocityX * 0.01f, p.y + p.size * 2f, paint)
                }
                ParticleType.SNOW -> {
                    paint.color = Color.argb(alpha, 240, 245, 255)
                    paint.style = Paint.Style.FILL
                    canvas.drawCircle(p.x, p.y, p.size, paint)
                }
                ParticleType.DUST -> {
                    paint.color = Color.argb(alpha, 200, 180, 140)
                    paint.style = Paint.Style.FILL
                    canvas.drawCircle(p.x, p.y, p.size * 0.5f, paint)
                }
            }
        }
    }

    fun destroy() {
        particles.clear()
        isInitialized = false
    }

    private fun spawnParticle(randomizeAge: Boolean): Particle {
        return when (type) {
            ParticleType.RAIN -> Particle(
                x = Random.nextFloat() * canvasWidth,
                y = if (randomizeAge) Random.nextFloat() * canvasHeight else -Random.nextFloat() * 100f,
                velocityX = windX * 0.3f + Random.nextFloat() * 10f - 5f,
                velocityY = 200f + Random.nextFloat() * 300f,
                size = 2f + Random.nextFloat() * 3f,
                opacity = 0.4f + Random.nextFloat() * 0.6f,
                lifetime = 1.5f + Random.nextFloat() * 2f,
                age = if (randomizeAge) Random.nextFloat() * 1.5f else 0f
            )
            ParticleType.SNOW -> Particle(
                x = Random.nextFloat() * canvasWidth,
                y = if (randomizeAge) Random.nextFloat() * canvasHeight else -Random.nextFloat() * 50f,
                velocityX = Random.nextFloat() * 30f - 15f,
                velocityY = 20f + Random.nextFloat() * 40f,
                size = 3f + Random.nextFloat() * 6f,
                opacity = 0.5f + Random.nextFloat() * 0.5f,
                lifetime = 5f + Random.nextFloat() * 5f,
                age = if (randomizeAge) Random.nextFloat() * 5f else 0f
            )
            ParticleType.DUST -> Particle(
                x = if (randomizeAge) Random.nextFloat() * canvasWidth else -20f,
                y = Random.nextFloat() * canvasHeight,
                velocityX = 30f + Random.nextFloat() * 50f,
                velocityY = Random.nextFloat() * 20f - 10f,
                size = 2f + Random.nextFloat() * 4f,
                opacity = 0.2f + Random.nextFloat() * 0.4f,
                lifetime = 4f + Random.nextFloat() * 4f,
                age = if (randomizeAge) Random.nextFloat() * 4f else 0f
            )
        }
    }
}
