package com.oplus.vfxsdk.naive.coe.engine

import java.nio.ByteBuffer
import java.nio.ByteOrder

class Mesh(engine: NativeEngine, buffer: ByteBuffer) : NativeObject() {

    companion object {
        @JvmStatic
        val quadMesh: ByteBuffer by lazy {
            val vertices = floatArrayOf(
                -1f, -1f, 0f, 0f, 0f,
                 1f, -1f, 0f, 1f, 0f,
                -1f,  1f, 0f, 0f, 1f,
                 1f,  1f, 0f, 1f, 1f
            )
            val buf = ByteBuffer.allocateDirect(vertices.size * 4)
            buf.order(ByteOrder.nativeOrder())
            buf.asFloatBuffer().put(vertices)
            buf.position(0)
            buf
        }

        @JvmStatic
        private external fun nativeInit(engineHandle: Long, buffer: ByteBuffer): Long

        @JvmStatic
        private external fun nativeUpdateMesh(handle: Long, buffer: ByteBuffer)
    }

    init {
        mEngine = engine
        val engineHandle = engine.getNativeHandle()
        if (engineHandle != 0L) {
            mNativeHandle = nativeInit(engineHandle, buffer)
        }
    }

    fun updateMesh(buffer: ByteBuffer) {
        if (mNativeHandle != 0L) {
            nativeUpdateMesh(mNativeHandle, buffer)
        }
    }
}
