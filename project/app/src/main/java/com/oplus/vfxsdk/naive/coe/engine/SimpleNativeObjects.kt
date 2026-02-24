package com.oplus.vfxsdk.naive.coe.engine

class RenderPass() : NativeObject() {
    constructor(engine: NativeEngine, handle: Long) : this() {
        mEngine = engine
        mNativeHandle = handle
    }
}

class RenderTexture() : NativeObject() {
    constructor(engine: NativeEngine, handle: Long) : this() {
        mEngine = engine
        mNativeHandle = handle
    }
}

class Texture() : NativeObject() {
    constructor(engine: NativeEngine, handle: Long) : this() {
        mEngine = engine
        mNativeHandle = handle
    }
}

class Transform() : NativeObject() {
    constructor(engine: NativeEngine, handle: Long) : this() {
        mEngine = engine
        mNativeHandle = handle
    }
}

class PostProcessor() : NativeObject() {
    constructor(engine: NativeEngine, handle: Long) : this() {
        mEngine = engine
        mNativeHandle = handle
    }
}

class Framebuffer(handle: Long) : NativeObject() {
    init {
        mNativeHandle = handle
    }
}
