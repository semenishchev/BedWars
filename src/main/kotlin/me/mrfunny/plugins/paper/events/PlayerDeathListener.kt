package me.mrfunny.plugins.paper.events

import org.bukkit.GameMode
import org.bukkit.attribute.Attribute
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent

class PlayerDeathListener : Listener {

    @EventHandler
    fun onDeath(event: PlayerDeathEvent){
        val player = event.entity

        player.spigot().respawn()
        player.health = player.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.value
        player.gameMode = GameMode.SPECTATOR
    }
}