package com.oplus.vfxsdk.common

data class AnimKey(
    @JvmField var _time: Float,
    @JvmField var _value: Float,
    @JvmField var _cacheValue: Float
) : IKey {
    override fun getTime(): Float = _time
    override fun getValue(): Float = _value
    override fun getCacheValue(): Float = _cacheValue
    override fun setCacheValue(v: Float) { _cacheValue = v }
    override fun setValue(v: Float) { _value = v }
}

data class AnimLine(
    val name: String,
    val keys: Array<AnimKey>,
    val duration: Float
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AnimLine) return false
        return name == other.name && keys.contentEquals(other.keys) && duration == other.duration
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + keys.contentHashCode()
        result = 31 * result + duration.hashCode()
        return result
    }
}

data class Anim(
    val name: String,
    val lines: Array<AnimLine>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Anim) return false
        return name == other.name && lines.contentEquals(other.lines)
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + lines.contentHashCode()
        return result
    }
}

data class AnimatorValue(
    val id: Int,
    val name: String,
    val currentTime: Float = 0f,
    val animLines: Array<AnimLine> = emptyArray(),
    val eventLine: AnimLine? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AnimatorValue) return false
        return id == other.id && name == other.name && currentTime == other.currentTime &&
                animLines.contentEquals(other.animLines) && eventLine == other.eventLine
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + name.hashCode()
        result = 31 * result + currentTime.hashCode()
        result = 31 * result + animLines.contentHashCode()
        result = 31 * result + (eventLine?.hashCode() ?: 0)
        return result
    }
}

fun interface AnimEndListener {
    fun onAnimEnd()
}
