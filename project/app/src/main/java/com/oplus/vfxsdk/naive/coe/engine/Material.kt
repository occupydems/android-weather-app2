package com.oplus.vfxsdk.naive.coe.engine

import java.util.concurrent.ConcurrentLinkedQueue

class Material(engine: NativeEngine) : NativeObject() {
    val passList: ArrayList<Pass> = ArrayList()
    val pendingParameters: ConcurrentLinkedQueue<Any> = ConcurrentLinkedQueue()

    companion object {
        @JvmStatic
        private external fun nativeInit(engineHandle: Long): Long

        @JvmStatic
        private external fun nativeAddPass(engineHandle: Long, passHandle: Long)

        @JvmStatic
        private external fun nativeSetProperty(engineHandle: Long, name: String, values: Array<Any?>, materialHandle: Long)

        @JvmStatic
        private external fun nativeSetPropertyBatch(engineHandle: Long, params: ArrayList<Any>, materialHandle: Long)
    }

    init {
        mEngine = engine
        val engineHandle = engine.getNativeHandle()
        if (engineHandle != 0L) {
            mNativeHandle = nativeInit(engineHandle)
        }
    }

    fun addPass(pass: Pass): Int {
        passList.add(pass)
        val engineHandle = mEngine?.getNativeHandle() ?: return -1
        if (engineHandle != 0L && pass.getNativeHandle() != 0L) {
            nativeAddPass(engineHandle, pass.getNativeHandle())
        }
        return passList.size - 1
    }

    fun getPass(index: Int): Pass? {
        return if (index in passList.indices) passList[index] else null
    }

    fun getPassCount(): Int = passList.size

    fun setParameter(passIndex: Int, name: String, vararg values: Any?) {
        val engineHandle = mEngine?.getNativeHandle() ?: return
        if (engineHandle != 0L && mNativeHandle != 0L) {
            nativeSetProperty(engineHandle, name, arrayOf(*values), mNativeHandle)
        }
    }

    fun setParameterCache(passIndex: Int, name: String, vararg values: Any?) {
        pendingParameters.add(arrayOf(passIndex, name, *values))
    }

    fun flush() {
        val engineHandle = mEngine?.getNativeHandle() ?: return
        if (engineHandle != 0L && mNativeHandle != 0L && pendingParameters.isNotEmpty()) {
            val params = ArrayList<Any>()
            while (pendingParameters.isNotEmpty()) {
                pendingParameters.poll()?.let { params.add(it) }
            }
            if (params.isNotEmpty()) {
                nativeSetPropertyBatch(engineHandle, params, mNativeHandle)
            }
        }
    }

    fun setRenderTexture(rt: RenderTexture, passIndex: Int) {
        val pass = getPass(passIndex) ?: return
        val engineHandle = mEngine?.getNativeHandle() ?: return
        if (engineHandle != 0L) {
            mEngine?.setRenderTexture(pass, rt)
        }
    }
}
