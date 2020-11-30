package me.mrfunny.plugins.paper.players

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*

class PlayerData(uuid: UUID) {
    companion object{
        val PLAYERS = hashMapOf<UUID, PlayerData>()
    }
    var player: Player = Bukkit.getPlayer(uuid)!!
        private set

    var lastCombat: Long = System.currentTimeMillis()
    var lastAttacker: Player? = null
    var health: Double = 20.0
}