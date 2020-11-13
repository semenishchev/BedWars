package me.mrfunny.plugins.paper.events

import me.mrfunny.plugins.paper.gamemanager.GameManager
import me.mrfunny.plugins.paper.gamemanager.GameState
import me.mrfunny.plugins.paper.tasks.PlayerRespawnTask
import me.mrfunny.plugins.paper.util.Colorize
import me.mrfunny.plugins.paper.worlds.Island
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Sound
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player
import org.bukkit.entity.Skeleton
import org.bukkit.entity.Villager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.scheduler.BukkitTask

class PlayerDeathListener(private val gameManager: GameManager) : Listener {

    @EventHandler
    fun onDamage(event: EntityDamageEvent){

        if(gameManager.state != GameState.ACTIVE) { event.isCancelled = true; return;}

        if(event.entity !is Player) {
            if(event.entity is Villager || event.entity is Skeleton){
                event.isCancelled = true
            }
            return
        }

        val player: Player = event.entity as Player
        val playerIsland: Island? = gameManager.world.getIslandForPlayer(player)
        if(playerIsland == null || player.gameMode != GameMode.SURVIVAL){
            event.isCancelled = true
            return
        }

        if(event.finalDamage >= player.health){
            event.isCancelled = true
            player.health = player.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.value
            player.gameMode = GameMode.SPECTATOR
            player.teleport(gameManager.world.lobbyPosition)
            if(playerIsland.isBedPlaced()){
                val task: BukkitTask = Bukkit.getScheduler().runTaskTimer(gameManager.plugin, PlayerRespawnTask(player, gameManager.world.getIslandForPlayer(player)!!), 0, 20)
                Bukkit.getScheduler().runTaskLater(gameManager.plugin, task::cancel, 20 * 6)
            } else {
                player.sendTitle(Colorize.c("&cYOU DIED"), null, 0, 20, 20)

                if(!gameManager.world.getActiveIslands().contains(playerIsland)){
                    Bukkit.broadcastMessage(Colorize.c("${playerIsland.color.formattedName()} &fis out"))
                }

                gameManager.endGameIfNeeded()
            }
        }
    }


    @EventHandler
    fun onDamageByOther(event: EntityDamageByEntityEvent){
        if(event.damager !is Player && event.entity !is Player) return
        val damager: Player = event.damager as Player
        val player: Player = event.entity as Player
        if(event.finalDamage >= player.health){
            damager.sendMessage(Colorize.c("&aYou killed ${player.name}"))
            damager.playSound(damager.location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f)
        }

    }
}