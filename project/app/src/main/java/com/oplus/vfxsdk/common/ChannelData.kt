package com.oplus.vfxsdk.common

import com.oplus.vfxsdk.common.parameter.IUpdate

class ChannelData(
    val name: String,
    val type: UniformType,
    var values: Any?,
    val passId: Int,
    var tempKey: String,
    var update: IUpdate?
) {
    private val channels = ArrayList<Any?>()

    fun addChannel(value: Any?, index: Int) {
        while (channels.size <= index) {
            channels.add(null)
        }
        channels[index] = value
    }

    fun getChannel(index: Int): Any? {
        return if (index in channels.indices) channels[index] else null
    }

    fun initDefaultValue() {
        when (type) {
            UniformType.Float -> values = 0f
            UniformType.Int -> values = 0
            UniformType.Vec2 -> values = floatArrayOf(0f, 0f)
            UniformType.Vec3 -> values = floatArrayOf(0f, 0f, 0f)
            UniformType.Vec4 -> values = floatArrayOf(0f, 0f, 0f, 0f)
            UniformType.Color -> values = floatArrayOf(0f, 0f, 0f, 1f)
            else -> values = null
        }
    }
}
