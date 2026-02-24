package com.oplus.vfxsdk.naive.coe.engine

import com.oplus.vfxsdk.naive.coe.engine.Event.Event

interface NativeCallback {
    fun getID(): Int
    fun onStart()
    fun onUpdate(time: Double)
    fun onAnimStart(name: String)
    fun onAnimEnd(name: String)
    fun onAnimUpdate(name: String, time: Double, vararg values: Float)
    fun onEvent(event: Event)

    fun onRenderStart() {}
    fun onRenderQuit() {}
}
