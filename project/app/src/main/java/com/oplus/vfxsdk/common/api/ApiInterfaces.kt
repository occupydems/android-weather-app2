package com.oplus.vfxsdk.common.api

interface IPlayer {
    fun play()
    fun pause()
    fun stop()
    fun seekTo(time: Double)
    fun setSpeed(speed: Float)
}

interface ILinkProxy {
    fun getById(id: Int): IPlayer?
    fun notify(event: String, data: String)
}
