package com.oplus.vfxsdk.naive.parse

import com.oplus.vfxsdk.common.Uniform
import com.oplus.vfxsdk.common.api.ILinkProxy
import com.oplus.vfxsdk.common.api.IPlayer

data class SystemUniform(
    val passIndex: Int,
    val uniform: Uniform
)

class LinkProxyImpl : ILinkProxy {
    private val players: HashMap<Int, IPlayer> = HashMap()

    override fun getById(id: Int): IPlayer? {
        return players[id]
    }

    override fun notify(event: String, data: String) {
    }

    fun addPlayer(id: Int, player: IPlayer) {
        players[id] = player
    }

    fun removePlayer(id: Int) {
        players.remove(id)
    }
}
