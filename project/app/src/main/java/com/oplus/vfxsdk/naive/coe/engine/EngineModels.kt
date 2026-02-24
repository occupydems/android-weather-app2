package com.oplus.vfxsdk.naive.coe.engine

import android.graphics.Bitmap

class TexCreateInfo {
    enum class WrapMode {
        Repeat,
        ClampToEdge,
        MirroredRepeat
    }

    enum class FilterMode {
        Linear,
        Nearest
    }

    enum class PixelFormat {
        RGBA8,
        RGB8,
        RG8,
        R8,
        RGBA16F,
        RGB16F,
        RG16F,
        R16F
    }

    var bitmap: Bitmap? = null
    var width: Int = 0
    var height: Int = 0
    var flipY: Boolean = false
    var pixelFormat: PixelFormat = PixelFormat.RGBA8
    var wrapMode: WrapMode = WrapMode.ClampToEdge
    var filterMode: FilterMode = FilterMode.Linear
    var createInMainCtx: Boolean? = null

    constructor()

    constructor(
        bitmap: Bitmap?,
        width: Int,
        height: Int,
        flipY: Boolean = false,
        pixelFormat: PixelFormat = PixelFormat.RGBA8,
        wrapMode: WrapMode = WrapMode.ClampToEdge,
        filterMode: FilterMode = FilterMode.Linear,
        createInMainCtx: Boolean? = null
    ) {
        this.bitmap = bitmap
        this.width = width
        this.height = height
        this.flipY = flipY
        this.pixelFormat = pixelFormat
        this.wrapMode = wrapMode
        this.filterMode = filterMode
        this.createInMainCtx = createInMainCtx
    }
}

class RenderStateInfo(
    var blendEnable: Boolean = false,
    var blendSrcFactor: Int = 0,
    var blendDstFactor: Int = 0
)

data class Message(
    val type: String,
    val data: Any?
)
