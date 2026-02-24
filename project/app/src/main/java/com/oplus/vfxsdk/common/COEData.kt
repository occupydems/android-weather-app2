package com.oplus.vfxsdk.common

class COEData {
    var layers: ArrayList<Layer> = ArrayList()
    var features: Array<String>? = null
    var animators: HashMap<String, AnimatorValue>? = null
}

class COEMiniData {
    var features: Array<String>? = null
    var sceneIds: ArrayList<String>? = null
}

class COEExpressions {
    var expressions: Map<String, Any>? = null

    fun evaluate(key: String): Any? {
        return expressions?.get(key)
    }
}
