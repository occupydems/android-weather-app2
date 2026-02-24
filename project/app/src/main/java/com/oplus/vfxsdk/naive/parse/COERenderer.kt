package com.oplus.vfxsdk.naive.parse

import com.oplus.vfxsdk.common.COEData
import com.oplus.vfxsdk.common.Layer
import com.oplus.vfxsdk.common.Uniform
import com.oplus.vfxsdk.naive.coe.api.IAnimListener
import com.oplus.vfxsdk.naive.coe.api.ICOEEngineListener
import com.oplus.vfxsdk.naive.coe.api.IUpdate
import com.oplus.vfxsdk.naive.coe.engine.BaseRenderer
import com.oplus.vfxsdk.naive.coe.engine.NativeEngine
import com.oplus.vfxsdk.naive.coe.engine.NativeRenderer
import com.oplus.vfxsdk.naive.coe.engine.TexCreateInfo

class COERenderer(
    engine: NativeEngine,
    nativeRenderer: NativeRenderer,
    val coeData: COEData,
    val layerIndex: Int = 0
) : BaseRenderer(engine, nativeRenderer) {

    var width: Float = 0f
    var height: Float = 0f
    var layout: FloatArray = floatArrayOf(0f, 0f, 0f, 0f)
    var scale: FloatArray = floatArrayOf(1f, 1f, 1f)
    var rotation: FloatArray = floatArrayOf(0f, 0f, 0f)
    var layoutWidth: Float = 0f
    var layoutHeight: Float = 0f
    var rt: HashMap<String, Any> = HashMap()
    var statusAnim: HashMap<String, Any> = HashMap()
    var iResolution: ArrayList<Float> = ArrayList()
    @JvmField
    var _animListener: IAnimListener? = null
    @JvmField
    var _animListenedList: Array<String>? = null
    var animTracker: HashMap<String, Any> = HashMap()
    var cacheData: Boolean = true
    var renderListener: ICOEEngineListener? = null

    val builtinUpdate: IUpdate = object : IUpdate {
        override fun onUpdate(time: Double) {
        }
    }

    fun initRenderPass() {
        if (layerIndex >= coeData.layers.size) return
        val layerData = coeData.layers[layerIndex]
        layer = layerData
        enable = layerData.enable
        blendEnable = layerData.enableBlend
        blendSrcFactor = layerData.blendSfactor
        blendDstFactor = layerData.blendDfactor
        sortingOrder = layerData.order
    }

    fun initTransform() {
        if (layerIndex >= coeData.layers.size) return
        val layerData = coeData.layers[layerIndex]
        val transform = layerData.transform ?: return
        transform.layout?.let { layout = it }
        transform.scale?.let { scale = it }
        transform.rotation?.let { rotation = it }
    }

    fun initPostProcessor() {
    }

    fun initParticle() {
    }

    fun updateTransform() {
        val matrix = FloatArray(16)
        android.opengl.Matrix.setIdentityM(matrix, 0)
        android.opengl.Matrix.scaleM(matrix, 0, scale[0], scale[1], scale[2])
        _engine.createTransform(matrix)
    }

    fun setAnimListener(listener: IAnimListener?) {
        _animListener = listener
    }

    fun setAnimListenedList(list: Array<String>?) {
        _animListenedList = list
        if (list != null) {
            _engine.setAnimListenedList(list)
        }
    }

    fun createTexture(info: TexCreateInfo): Long {
        return _engine.createTexture(info)
    }

    fun createTexture(uniform: Uniform): Long {
        val info = TexCreateInfo()
        info.width = uniform.width
        info.height = uniform.height
        return _engine.createTexture(info)
    }

    fun layoutExpr() {
    }
}
