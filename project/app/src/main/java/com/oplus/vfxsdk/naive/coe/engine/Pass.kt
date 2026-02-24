package com.oplus.vfxsdk.naive.coe.engine

class Pass(
    engine: NativeEngine,
    vertex: String,
    fragment: String,
    flag: Boolean,
    mode: Int,
    renderState: RenderStateInfo?
) : NativeObject() {
    @JvmField
    var _material: Material? = null
    @JvmField
    var _mesh: Mesh? = null

    init {
        mEngine = engine
        mNativeHandle = engine.createPass(vertex, fragment, flag, mode, renderState)
    }

    fun enableBlend(enable: Boolean) {
    }

    fun getMaterial(): Material? = _material

    fun getMesh(): Mesh? = _mesh
}
