package com.oplus.vfxsdk.common

import android.view.Choreographer

open class Animator(
    var id: Int,
    var name: String,
    var animLines: Array<AnimLine>,
    var status: AnimaStatus = AnimaStatus.Idle
) {
    enum class AnimaStatus {
        Idle,
        Playing,
        Paused,
        Stopped
    }

    private var speed: Float = 1.0f
    private var currentTime: Double = 0.0
    private var lastFrameTime: Long = 0L
    private var frameCallback: Choreographer.FrameCallback? = null

    inner class AnimatorLine(
        val animLine: AnimLine,
        var currentTime: Double = 0.0
    ) {
        fun seekTo(time: Double) {
            currentTime = time.coerceIn(0.0, animLine.duration.toDouble())
        }

        fun getValue(): Float {
            if (animLine.keys.isEmpty()) return 0f
            if (animLine.keys.size == 1) return animLine.keys[0].getValue()
            val t = currentTime.toFloat()
            for (i in 0 until animLine.keys.size - 1) {
                val k0 = animLine.keys[i]
                val k1 = animLine.keys[i + 1]
                if (t >= k0.getTime() && t <= k1.getTime()) {
                    val dt = k1.getTime() - k0.getTime()
                    if (dt <= 0f) return k0.getValue()
                    val frac = (t - k0.getTime()) / dt
                    return k0.getValue() + (k1.getValue() - k0.getValue()) * frac
                }
            }
            return animLine.keys.last().getValue()
        }
    }

    open fun play() {
        status = AnimaStatus.Playing
        lastFrameTime = System.nanoTime()
        AnimatorHandler.addAnimator(this)
    }

    open fun pause() {
        status = AnimaStatus.Paused
        AnimatorHandler.removeAnimator(this)
    }

    open fun stop() {
        status = AnimaStatus.Stopped
        currentTime = 0.0
        AnimatorHandler.removeAnimator(this)
    }

    open fun seekTo(time: Double) {
        currentTime = time
    }

    fun setSpeed(speed: Float) {
        this.speed = speed
    }

    fun getSpeed(): Float = speed

    fun getCurrentTime(): Double = currentTime

    open fun onFrame(frameTimeNanos: Long) {
        if (status != AnimaStatus.Playing) return
        val dt = if (lastFrameTime > 0) {
            (frameTimeNanos - lastFrameTime) / 1_000_000_000.0
        } else {
            0.0
        }
        lastFrameTime = frameTimeNanos
        currentTime += dt * speed
    }
}
