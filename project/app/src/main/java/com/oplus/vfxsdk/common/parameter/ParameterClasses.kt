package com.oplus.vfxsdk.common.parameter

import android.animation.TimeInterpolator

class Param(
    val name: String,
    val defaultValue: Any?,
    val interpolator: TimeInterpolator?,
    val delay: Long,
    val duration: Long,
    val ipol: String?
)

abstract class AbsParameter : IParameter {
    @JvmField
    protected var _value: Any? = null
    @JvmField
    protected var _animEndListener: (() -> Unit)? = null
    @JvmField
    protected var _animStartListener: (() -> Unit)? = null

    override fun getValue(): Any? = _value
    override fun copyValue(): Any? = _value

    override fun setAnimEndListener(listener: () -> Unit) {
        _animEndListener = listener
    }

    override fun setAnimStartListener(listener: () -> Unit) {
        _animStartListener = listener
    }
}

class ParameterFloat(defaultValue: Float = 0f) : AbsParameter() {
    init {
        _value = defaultValue
    }

    fun getFloatValue(): Float = (_value as? Float) ?: 0f

    fun setFloatValue(v: Float) {
        _value = v
    }

    override fun copyValue(): Any = getFloatValue()
}

class ParameterFloatArray(defaultValue: FloatArray = floatArrayOf()) : AbsParameter() {
    init {
        _value = defaultValue
    }

    fun getFloatArrayValue(): FloatArray = (_value as? FloatArray) ?: floatArrayOf()

    fun setFloatArrayValue(v: FloatArray) {
        _value = v
    }

    override fun copyValue(): Any = getFloatArrayValue().copyOf()
}

class ParameterInt(defaultValue: Int = 0) : AbsParameter() {
    init {
        _value = defaultValue
    }

    fun getIntValue(): Int = (_value as? Int) ?: 0

    fun setIntValue(v: Int) {
        _value = v
    }

    override fun copyValue(): Any = getIntValue()
}

data class ParameterEntry(
    val name: String,
    val parameter: IParameter
)
