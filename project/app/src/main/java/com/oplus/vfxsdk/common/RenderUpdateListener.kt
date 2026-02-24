package com.oplus.vfxsdk.common

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

class RenderUpdateListener(val flush: () -> Unit) {
    val listeners: CopyOnWriteArrayList<Animator> = CopyOnWriteArrayList()
    val playingAnim: CopyOnWriteArrayList<Animator> = CopyOnWriteArrayList()
    val animEndListenerMap: ConcurrentHashMap<String, AnimEndListener> = ConcurrentHashMap()

    fun addUpdate(anim: Animator) {
        if (!listeners.contains(anim)) {
            listeners.add(anim)
        }
    }

    fun removeUpdate(anim: Animator) {
        listeners.remove(anim)
        playingAnim.remove(anim)
    }

    fun update(time: Double) {
        for (anim in listeners) {
            if (anim.status == Animator.AnimaStatus.Playing) {
                if (!playingAnim.contains(anim)) {
                    playingAnim.add(anim)
                }
            }
        }
        flush()
    }
}
