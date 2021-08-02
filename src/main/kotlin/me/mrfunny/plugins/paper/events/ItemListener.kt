package me.mrfunny.plugins.paper.events

import me.mrfunny.plugins.paper.util.TeleportUtil.pullEntityToLocation
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.PrepareItemCraftEvent
import org.bukkit.event.player.PlayerFishEvent
import org.bukkit.event.player.PlayerItemDamageEvent
import org.bukkit.event.weather.WeatherChangeEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import org.bukkit.inventory.meta.ItemMeta


object ItemListener: Listener {

    @EventHandler
    fun onItemDamage(event: PlayerItemDamageEvent){
        if(event.item.type != Material.SHIELD || event.item.type != Material.FISHING_ROD){
            if(event.item.type != Material.BOW){
                event.isCancelled = true
                return
            }
        } else if(event.item.type == Material.BOW){
            if(event.item.itemMeta.hasEnchant(Enchantment.ARROW_INFINITE)){
                return
            }
        } else if(event.item.type.name.contains("AXE")){
            return
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
        if (event.state == PlayerFishEvent.State.IN_GROUND || event.state == PlayerFishEvent.State.REEL_IN || event.state == PlayerFishEvent.State.CAUGHT_ENTITY) {
            val pull: Location = event.hook.location
            if(event.player.inventory.itemInMainHand.type == Material.FISHING_ROD){
                if(!event.player.inventory.itemInMainHand.hasItemMeta()) return
                val meta: ItemMeta = event.player.inventory.itemInMainHand.itemMeta
                if((meta as Damageable).damage < 61){
                    meta.damage = 62
                } else {
                    meta.damage++
                }
                if(meta.damage == 64){
                    event.player.inventory.removeItem(event.player.inventory.itemInMainHand)
                    pullEntityToLocation(event.player, pull)
                    return
                }
                event.player.inventory.itemInMainHand.itemMeta = meta
                pullEntityToLocation(event.player, pull)
            } else if(event.player.inventory.itemInOffHand.type == Material.FISHING_ROD){
//                if(!event.player.inventory.itemInOffHand.hasItemMeta()) return
                val meta: ItemMeta = event.player.inventory.itemInOffHand.itemMeta
                if(!meta.displayName.contains("Hook")) return
                if((meta as Damageable).damage < 61){
                    meta.damage = 61
                } else {
                    meta.damage++
                }
                if(meta.damage == 64){
                    event.player.inventory.removeItem(event.player.inventory.itemInOffHand)
                    pullEntityToLocation(event.player, pull)
                    return
                }
                event.player.inventory.itemInOffHand.itemMeta = meta
                pullEntityToLocation(event.player, pull)
            }
            return
        } else if(event.state == PlayerFishEvent.State.CAUGHT_FISH){
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onCraft(event: PrepareItemCraftEvent){
        event.inventory.result = ItemStack(Material.AIR)
    }
}