package com.oplus.vfxsdk.naive.parse

import android.content.Context
import android.graphics.SurfaceTexture
import android.util.AttributeSet
import android.view.TextureView
import com.oplus.vfxsdk.naive.coe.BaseTextureView
import com.oplus.vfxsdk.naive.coe.engine.NativeEngine
import java.io.InputStream

open class COEView : BaseTextureView, TextureView.SurfaceTextureListener {

    var fileName: String? = null
    var notifiers: Map<String, Any> = emptyMap()

    private var renderers: ArrayList<COERenderer> = ArrayList()

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    fun load(fileName: String, cacheData: Boolean = true): COEView {
        this.fileName = fileName
        if (_engine == null) {
            _engine = NativeEngine()
        }
        _engine?.attachAssetManager(context.assets)
        _engine?.attachClassLoader(context.classLoader)
        _engine?.setFilesCachePath(context.cacheDir.absolutePath)
        _engine?.loadEffect(fileName, cacheData)
        initRenderer()
        return this
    }

    fun loadAsync(fileName: String, cacheData: Boolean = true): COEView {
        this.fileName = fileName
        if (_engine == null) {
            _engine = NativeEngine()
        }
        _engine?.attachAssetManager(context.assets)
        _engine?.attachClassLoader(context.classLoader)
        _engine?.setFilesCachePath(context.cacheDir.absolutePath)
        _engine?.loadEffect(fileName, cacheData)
        initRenderer()
        return this
    }

    fun loadFromStorageAsync(path: String, cacheData: Boolean = true): COEView {
        this.fileName = path
        if (_engine == null) {
            _engine = NativeEngine()
        }
        _engine?.setFilesCachePath(context.cacheDir.absolutePath)
        _engine?.loadEffect(path, cacheData)
        initRenderer()
        return this
    }

    fun load(inputStream: InputStream, cacheData: Boolean, async: Boolean) {
        val parse = COEParse()
        val data = parse.parse(inputStream)
        if (data != null) {
            if (_engine == null) {
                _engine = NativeEngine()
            }
            _engine?.attachAssetManager(context.assets)
            _engine?.attachClassLoader(context.classLoader)
            _engine?.setFilesCachePath(context.cacheDir.absolutePath)
            val features = data.features
            if (features != null) {
                _engine?.initEngine(features)
            }
            initRenderer()
        }
    }

    fun getRenderer(): COERenderer? {
        return if (renderers.isNotEmpty()) renderers[0] else null
    }

    fun getRenderer(index: Int): COERenderer? {
        return if (index in renderers.indices) renderers[index] else null
    }

    override fun initRenderer() {
        coeEngineListener?.onInit()
    }

    override fun onSurfaceTextureSizeChanged(surfaceTexture: SurfaceTexture, width: Int, height: Int) {
        super.onSurfaceTextureSizeChanged(surfaceTexture, width, height)
        for (renderer in renderers) {
            renderer.updateTransform()
        }
    }
}
