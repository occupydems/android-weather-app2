package com.oplus.vfxsdk.naive.parse

import com.oplus.vfxsdk.common.AbsAnimator
import com.oplus.vfxsdk.common.AnimLine

class COEAnimator(
    id: Int,
    name: String,
    animLines: Array<AnimLine>
) : AbsAnimator(id, name, animLines) {

    var sceneId: String? = null
    var layerName: String? = null

    override fun playAnim() {
        super.playAnim()
    }

    override fun pauseAnim() {
        super.pauseAnim()
    }

    override fun restartAnim() {
        super.restartAnim()
    }

    override fun onFrame(frameTimeNanos: Long) {
        super.onFrame(frameTimeNanos)
    }
}
