package me.mrfunny.plugins.paper.players

import org.bukkit.entity.Player
import java.util.*

object NoFallPlayers {
    private val players = arrayListOf<UUID>()

    fun add(player: Player){
        if(players.contains(player.uniqueId)) return
        players.add(player.uniqueId)
    }

    fun add(uuid: UUID){
        if(players.contains(uuid)) return
        players.add(uuid)
    }

    fun remove(uuid: UUID){
        players.remove(uuid)
    }

    fun remove(player: Player){
        players.remove(player.uniqueId)
    }

    fun check(player: Player): Boolean{
        return players.contains(player.uniqueId)
    }

    fun clear(){
        players.clear()
    }
}