package com.oplus.vfxsdk.common

open class Effect(
    val name: String,
    val type: String
)

data class ParticleEffect(
    val effectName: String,
    val effectType: String
) : Effect(effectName, effectType)

data class ParticleEmitter(
    val name: String,
    val params: Map<String, Any>?
)

data class ExprExtra(
    val key: String,
    val value: Any?
)
