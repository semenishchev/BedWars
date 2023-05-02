package me.mrfunny.bedwars.events

import me.mrfunny.bedwars.game.GameManager
import me.mrfunny.bedwars.tasks.InvisibilityCheckTask
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityPotionEffectEvent
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.potion.PotionEffectType
import java.util.*


class PotionListener(private val gameManager: GameManager): Listener {

    val invisibilityCheckTaskToUUID = hashMapOf<UUID, InvisibilityCheckTask>()

    @EventHandler
    fun onConsume(event: EntityPotionEffectEvent){
        if(event.entity !is Player) return
        val player: Player = event.entity as Player

        if(event.cause == EntityPotionEffectEvent.Cause.POTION_DRINK){
            Bukkit.getScheduler().runTaskLater(gameManager.plugin, {->
                player.inventory.remove(Material.GLASS_BOTTLE)
                player.updateInventory()
            }, 2)

        }

        if(event.cause == EntityPotionEffectEvent.Cause.POTION_DRINK && event.modifiedType == PotionEffectType.INVISIBILITY){
            gameManager.playerManager.hideItems(player)
            val checkTask = InvisibilityCheckTask(event.entity as Player, gameManager)
            checkTask.runTaskTimer(gameManager.plugin, 0, 10)
            invisibilityCheckTaskToUUID[event.entity.uniqueId] = checkTask
        } else if((event.cause == EntityPotionEffectEvent.Cause.EXPIRATION || event.cause == EntityPotionEffectEvent.Cause.PLUGIN) && event.modifiedType == PotionEffectType.INVISIBILITY) {
            gameManager.playerManager.showItems(player)
            if(invisibilityCheckTaskToUUID[event.entity.uniqueId] == null) {
                gameManager.playerManager.showItems(player)
                return
            }
            if(!invisibilityCheckTaskToUUID[event.entity.uniqueId]!!.isCancelled){
                invisibilityCheckTaskToUUID[event.entity.uniqueId]!!.cancel()
                gameManager.playerManager.showItems(player)
            }
        }
    }

    @EventHandler
    fun onChangeEquipment(event: PlayerItemHeldEvent){
        if(event.player.hasPotionEffect(PotionEffectType.INVISIBILITY)){
            gameManager.playerManager.hideItems(event.player)
        }
    }
}