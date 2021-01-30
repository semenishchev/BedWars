package me.mrfunny.plugins.paper.tasks

import me.mrfunny.plugins.paper.BedWars.Companion.colorize
import me.mrfunny.plugins.paper.gamemanager.GameManager
import me.mrfunny.plugins.paper.players.PlayerData
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import kotlin.math.cos
import kotlin.math.sin

class TeleportTask(val gameManager: GameManager, val player: Player?): BukkitRunnable(){

    companion object{
        val teleporting = hashMapOf<Player, TeleportTask>()
    }

    val location: Location = player!!.location

    init {
        if (player != null){
            player.sendTitle("&c${if(PlayerData.PLAYERS[player.uniqueId]!!.isRussian()) "Не двигайтесь" else "Do not move"}!".colorize(), "", 0, 40, 0)
            teleporting[player] = this
        }
    }

    var timer = 5

    override fun run() {
        if (player == null){
            cancel()
            return
        }
        if(timer == 0) {
            teleporting.remove(player)
            player.sendTitle("&a${if(PlayerData.PLAYERS[player.uniqueId]!!.isRussian()) "Вы были телепортированы" else "You have been teleported"}!".colorize(), "", 0, 20, 0)
            player.teleport(gameManager.world.getIslandForPlayer(player)!!.spawnLocation!!)
            this.cancel()
            return
        }

        val increment: Double = 2 * Math.PI / 40
        for (i in 0..40) {
            val angle = i * increment
            player.world.spawnParticle(Particle.TOTEM, player.location.x + cos(angle), player.location.y + 1.8, player.location.z + sin(angle), 0)
        }
        timer--
    }
}