package com.oplus.vfxsdk.naive

import android.content.Context
import android.view.Surface
import com.oplus.vfxsdk.common.Animator
import com.oplus.vfxsdk.common.AnimatorValue
import com.oplus.vfxsdk.common.COEExpressions
import com.oplus.vfxsdk.common.Layer
import com.oplus.vfxsdk.common.RenderUpdateListener
import com.oplus.vfxsdk.naive.coe.api.ICOEEngine
import com.oplus.vfxsdk.naive.coe.api.ICOEEngineListener
import com.oplus.vfxsdk.naive.coe.engine.NativeCallback
import com.oplus.vfxsdk.naive.coe.engine.NativeEngine
import com.oplus.vfxsdk.naive.coe.model.COEOptions
import com.oplus.vfxsdk.naive.parse.COEAnimator

class COEEngine(
    val context: Context,
    val options: COEOptions = COEOptions()
) : ICOEEngine {

    @JvmField
    val _engine: NativeEngine = NativeEngine(options.caps)
    var surface: Surface? = null
    var surfaceWidth: Int = 0
    var surfaceHeight: Int = 0
    val layers: ArrayList<Layer> = ArrayList()
    val sceneIdList: ArrayList<String> = ArrayList()
    var isPlaying: Boolean = false
    var globalAnimators: COEAnimator = COEAnimator(0, "global", emptyArray())
    val renderUpdateListener: RenderUpdateListener = RenderUpdateListener { flush() }
    var coeEngineListener: ICOEEngineListener? = null
    var expression: COEExpressions? = null

    init {
        doInit()
    }

    private fun doInit() {
        _engine.attachAssetManager(context.assets)
        _engine.attachClassLoader(context.classLoader)
        _engine.setFilesCachePath(context.cacheDir.absolutePath)
        _engine.setRenderThreadName(options.renderThreadName)
        coeEngineListener?.onInit()
    }

    override fun loadCOE2(fileName: String, cacheData: Boolean, async: Boolean): Int {
        val effectId = _engine.loadEffect(fileName, cacheData)
        if (effectId >= 0) {
            sceneIdList.add(fileName)
            coeEngineListener?.onLoaded()
        }
        return effectId
    }

    fun play() {
        isPlaying = true
        _engine.startEngine()
        coeEngineListener?.onStart()
    }

    fun pause() {
        isPlaying = false
        _engine.stopEngine()
    }

    fun stop() {
        isPlaying = false
        _engine.stopEngine()
    }

    fun destroy() {
        stop()
        _engine.removeCallbacks()
        _engine.destroyEngine()
        coeEngineListener?.onDestroyed()
    }

    fun setSurface(surface: Surface, width: Int, height: Int) {
        this.surface = surface
        surfaceWidth = width
        surfaceHeight = height
        _engine.setSurface(surface, true)
        _engine.setViewport(width, height)
        _engine.setScreenSize(width, height)
        coeEngineListener?.onSurfaceChange()
    }

    fun setViewport(width: Int, height: Int) {
        surfaceWidth = width
        surfaceHeight = height
        _engine.setViewport(width, height)
    }

    fun getEngine(): NativeEngine = _engine

    fun getAnimator(name: String): Animator? {
        if (globalAnimators.name == name) return globalAnimators
        return null
    }

    fun createAnimator(value: AnimatorValue, sceneId: String, layerName: String?): Animator {
        val animator = COEAnimator(value.id, value.name, value.animLines)
        animator.sceneId = sceneId
        animator.layerName = layerName
        return animator
    }

    fun triggerEvent(name: String, value: Any?) {
        val event = com.oplus.vfxsdk.naive.coe.engine.Event.Event(name, name, value)
        _engine.postEvent(event)
    }

    fun setParameter(name: String, value: Any?) {
    }

    fun addCallback(callback: NativeCallback) {
        _engine.addCallback(callback)
    }

    private fun flush() {
    }
}
