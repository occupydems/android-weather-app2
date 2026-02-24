package com.oplus.vfxsdk.naive.coe.engine.Event

class Event(
    var type: String,
    var key: String,
    var value: Any?
) {
    constructor() : this("", "", null)
}
