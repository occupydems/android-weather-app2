package com.oplus.vfxsdk.naive.coe.engine

class NativeRenderer(engine: NativeEngine, mode: Int, flag: Boolean) : NativeObject() {
    init {
        mEngine = engine
        mNativeHandle = engine.createRenderer(mode, flag)
    }
}
