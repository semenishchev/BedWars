package me.mrfunny.plugins.paper.players

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.lang.System.currentTimeMillis
import java.util.*

class PlayerData(uuid: UUID) {
    companion object{
        val PLAYERS = hashMapOf<UUID, PlayerData>()

        fun enable(){
            Bukkit.getOnlinePlayers().forEach {
                PLAYERS[it.uniqueId] = PlayerData(it.uniqueId)
            }
        }

        fun disable(){
            PLAYERS.clear()
        }
    }
    var player: Player = Bukkit.getPlayer(uuid)!!
        private set

    var totalKills: Int = 0
    var totalDeaths: Int = 0

    var lastCombat: Long = currentTimeMillis()
    var lastAttacker: UUID? = null
    var health: Double = 20.0
}