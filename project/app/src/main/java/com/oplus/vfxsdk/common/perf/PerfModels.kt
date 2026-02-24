package com.oplus.vfxsdk.common.perf

data class Stat(
    var frameCount: Long = 0L,
    var totalTime: Long = 0L,
    var averageTime: Double = 0.0,
    var maxTime: Long = 0L,
    var minTime: Long = Long.MAX_VALUE,
    var lastTime: Long = 0L
) {
    fun reset() {
        frameCount = 0L
        totalTime = 0L
        averageTime = 0.0
        maxTime = 0L
        minTime = Long.MAX_VALUE
        lastTime = 0L
    }

    fun record(time: Long) {
        frameCount++
        totalTime += time
        lastTime = time
        if (time > maxTime) maxTime = time
        if (time < minTime) minTime = time
        averageTime = totalTime.toDouble() / frameCount
    }
}

abstract class StatUtil {
    companion object {
        @JvmStatic
        val stats: Stat = Stat()
    }

    open fun begin() {}
    open fun end() {}
    open fun reset() { stats.reset() }
}

class EngineStat : StatUtil() {
    private var startTime: Long = 0L

    override fun begin() {
        startTime = System.nanoTime()
    }

    override fun end() {
        val elapsed = System.nanoTime() - startTime
        stats.record(elapsed / 1_000_000)
    }
}

class AnimStat : StatUtil() {
    private var startTime: Long = 0L

    override fun begin() {
        startTime = System.nanoTime()
    }

    override fun end() {
        val elapsed = System.nanoTime() - startTime
        stats.record(elapsed / 1_000_000)
    }
}

class FileStat : StatUtil() {
    private var startTime: Long = 0L

    override fun begin() {
        startTime = System.nanoTime()
    }

    override fun end() {
        val elapsed = System.nanoTime() - startTime
        stats.record(elapsed / 1_000_000)
    }
}

class ImgStat : StatUtil() {
    private var startTime: Long = 0L

    override fun begin() {
        startTime = System.nanoTime()
    }

    override fun end() {
        val elapsed = System.nanoTime() - startTime
        stats.record(elapsed / 1_000_000)
    }
}

class NativeStatUtil : StatUtil() {
    private var startTime: Long = 0L

    override fun begin() {
        startTime = System.nanoTime()
    }

    override fun end() {
        val elapsed = System.nanoTime() - startTime
        stats.record(elapsed / 1_000_000)
    }
}
