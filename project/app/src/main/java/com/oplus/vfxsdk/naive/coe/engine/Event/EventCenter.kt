package com.oplus.vfxsdk.naive.coe.engine.Event

import java.util.concurrent.CopyOnWriteArrayList

class EventCenter {
    interface Listener {
        fun onEvent(event: Event)
    }

    private val listeners: CopyOnWriteArrayList<Listener> = CopyOnWriteArrayList()

    fun postEvent(event: Event) {
        for (listener in listeners) {
            listener.onEvent(event)
        }
    }

    fun addListener(listener: Listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener)
        }
    }

    fun removeListener(listener: Listener) {
        listeners.remove(listener)
    }
}
