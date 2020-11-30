package me.mrfunny.plugins.paper.events

import me.mrfunny.plugins.paper.util.TeleportUtil.pullEntityToLocation
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.PrepareItemCraftEvent
import org.bukkit.event.player.PlayerFishEvent
import org.bukkit.event.player.PlayerItemDamageEvent
import org.bukkit.event.weather.WeatherChangeEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable


object ItemListener: Listener {

    @EventHandler
    fun onItemDamage(event: PlayerItemDamageEvent){
        if(event.item.type != Material.SHIELD || event.item.type != Material.FISHING_ROD){
            event.item.itemMeta.isUnbreakable = false
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onWeather(event: WeatherChangeEvent){
        event.isCancelled = true
        event.world.setStorm(false)
        event.world.isThundering = false
    }

    @EventHandler
    fun onFishingRodUse(event: PlayerFishEvent){
        if (event.state == PlayerFishEvent.State.IN_GROUND || event.state == PlayerFishEvent.State.CAUGHT_ENTITY) {
            val pull: Location = event.hook.location
            if(event.player.inventory.itemInMainHand.type == Material.FISHING_ROD){
                event.player.inventory.itemInMainHand.itemMeta.isUnbreakable = false
                if((event.player.inventory.itemInMainHand.itemMeta as Damageable).damage >= 32){
                    (event.player.inventory.itemInMainHand.itemMeta as Damageable).damage = 64
                } else {
                    (event.player.inventory.itemInMainHand.itemMeta as Damageable).damage = 32
                }
            } else if(event.player.inventory.itemInOffHand.type == Material.FISHING_ROD){
                event.player.inventory.itemInOffHand.itemMeta.isUnbreakable = false
                if((event.player.inventory.itemInOffHand.itemMeta as Damageable).damage == 32){
                    (event.player.inventory.itemInOffHand.itemMeta as Damageable).damage = 64
                } else {
                    (event.player.inventory.itemInOffHand.itemMeta as Damageable).damage = 32
                }
            }
            pullEntityToLocation(event.player, pull)

        }
    }

    @EventHandler
    fun onCraft(event: PrepareItemCraftEvent){
        event.inventory.result = ItemStack(Material.AIR)
    }
}