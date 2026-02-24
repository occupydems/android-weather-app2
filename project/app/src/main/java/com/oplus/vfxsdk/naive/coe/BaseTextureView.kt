package com.oplus.vfxsdk.naive.coe

import android.app.Activity
import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.graphics.SurfaceTexture
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import android.util.AttributeSet
import android.view.Surface
import android.view.TextureView
import com.oplus.vfxsdk.common.Layer
import com.oplus.vfxsdk.naive.coe.api.ICOEEngineListener
import com.oplus.vfxsdk.naive.coe.engine.BaseRenderer
import com.oplus.vfxsdk.naive.coe.engine.NativeEngine
import com.oplus.vfxsdk.naive.coe.engine.TexCreateInfo

abstract class BaseTextureView : TextureView, TextureView.SurfaceTextureListener,
    Application.ActivityLifecycleCallbacks {

    @JvmField
    var _engine: NativeEngine? = null
    protected var _renderer: BaseRenderer? = null
    protected var _caps: Int = 0
    var surface: Surface? = null
    var surfaceTex: SurfaceTexture? = null
    var surfaceWidth: Int = 0
    var surfaceHeight: Int = 0
    var surfaceWidthLimit: Int = 0
    var surfaceHeightLimit: Int = 0
    var surfaceSizeRadio: Double = 1.0
    var isPlaying: Boolean = false
    protected var _autoGC: Boolean = false
    var layers: ArrayList<Layer>? = null
    var coeEngineListener: ICOEEngineListener? = null
    val mainHandler: Handler = Handler(Looper.getMainLooper())
    var powerManager: PowerManager? = null
    var screenStateReceiver: BroadcastReceiver? = null
    var textureViewInitTime: Long = 0L

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    private fun init(context: Context) {
        textureViewInitTime = System.currentTimeMillis()
        surfaceTextureListener = this
        isOpaque = false
        powerManager = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
    }

    abstract fun initRenderer()

    open fun createRenderPass(
        name: String,
        type: String,
        mode: Int,
        flag: Boolean,
        width: Int,
        height: Int
    ) {
        _engine?.createRenderObject(name, type, mode, flag, null, width, height)
    }

    open fun createTexture(info: TexCreateInfo): Long {
        return _engine?.createTexture(info) ?: 0L
    }

    open fun play() {
        isPlaying = true
        _engine?.startEngine()
    }

    open fun pause() {
        isPlaying = false
        _engine?.stopEngine()
    }

    open fun stop() {
        isPlaying = false
        _engine?.stopEngine()
    }

    open fun destroy() {
        stop()
        _engine?.destroyEngine()
        _engine = null
        surface?.release()
        surface = null
    }

    fun getEngine(): NativeEngine? = _engine

    fun setAutoGC(autoGC: Boolean) {
        _autoGC = autoGC
    }

    override fun onSurfaceTextureAvailable(surfaceTexture: SurfaceTexture, width: Int, height: Int) {
        surfaceTex = surfaceTexture
        surfaceWidth = width
        surfaceHeight = height
        surface = Surface(surfaceTexture)
        _engine?.setSurface(surface!!, true)
        _engine?.setViewport(width, height)
        _engine?.setScreenSize(width, height)
        coeEngineListener?.onSurfaceChange()
    }

    override fun onSurfaceTextureUpdated(surfaceTexture: SurfaceTexture) {
    }

    override fun onSurfaceTextureDestroyed(surfaceTexture: SurfaceTexture): Boolean {
        stop()
        surface?.release()
        surface = null
        return true
    }

    override fun onSurfaceTextureSizeChanged(surfaceTexture: SurfaceTexture, width: Int, height: Int) {
        surfaceWidth = width
        surfaceHeight = height
        _engine?.setViewport(width, height)
        _engine?.setScreenSize(width, height)
        coeEngineListener?.onSurfaceChange()
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
    override fun onActivityStarted(activity: Activity) {}
    override fun onActivityResumed(activity: Activity) {
        if (isPlaying) play()
    }

    override fun onActivityPaused(activity: Activity) {
        pause()
    }

    override fun onActivityStopped(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {
        destroy()
    }
}
