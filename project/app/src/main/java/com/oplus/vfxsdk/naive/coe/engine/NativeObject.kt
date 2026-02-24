package com.oplus.vfxsdk.naive.coe.engine

abstract class NativeObject {
    @Volatile
    protected var mNativeHandle: Long = 0L
    var mEngine: NativeEngine? = null
    protected val mLock: Any = Object()

    companion object {
        @JvmStatic
        var libraryFound: Boolean = false
            private set

        @JvmStatic
        fun loadLibrary() {
            if (libraryFound) return
            try {
                System.loadLibrary("naiveEngine")
                libraryFound = true
            } catch (e: UnsatisfiedLinkError) {
                libraryFound = false
            }
        }

        @JvmStatic
        private external fun nativeRelease(engineHandle: Long, objectHandle: Long)

        @JvmStatic
        private external fun nativeRetain(engineHandle: Long, objectHandle: Long)
    }

    fun getNativeHandle(): Long = mNativeHandle

    fun setNativeHandle(handle: Long) {
        mNativeHandle = handle
    }

    open fun release() {
        synchronized(mLock) {
            if (mNativeHandle != 0L && mEngine != null) {
                val engineHandle = mEngine!!.getNativeHandle()
                if (engineHandle != 0L) {
                    nativeRelease(engineHandle, mNativeHandle)
                }
                mNativeHandle = 0L
            }
        }
    }

    fun retain() {
        synchronized(mLock) {
            if (mNativeHandle != 0L && mEngine != null) {
                val engineHandle = mEngine!!.getNativeHandle()
                if (engineHandle != 0L) {
                    nativeRetain(engineHandle, mNativeHandle)
                }
            }
        }
    }

    fun CheckEngine(): Boolean {
        return mEngine != null && mEngine!!.getNativeHandle() != 0L
    }

    fun CheckHandle(): Boolean {
        return mNativeHandle != 0L
    }

    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated("Deprecated in Java")
    protected fun finalize() {
        release()
    }
}
