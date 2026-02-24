package com.oplus.vfxsdk.common

data class Uniform(
    val id: Int,
    val name: String,
    val type: UniformType,
    val value: Any?,
    val width: Int = 0,
    val height: Int = 0,
    val w: Float = 0f,
    val format: Int = 0,
    val mediaType: String? = null,
    val flip: Boolean = false,
    val colorMode: Int? = null,
    val filterMode: Int? = null,
    val wrapMode: Int? = null
)

data class UniformValue(
    val name: String,
    val type: UniformType,
    val values: Array<Any?>,
    val delay: Int = 0,
    val duration: Long = 0L,
    val ipol: String? = null,
    val bezier: FloatArray? = null,
    val spring: FloatArray? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UniformValue) return false
        return name == other.name && type == other.type && values.contentEquals(other.values)
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + values.contentHashCode()
        return result
    }
}
