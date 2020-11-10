package me.mrfunny.plugins.paper.events

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerItemDamageEvent

object ItemListener : Listener {

    @EventHandler
    fun onItemDamage(event: PlayerItemDamageEvent){
        event.isCancelled = true
    }
}