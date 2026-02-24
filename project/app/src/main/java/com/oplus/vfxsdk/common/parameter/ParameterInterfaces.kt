package com.oplus.vfxsdk.common.parameter

import com.oplus.vfxsdk.common.ChannelData

interface IParameter {
    fun getValue(): Any?
    fun copyValue(): Any?
    fun setAnimEndListener(listener: () -> Unit)
    fun setAnimStartListener(listener: () -> Unit)
}

interface IUpdate {
    fun updateValue(name: String, value: Any?)
    fun updateValueIndex(channelData: ChannelData, name: String, value: Any?, index: Int)
    fun updateValueIndexKey(channelData: ChannelData, name: String, value: Any?, index: Int, key: String)
}

fun interface ICallback {
    fun callback(vararg args: Any?)
}

interface IPop {
    fun popValue(channelData: ChannelData, name: String, index: Int): Any?
}
