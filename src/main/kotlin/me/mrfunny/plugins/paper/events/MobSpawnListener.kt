package me.mrfunny.plugins.paper.events

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.CreatureSpawnEvent

object MobSpawnListener : Listener{

    @EventHandler
    fun onMobSpawn(event: CreatureSpawnEvent){
        if(event.spawnReason == CreatureSpawnEvent.SpawnReason.NATURAL) event.isCancelled = true
    }
}