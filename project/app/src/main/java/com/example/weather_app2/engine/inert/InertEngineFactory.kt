package com.example.weather_app2.engine.inert

import android.util.Log

object InertEngineFactory {

    private const val TAG = "InertEngineFactory"
    private const val NATIVE_LIB_NAME = "naiveEngine"

    @Volatile
    private var engine: InertRenderEngine? = null

    @Volatile
    private var nativeAvailable: Boolean? = null

    fun isNativeAvailable(): Boolean {
        if (nativeAvailable != null) return nativeAvailable!!
        synchronized(this) {
            if (nativeAvailable != null) return nativeAvailable!!
            nativeAvailable = try {
                System.loadLibrary(NATIVE_LIB_NAME)
                Log.i(TAG, "Native library '$NATIVE_LIB_NAME' loaded successfully")
                true
            } catch (e: UnsatisfiedLinkError) {
                Log.w(TAG, "Native library '$NATIVE_LIB_NAME' not available: ${e.message}")
                false
            } catch (e: SecurityException) {
                Log.w(TAG, "Security exception loading '$NATIVE_LIB_NAME': ${e.message}")
                false
            }
            return nativeAvailable!!
        }
    }

    fun getInertEngine(): InertRenderEngine {
        if (engine != null) return engine!!
        synchronized(this) {
            if (engine != null) return engine!!
            val newEngine = InertRenderEngine()
            if (!isNativeAvailable()) {
                Log.i(TAG, "Using InertRenderEngine as fallback (native unavailable)")
                newEngine.activate()
            } else {
                Log.i(TAG, "Native engine available; InertRenderEngine created but dormant")
            }
            engine = newEngine
            return newEngine
        }
    }

    fun getEngineType(): String {
        return if (isNativeAvailable()) "native" else "inert"
    }

    fun reset() {
        synchronized(this) {
            engine?.destroy()
            engine = null
            nativeAvailable = null
        }
    }
}
