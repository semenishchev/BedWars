package me.mrfunny.plugins.paper.events

import me.mrfunny.plugins.paper.manager.GameManager
import me.mrfunny.plugins.paper.manager.GameState
import me.mrfunny.plugins.paper.tasks.PlayerRespawnTask
import me.mrfunny.plugins.paper.util.Colorize
import me.mrfunny.plugins.paper.worlds.Island
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.GameMode
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.scheduler.BukkitTask

class PlayerDeathListener(private val gameManager: GameManager) : Listener {

    @EventHandler
    fun onDamage(event: EntityDamageEvent){

        if(event.entity !is Player) return
        if(gameManager.state != GameState.ACTIVE) return

        val player: Player = event.entity as Player
        val playerIsland: Island? = gameManager.world.getIslandForPlayer(player)
        if(playerIsland == null || player.gameMode != GameMode.SURVIVAL){
            event.isCancelled = true
            return
        }

        if(event.finalDamage >= player.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.value){
            event.isCancelled = true
            player.health = player.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.value
            player.gameMode = GameMode.SPECTATOR
            player.teleport(gameManager.world.lobbyPosition)
            if(playerIsland.isBedPlaced()){
                val task: BukkitTask = Bukkit.getScheduler().runTaskTimer(gameManager.plugin, PlayerRespawnTask(player), 0, 20)
                Bukkit.getScheduler().runTaskLater(gameManager.plugin, task::cancel, 20 * 6)
            } else {
                player.sendTitle(Colorize.c("&cYOU DIED"), null, 30, 30, 30)
            }
        }
    }

    fun onDamageByOther(event: EntityDamageByEntityEvent){
        if(event.damager !is Player && event.entity !is Player) return

        val damager: Player = event.damager as Player
        val player: Player = event.entity as Player
        if(event.finalDamage >= player.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.value){
            damager.sendMessage(Colorize.c("&aYou killed ${player.name}"))
        }


    }
}