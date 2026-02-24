package com.oplus.vfxsdk.naive.coe.engine

class Scene {
    val mRetainedObjects: ArrayList<NativeObject> = ArrayList()
    val mLock: Any = Object()
    val mTls: ThreadLocal<Boolean> = ThreadLocal()
    var SceneObjectCount: Int = 0

    fun addComponent(obj: NativeObject): Boolean {
        synchronized(mLock) {
            if (!mRetainedObjects.contains(obj)) {
                mRetainedObjects.add(obj)
                SceneObjectCount++
                return true
            }
            return false
        }
    }

    fun removeAllComponents() {
        synchronized(mLock) {
            for (obj in mRetainedObjects) {
                obj.release()
            }
            mRetainedObjects.clear()
            SceneObjectCount = 0
        }
    }

    fun bind() {
        mTls.set(true)
    }

    fun unbind() {
        mTls.set(false)
    }

    fun inBinding(): Boolean {
        return mTls.get() == true
    }
}
