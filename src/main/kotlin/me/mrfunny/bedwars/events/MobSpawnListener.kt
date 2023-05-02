package me.mrfunny.bedwars.events

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.CreatureSpawnEvent

object MobSpawnListener : Listener{

    @EventHandler
    fun onMobSpawn(event: CreatureSpawnEvent){
        if(event.spawnReason != CreatureSpawnEvent.SpawnReason.CUSTOM && event.spawnReason != CreatureSpawnEvent.SpawnReason.DEFAULT) event.isCancelled = true
    }
}