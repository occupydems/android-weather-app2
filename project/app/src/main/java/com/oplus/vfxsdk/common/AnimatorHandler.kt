package com.oplus.vfxsdk.common

import android.view.Choreographer
import java.util.concurrent.CopyOnWriteArrayList

object AnimatorHandler : Choreographer.FrameCallback {
    private val animators: CopyOnWriteArrayList<Animator> = CopyOnWriteArrayList()
    private var isRunning = false

    fun addAnimator(animator: Animator) {
        if (!animators.contains(animator)) {
            animators.add(animator)
        }
        startIfNeeded()
    }

    fun removeAnimator(animator: Animator) {
        animators.remove(animator)
        if (animators.isEmpty()) {
            isRunning = false
        }
    }

    private fun startIfNeeded() {
        if (!isRunning && animators.isNotEmpty()) {
            isRunning = true
            Choreographer.getInstance().postFrameCallback(this)
        }
    }

    override fun doFrame(frameTimeNanos: Long) {
        for (animator in animators) {
            animator.onFrame(frameTimeNanos)
        }
        if (isRunning && animators.isNotEmpty()) {
            Choreographer.getInstance().postFrameCallback(this)
        }
    }
}
