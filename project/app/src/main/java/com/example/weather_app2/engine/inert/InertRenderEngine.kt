package com.example.weather_app2.engine.inert

import android.util.Log

class InertRenderEngine {

    companion object {
        private const val TAG = "InertRenderEngine"
        const val BUILD_FLAG_FORCE_INERT = "com.example.weather_app2.FORCE_INERT_ENGINE"
    }

    @Volatile
    var isActive: Boolean = false
        private set

    private var viewportWidth: Int = 0
    private var viewportHeight: Int = 0
    private var currentScene: String? = null
    private var isPlaying: Boolean = false
    private var isInitialized: Boolean = false
    private val uniforms = mutableMapOf<String, Float>()
    private val lock = Any()

    // Native version: Initializes the OPPO COEEngine OpenGL context, loads shaders,
    // allocates GPU buffers, and prepares the render pipeline for weather scenes.
    fun init(): Boolean = synchronized(lock) {
        if (isInitialized) return true
        Log.d(TAG, "init() — no-op: native version initializes GL context and shader pipeline")
        isInitialized = true
        return true
    }

    // Native version: Configures the GL viewport dimensions and updates projection matrices
    // used by all weather scene shaders.
    fun setViewport(width: Int, height: Int) = synchronized(lock) {
        Log.d(TAG, "setViewport($width, $height) — no-op: native version updates GL viewport and projection matrices")
        viewportWidth = width
        viewportHeight = height
    }

    // Native version: Parses the .coz2 asset bundle, locates the named scene definition,
    // compiles its shaders, binds textures, and transitions from the previous scene.
    fun loadScene(sceneName: String): Boolean = synchronized(lock) {
        Log.d(TAG, "loadScene($sceneName) — no-op: native version loads scene from .coz2 bundle and compiles shaders")
        currentScene = sceneName
        return true
    }

    // Native version: Starts the GL render loop, begins dispatching compute shaders for
    // particle simulation, and enables frame callbacks for animation timing.
    fun play() = synchronized(lock) {
        Log.d(TAG, "play() — no-op: native version starts GL render loop and compute dispatch")
        isPlaying = true
    }

    // Native version: Pauses the GL render loop and suspends compute shader dispatches
    // while preserving the current scene state for resumption.
    fun pause() = synchronized(lock) {
        Log.d(TAG, "pause() — no-op: native version pauses GL render loop, preserves scene state")
        isPlaying = false
    }

    // Native version: Releases all GPU resources including shader programs, textures,
    // framebuffers, compute buffers, and the EGL context.
    fun destroy() = synchronized(lock) {
        Log.d(TAG, "destroy() — no-op: native version releases all GPU resources and EGL context")
        isPlaying = false
        isInitialized = false
        currentScene = null
        uniforms.clear()
    }

    // Native version: Uploads a named float uniform value to the currently bound shader program,
    // used for dynamic parameters like wind direction, wind force, and time-of-day blending.
    fun setUniforms(name: String, value: Float) = synchronized(lock) {
        Log.d(TAG, "setUniforms($name, $value) — no-op: native version uploads uniform to active shader")
        uniforms[name] = value
    }

    fun activate() = synchronized(lock) {
        Log.i(TAG, "Inert engine activated as fallback")
        isActive = true
    }

    fun deactivate() = synchronized(lock) {
        Log.i(TAG, "Inert engine deactivated")
        isActive = false
        destroy()
    }

    fun getCurrentScene(): String? = synchronized(lock) { currentScene }

    fun isEngineInitialized(): Boolean = synchronized(lock) { isInitialized }

    fun isEngineRunning(): Boolean = synchronized(lock) { isPlaying }

    fun getUniform(name: String): Float = synchronized(lock) { uniforms[name] ?: 0f }

    fun getViewportWidth(): Int = synchronized(lock) { viewportWidth }

    fun getViewportHeight(): Int = synchronized(lock) { viewportHeight }
}
