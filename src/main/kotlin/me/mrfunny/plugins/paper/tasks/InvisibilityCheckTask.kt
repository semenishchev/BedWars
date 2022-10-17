package me.mrfunny.plugins.paper.tasks

import me.mrfunny.plugins.paper.gamemanager.GameManager
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable

class InvisibilityCheckTask(private val player: Player, private val gameManager: GameManager): BukkitRunnable() {
    override fun run() {
        if(player.hasPotionEffect(PotionEffectType.INVISIBILITY)){
            gameManager.playerManager.hideItems(player)
        } else {
            gameManager.playerManager.showItems(player)
            cancel()
        }
    }
}