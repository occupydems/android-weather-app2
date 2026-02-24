package com.oplus.vfxsdk.common

interface IKey {
    fun getTime(): Float
    fun getValue(): Float
    fun getCacheValue(): Float
    fun setCacheValue(v: Float)
    fun setValue(v: Float)
}
