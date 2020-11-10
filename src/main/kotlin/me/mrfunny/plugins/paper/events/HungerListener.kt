package me.mrfunny.plugins.paper.events

import me.mrfunny.plugins.paper.manager.GameManager
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.FoodLevelChangeEvent

class HungerListener(private val gameManager: GameManager) : Listener {

    @EventHandler
    fun onSaturation(event: FoodLevelChangeEvent){
        event.isCancelled = true
        if(!gameManager.plugin.config.isConfigurationSection("saturation")){
            gameManager.plugin.config.set("saturation", 20.0)
        }
        (event.entity as Player).saturation = gameManager.plugin.config.getDouble("saturation").toFloat()
    }
}