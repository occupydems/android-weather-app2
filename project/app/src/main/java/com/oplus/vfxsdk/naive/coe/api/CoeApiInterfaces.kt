package com.oplus.vfxsdk.naive.coe.api

fun interface ITask {
    fun doTask()
}

enum class ExecutionType {
    Immediate,
    Deferred
}

data class TaskOptions(
    val taskName: String = "",
    val taskPriority: Int = 0,
    val taskDelay: Long = 0L,
    val taskPeriod: Long = 0L,
    val taskRepeat: Int = 0,
    val taskExecutionType: ExecutionType = ExecutionType.Deferred,
    val taskBindWithGLContext: Boolean = false,
    val taskMainContext: Boolean = false
)

interface ICOEEngine {
    fun loadCOE2(fileName: String, cacheData: Boolean, async: Boolean): Int
}

interface ICOEEngineListener {
    fun onInit()
    fun onStart()
    fun onLoaded()
    fun onSurfaceChange()
    fun onRenderStart()
    fun onRenderQuit()
    fun onDestroyed()
}

interface IAnimListener

interface IUpdate {
    fun onUpdate(time: Double)
}
