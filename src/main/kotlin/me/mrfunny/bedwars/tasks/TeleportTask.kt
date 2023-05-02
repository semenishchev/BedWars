package me.mrfunny.bedwars.tasks

import me.mrfunny.bedwars.BedWars.Companion.colorize
import me.mrfunny.bedwars.game.GameManager
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
            player.sendTitle("&cDO NOT MOVE!!".colorize(), "", 0, 40, 0)
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
            player.sendTitle("&aBase looks nice, innit?".colorize(), "", 0, 20, 0)
            player.teleport(gameManager.world.getIslandOf(player).spawnLocation)
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