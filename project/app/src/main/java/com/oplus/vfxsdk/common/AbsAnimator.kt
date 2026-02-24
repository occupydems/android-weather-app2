package com.oplus.vfxsdk.common

abstract class AbsAnimator(
    id: Int,
    name: String,
    animLines: Array<AnimLine>
) : Animator(id, name, animLines) {

    var stateParams: HashMap<String, Any>? = null
    var triggers: HashMap<String, Any>? = null
    var triggerStates: ArrayList<Any>? = null

    open fun playAnim() {
        play()
    }

    open fun pauseAnim() {
        pause()
    }

    open fun restartAnim() {
        stop()
        play()
    }

    open fun seekAnimTo(time: Double) {
        seekTo(time)
    }

    open fun seekAnimNext() {
        val nextTime = getCurrentTime() + 1.0 / 60.0
        seekTo(nextTime)
    }

    open fun getUniformData(name: String): Any? {
        return stateParams?.get(name)
    }

    open fun handleTrigger(triggerName: String, value: Any?) {
        triggers?.put(triggerName, value ?: return)
    }

    open fun getAllTriggers(): HashMap<String, Any>? = triggers

    open fun getAllTriggerStates(): List<Any>? = triggerStates
}
