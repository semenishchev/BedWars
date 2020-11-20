package me.mrfunny.plugins.paper.events

import org.bukkit.entity.EntityType
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.CreatureSpawnEvent

object MobSpawnListener : Listener{

    @EventHandler
    fun onMobSpawn(event: CreatureSpawnEvent){
//        val type: EntityType = event.entityType

        if(event.spawnReason == CreatureSpawnEvent.SpawnReason.NATURAL) event.isCancelled = true

//        event.isCancelled = !(type == EntityType.VILLAGER ||
//                    type == EntityType.PLAYER ||
//                    type == EntityType.SPLASH_POTION ||
//                    type == EntityType.SKELETON ||
//                    type == EntityType.DROPPED_ITEM ||
//                    type == EntityType.ARROW ||
//                    type == EntityType.EGG ||
//                    type == EntityType.PRIMED_TNT ||
//                    type == EntityType.FIREBALL ||
//                    type == EntityType.SNOWBALL ||
//                    type == EntityType.ENDER_PEARL ||
//                    type == EntityType.LIGHTNING ||
//                    type == EntityType.SILVERFISH)
    }
}