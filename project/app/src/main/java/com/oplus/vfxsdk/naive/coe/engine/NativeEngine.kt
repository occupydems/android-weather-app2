package com.oplus.vfxsdk.naive.coe.engine

import android.content.res.AssetManager
import android.graphics.Bitmap
import android.view.Surface
import com.oplus.vfxsdk.naive.coe.api.ITask
import com.oplus.vfxsdk.naive.coe.api.TaskOptions
import com.oplus.vfxsdk.naive.coe.engine.Event.Event
import com.oplus.vfxsdk.naive.coe.engine.Event.EventCenter
import java.nio.ByteBuffer

class NativeEngine : NativeObject, EventCenter.Listener {

    val mRetainedObjects: ArrayList<NativeObject> = ArrayList()
    val mScene: Scene = Scene()

    companion object {
        @JvmStatic
        private external fun nativeCreate(caps: Int): Long
        @JvmStatic
        private external fun nativeDestroyEngine(handle: Long)
        @JvmStatic
        private external fun nativeStartEngine(handle: Long)
        @JvmStatic
        private external fun nativeStopEngine(handle: Long)
        @JvmStatic
        private external fun nativeBind(handle: Long, flag: Boolean): Boolean
        @JvmStatic
        private external fun nativeUnbind(handle: Long): Boolean
        @JvmStatic
        private external fun nativeInitEngine(handle: Long, features: Array<String>)
        @JvmStatic
        private external fun nativeSetSurface(handle: Long, surface: Surface, flag: Boolean)
        @JvmStatic
        private external fun nativeSetViewport(handle: Long, width: Int, height: Int)
        @JvmStatic
        private external fun nativeSetScreenSize(handle: Long, width: Int, height: Int)
        @JvmStatic
        private external fun nativeSetFPS(handle: Long, fps: Int)
        @JvmStatic
        private external fun nativeProcessFrame(handle: Long, time: Double)
        @JvmStatic
        private external fun nativePostDraw(handle: Long)
        @JvmStatic
        private external fun nativeLoadEffect(handle: Long, name: String, flag: Boolean): Int
        @JvmStatic
        private external fun nativeUnloadEffect(handle: Long, effectId: Int): Int
        @JvmStatic
        private external fun nativeCreateMaterial(handle: Long): Long
        @JvmStatic
        private external fun nativeCreateMesh(handle: Long, buffer: ByteBuffer): Long
        @JvmStatic
        private external fun nativeCreatePass(handle: Long, vertex: String, fragment: String, flag: Boolean, mode: Int, renderState: RenderStateInfo?): Long
        @JvmStatic
        private external fun nativeCreateRenderer(handle: Long, mode: Int, flag: Boolean): Long
        @JvmStatic
        private external fun nativeCreateRenderObject(handle: Long, name: String, type: String, mode: Int, flag: Boolean, buffer: ByteBuffer?, width: Int, height: Int): Long
        @JvmStatic
        private external fun nativeCreateTexture1(handle: Long, info: TexCreateInfo): Long
        @JvmStatic
        private external fun nativeCreateRenderTexture(handle: Long, infos: Array<TexCreateInfo>): Long
        @JvmStatic
        private external fun nativeCreateTransform(handle: Long, matrix: FloatArray): Long
        @JvmStatic
        private external fun nativeCreateWindowSurface(handle: Long, surface: Surface): Long
        @JvmStatic
        private external fun nativeCreatePostProcessor(handle: Long): Long
        @JvmStatic
        private external fun nativeCreateEffect(handle: Long, name: String): Long
        @JvmStatic
        private external fun nativeCreateEmitter(handle: Long, name: String): Long
        @JvmStatic
        private external fun nativeSetProperty(handle: Long, material: Material, type: Int, name: String, values: Array<Any?>)
        @JvmStatic
        private external fun nativeSetMaterial(handle: Long, renderer: NativeRenderer, material: Material)
        @JvmStatic
        private external fun nativeSetMesh(handle: Long, renderer: NativeRenderer, mesh: Mesh)
        @JvmStatic
        private external fun nativeSetRenderTexture(handle: Long, pass: Pass, renderTexture: RenderTexture)
        @JvmStatic
        private external fun nativeAddPass(handle: Long, material: Material, pass: Pass)
        @JvmStatic
        private external fun nativeSetSortingOrder(handle: Long, renderPass: RenderPass, layer: Int, order: Int)
        @JvmStatic
        private external fun nativeEnableRenderObject(handle: Long, renderPass: RenderPass, enable: Boolean)
        @JvmStatic
        private external fun nativeGetRenderObjects(handle: Long, name: String): LongArray
        @JvmStatic
        private external fun nativeAttachAssetManager(handle: Long, assetManager: AssetManager)
        @JvmStatic
        private external fun nativeAttachClassLoader(handle: Long, classLoader: ClassLoader)
        @JvmStatic
        private external fun nativeSetFilesCachePath(handle: Long, path: String)
        @JvmStatic
        private external fun nativeSetRenderThreadName(handle: Long, name: String)
        @JvmStatic
        private external fun nativeAddCallback(handle: Long, callback: NativeCallback)
        @JvmStatic
        private external fun nativeRemoveCallback(handle: Long, callback: NativeCallback)
        @JvmStatic
        private external fun nativeRemoveCallbacks(handle: Long)
        @JvmStatic
        private external fun nativePostEvent(handle: Long, event: Event)
        @JvmStatic
        private external fun nativeSendMessage(handle: Long, message: Message)
        @JvmStatic
        private external fun nativePostTask(handle: Long, task: ITask, options: TaskOptions)
        @JvmStatic
        private external fun nativeSetBitmap(handle: Long, bitmap: Bitmap, name: String)
        @JvmStatic
        private external fun nativeResetScene(handle: Long)
        @JvmStatic
        private external fun nativeSetSceneOutputRtSize(handle: Long, width: Int, height: Int)
        @JvmStatic
        private external fun nativeSetAnimListenedList(handle: Long, names: Array<String>)
        @JvmStatic
        private external fun nativeOnAnimStart(handle: Long, animId: Int, name: String)
        @JvmStatic
        private external fun nativeOnAnimEnd(handle: Long, animId: Int, name: String)
        @JvmStatic
        private external fun nativeTouchEvent(handle: Long, action: Int, x: Float, y: Float)
        @JvmStatic
        private external fun nativeSetCOEEngineLocked(handle: Long): Long
        @JvmStatic
        private external fun nativeSetCOEEngineUnlocked(handle: Long, lockToken: Long)
    }

    constructor() : super() {
        loadLibrary()
        if (libraryFound) {
            mNativeHandle = nativeCreate(0)
        }
        mEngine = this
    }

    constructor(caps: Int) : super() {
        loadLibrary()
        if (libraryFound) {
            mNativeHandle = nativeCreate(caps)
        }
        mEngine = this
    }

    fun destroyEngine() {
        synchronized(mLock) {
            if (mNativeHandle != 0L) {
                mScene.removeAllComponents()
                mRetainedObjects.clear()
                nativeDestroyEngine(mNativeHandle)
                mNativeHandle = 0L
            }
        }
    }

    fun startEngine() {
        if (CheckHandle()) nativeStartEngine(getNativeHandle())
    }

    fun stopEngine() {
        if (CheckHandle()) nativeStopEngine(getNativeHandle())
    }

    fun bind(flag: Boolean): Boolean {
        return if (CheckHandle()) nativeBind(getNativeHandle(), flag) else false
    }

    fun unbind(): Boolean {
        return if (CheckHandle()) nativeUnbind(getNativeHandle()) else false
    }

    fun initEngine(features: Array<String>) {
        if (CheckHandle()) nativeInitEngine(getNativeHandle(), features)
    }

    fun setSurface(surface: Surface, flag: Boolean) {
        if (CheckHandle()) nativeSetSurface(getNativeHandle(), surface, flag)
    }

    fun setViewport(width: Int, height: Int) {
        if (CheckHandle()) nativeSetViewport(getNativeHandle(), width, height)
    }

    fun setScreenSize(width: Int, height: Int) {
        if (CheckHandle()) nativeSetScreenSize(getNativeHandle(), width, height)
    }

    fun setFPS(fps: Int) {
        if (CheckHandle()) nativeSetFPS(getNativeHandle(), fps)
    }

    fun processFrame(time: Double) {
        if (CheckHandle()) nativeProcessFrame(getNativeHandle(), time)
    }

    fun postDraw() {
        if (CheckHandle()) nativePostDraw(getNativeHandle())
    }

    fun loadEffect(name: String, flag: Boolean): Int {
        return if (CheckHandle()) nativeLoadEffect(getNativeHandle(), name, flag) else -1
    }

    fun unloadEffect(effectId: Int): Int {
        return if (CheckHandle()) nativeUnloadEffect(getNativeHandle(), effectId) else -1
    }

    fun createMaterial(): Long {
        return if (CheckHandle()) nativeCreateMaterial(getNativeHandle()) else 0L
    }

    fun createMesh(buffer: ByteBuffer): Long {
        return if (CheckHandle()) nativeCreateMesh(getNativeHandle(), buffer) else 0L
    }

    fun createPass(vertex: String, fragment: String, flag: Boolean, mode: Int, renderState: RenderStateInfo?): Long {
        return if (CheckHandle()) nativeCreatePass(getNativeHandle(), vertex, fragment, flag, mode, renderState) else 0L
    }

    fun createRenderer(mode: Int, flag: Boolean): Long {
        return if (CheckHandle()) nativeCreateRenderer(getNativeHandle(), mode, flag) else 0L
    }

    fun createRenderObject(name: String, type: String, mode: Int, flag: Boolean, buffer: ByteBuffer?, width: Int, height: Int): Long {
        return if (CheckHandle()) nativeCreateRenderObject(getNativeHandle(), name, type, mode, flag, buffer, width, height) else 0L
    }

    fun createTexture(info: TexCreateInfo): Long {
        return if (CheckHandle()) nativeCreateTexture1(getNativeHandle(), info) else 0L
    }

    fun createRenderTexture(infos: Array<TexCreateInfo>): Long {
        return if (CheckHandle()) nativeCreateRenderTexture(getNativeHandle(), infos) else 0L
    }

    fun createTransform(matrix: FloatArray): Long {
        return if (CheckHandle()) nativeCreateTransform(getNativeHandle(), matrix) else 0L
    }

    fun createWindowSurface(surface: Surface): Long {
        return if (CheckHandle()) nativeCreateWindowSurface(getNativeHandle(), surface) else 0L
    }

    fun createPostProcessor(): Long {
        return if (CheckHandle()) nativeCreatePostProcessor(getNativeHandle()) else 0L
    }

    fun createEffect(name: String): Long {
        return if (CheckHandle()) nativeCreateEffect(getNativeHandle(), name) else 0L
    }

    fun createEmitter(name: String): Long {
        return if (CheckHandle()) nativeCreateEmitter(getNativeHandle(), name) else 0L
    }

    fun setProperty(material: Material, type: Int, name: String, values: Array<Any?>) {
        if (CheckHandle()) nativeSetProperty(getNativeHandle(), material, type, name, values)
    }

    fun setMaterial(renderer: NativeRenderer, material: Material) {
        if (CheckHandle()) nativeSetMaterial(getNativeHandle(), renderer, material)
    }

    fun setMesh(renderer: NativeRenderer, mesh: Mesh) {
        if (CheckHandle()) nativeSetMesh(getNativeHandle(), renderer, mesh)
    }

    fun setRenderTexture(pass: Pass, renderTexture: RenderTexture) {
        if (CheckHandle()) nativeSetRenderTexture(getNativeHandle(), pass, renderTexture)
    }

    fun addPass(material: Material, pass: Pass) {
        if (CheckHandle()) nativeAddPass(getNativeHandle(), material, pass)
    }

    fun setSortingOrder(renderPass: RenderPass, layer: Int, order: Int) {
        if (CheckHandle()) nativeSetSortingOrder(getNativeHandle(), renderPass, layer, order)
    }

    fun enableRenderObject(renderPass: RenderPass, enable: Boolean) {
        if (CheckHandle()) nativeEnableRenderObject(getNativeHandle(), renderPass, enable)
    }

    fun getRenderObjects(name: String): LongArray {
        return if (CheckHandle()) nativeGetRenderObjects(getNativeHandle(), name) else LongArray(0)
    }

    fun attachAssetManager(assetManager: AssetManager) {
        if (CheckHandle()) nativeAttachAssetManager(getNativeHandle(), assetManager)
    }

    fun attachClassLoader(classLoader: ClassLoader) {
        if (CheckHandle()) nativeAttachClassLoader(getNativeHandle(), classLoader)
    }

    fun setFilesCachePath(path: String) {
        if (CheckHandle()) nativeSetFilesCachePath(getNativeHandle(), path)
    }

    fun setRenderThreadName(name: String) {
        if (CheckHandle()) nativeSetRenderThreadName(getNativeHandle(), name)
    }

    fun addCallback(callback: NativeCallback) {
        if (CheckHandle()) nativeAddCallback(getNativeHandle(), callback)
    }

    fun removeCallback(callback: NativeCallback) {
        if (CheckHandle()) nativeRemoveCallback(getNativeHandle(), callback)
    }

    fun removeCallbacks() {
        if (CheckHandle()) nativeRemoveCallbacks(getNativeHandle())
    }

    fun postEvent(event: Event) {
        if (CheckHandle()) nativePostEvent(getNativeHandle(), event)
    }

    fun sendMessage(message: Message) {
        if (CheckHandle()) nativeSendMessage(getNativeHandle(), message)
    }

    fun postTask(task: ITask, options: TaskOptions) {
        if (CheckHandle()) nativePostTask(getNativeHandle(), task, options)
    }

    fun setBitmap(bitmap: Bitmap, name: String) {
        if (CheckHandle()) nativeSetBitmap(getNativeHandle(), bitmap, name)
    }

    fun resetScene() {
        if (CheckHandle()) nativeResetScene(getNativeHandle())
    }

    fun setSceneOutputRtSize(width: Int, height: Int) {
        if (CheckHandle()) nativeSetSceneOutputRtSize(getNativeHandle(), width, height)
    }

    fun setAnimListenedList(names: Array<String>) {
        if (CheckHandle()) nativeSetAnimListenedList(getNativeHandle(), names)
    }

    fun onAnimStart(animId: Int, name: String) {
        if (CheckHandle()) nativeOnAnimStart(getNativeHandle(), animId, name)
    }

    fun onAnimEnd(animId: Int, name: String) {
        if (CheckHandle()) nativeOnAnimEnd(getNativeHandle(), animId, name)
    }

    fun touchEvent(action: Int, x: Float, y: Float) {
        if (CheckHandle()) nativeTouchEvent(getNativeHandle(), action, x, y)
    }

    fun setCOEEngineLocked(): Long {
        return if (CheckHandle()) nativeSetCOEEngineLocked(getNativeHandle()) else 0L
    }

    fun setCOEEngineUnlocked(lockToken: Long) {
        if (CheckHandle()) nativeSetCOEEngineUnlocked(getNativeHandle(), lockToken)
    }

    fun addRetainedObject(obj: NativeObject) {
        synchronized(mLock) {
            if (!mRetainedObjects.contains(obj)) {
                mRetainedObjects.add(obj)
            }
        }
    }

    override fun release() {
        destroyEngine()
    }

    override fun onEvent(event: Event) {
    }
}
