package com.oplus.vfxsdk.naive.coe.engine

import com.oplus.vfxsdk.common.AbsAnimator
import com.oplus.vfxsdk.common.IAnimator
import com.oplus.vfxsdk.common.Layer
import com.oplus.vfxsdk.naive.coe.api.IUpdate
import com.oplus.vfxsdk.naive.coe.engine.Event.Event

open class BaseRenderer(
    @JvmField val _engine: NativeEngine,
    val nativeRenderer: NativeRenderer,
    var layer: Layer? = null,
    var enable: Boolean = true,
    var sortingOrder: Int = 0,
    var layerOrder: Int = 0,
    var blendEnable: Boolean = false,
    var blendSrcFactor: Int = 0,
    var blendDstFactor: Int = 0
) : NativeCallback, IAnimator {

    val passList: ArrayList<Pass> = ArrayList()
    @JvmField
    var _animator: AbsAnimator? = null
    val updateListenerArray: ArrayList<IUpdate> = ArrayList()

    private var callbackId: Int = 0

    fun addPass(pass: Pass): Int {
        passList.add(pass)
        return passList.size - 1
    }

    fun addComponent(obj: NativeObject) {
        _engine.mScene.addComponent(obj)
    }

    open fun flush() {
    }

    override fun getAnimator(): AbsAnimator? = _animator

    open fun getAnimators(): HashMap<String, Any>? = null

    open fun getAllTrigers(): HashMap<String, Any>? = _animator?.getAllTriggers()

    open fun getAllTrigerState(): List<Any>? = _animator?.getAllTriggerStates()

    fun addUpdateListener(listener: IUpdate) {
        if (!updateListenerArray.contains(listener)) {
            updateListenerArray.add(listener)
        }
    }

    fun removeUpdateListener(listener: IUpdate) {
        updateListenerArray.remove(listener)
    }

    override fun getID(): Int = callbackId

    override fun onStart() {}

    override fun onUpdate(time: Double) {
        for (listener in updateListenerArray) {
            listener.onUpdate(time)
        }
    }

    override fun onAnimStart(name: String) {}

    override fun onAnimEnd(name: String) {}

    override fun onAnimUpdate(name: String, time: Double, vararg values: Float) {}

    override fun onEvent(event: Event) {}

    override fun onRenderStart() {}

    override fun onRenderQuit() {}
}
