package com.oplus.vfxsdk.common

data class Layer(
    val id: String,
    val name: String,
    val type: String,
    val transform: TransformData?,
    val postProcessor: Array<PostProcessorData>?,
    val render: Array<RendPass>?,
    val params: HashMap<String, Any>?,
    val animParams: HashMap<String, Any>?,
    val enable: Boolean = true,
    val enableBlend: Boolean = false,
    val blendSfactor: Int = 0,
    val blendDfactor: Int = 0,
    val order: Int = 0
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Layer) return false
        return id == other.id && name == other.name
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        return result
    }
}

data class TransformData(
    val layout: FloatArray?,
    val scale: FloatArray?,
    val rotation: FloatArray?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TransformData) return false
        return layout.contentEquals(other.layout) && scale.contentEquals(other.scale) && rotation.contentEquals(other.rotation)
    }

    override fun hashCode(): Int {
        var result = layout?.contentHashCode() ?: 0
        result = 31 * result + (scale?.contentHashCode() ?: 0)
        result = 31 * result + (rotation?.contentHashCode() ?: 0)
        return result
    }
}

data class PostProcessorData(
    val name: String,
    val params: Map<String, Any>?
)

data class RendPass(
    val name: String,
    val vertex: String?,
    val fragment: String?,
    val uniforms: List<Uniform>?,
    val systemUniforms: List<Any>?
)

data class StatusData(
    val name: String,
    val params: Map<String, Any>?
)

data class StatusAnim(
    val name: String,
    val data: StatusData?
)
