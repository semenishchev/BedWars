package me.mrfunny.plugins.paper.events

import me.mrfunny.plugins.paper.BedWars.Companion.colorize
import me.mrfunny.plugins.paper.gamemanager.GameManager
import me.mrfunny.plugins.paper.util.Cooldowns
import me.mrfunny.plugins.paper.util.TeleportUtil.pullEntityToLocation
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Container
import org.bukkit.enchantments.Enchantment
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.PrepareItemCraftEvent
import org.bukkit.event.player.PlayerAttemptPickupItemEvent
import org.bukkit.event.player.PlayerFishEvent
import org.bukkit.event.player.PlayerItemDamageEvent
import org.bukkit.event.weather.WeatherChangeEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import org.bukkit.inventory.meta.ItemMeta
import java.util.*


class ItemListener(private val gameManager: GameManager): Listener {

    @EventHandler
    fun onItemDamage(event: PlayerItemDamageEvent){
        if(event.item.type != Material.SHIELD || event.item.type != Material.FISHING_ROD){
            if(event.item.type != Material.BOW){
                event.isCancelled = true
                return
            }
        } else if(event.item.type == Material.BOW){
            if(event.item.itemMeta!!.hasEnchant(Enchantment.ARROW_INFINITE)){
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
        if(gameManager.cooldowns.grapplingHook.contains(event.player.uniqueId)) {
            event.player.sendMessage("&cGrappling Hook is on cooldown".colorize())
            return
        }
        if (event.state == PlayerFishEvent.State.IN_GROUND || event.state == PlayerFishEvent.State.REEL_IN || event.state == PlayerFishEvent.State.CAUGHT_ENTITY) {
            val pull: Location = event.hook.location
            if(event.player.inventory.itemInMainHand.type == Material.FISHING_ROD){
                if(!event.player.inventory.itemInMainHand.hasItemMeta()) return
                val meta: ItemMeta = event.player.inventory.itemInMainHand.itemMeta!!
                if((meta as Damageable).damage < 61){
                    meta.damage = 63
                } else {
                    meta.damage++
                }
                if(meta.damage == 64){
                    event.player.inventory.removeItem(event.player.inventory.itemInMainHand)
                    pullEntityToLocation(gameManager, event.player, pull)
                    cooldownRod(event.player.uniqueId)
                    return
                }
                event.player.inventory.itemInMainHand.itemMeta = meta
                pullEntityToLocation(gameManager, event.player, pull)
            } else if(event.player.inventory.itemInOffHand.type == Material.FISHING_ROD){
                val meta: ItemMeta = event.player.inventory.itemInOffHand.itemMeta!!
                if(!meta.displayName.contains("Hook")) return
                if((meta as Damageable).damage < 61){
                    meta.damage = 61
                } else {
                    meta.damage++
                }
                if(meta.damage == 64){
                    event.player.inventory.removeItem(event.player.inventory.itemInOffHand)
                    pullEntityToLocation(gameManager, event.player, pull)
                    cooldownRod(event.player.uniqueId)
                    return
                }
                event.player.inventory.itemInOffHand.itemMeta = meta
                pullEntityToLocation(gameManager, event.player, pull)
            }
            return
        } else if(event.state == PlayerFishEvent.State.CAUGHT_FISH){
            event.isCancelled = true
        }
    }

    fun cooldownRod(uuid: UUID){
        gameManager.cooldowns.grapplingHook.add(uuid)
        Bukkit.getScheduler().runTaskLater(gameManager.plugin, {-> gameManager.cooldowns.grapplingHook.remove(uuid)}, 100)
    }

    @EventHandler
    fun onCraft(event: PrepareItemCraftEvent){
        event.inventory.result = null
    }

    @EventHandler
    fun onPickup(event: PlayerAttemptPickupItemEvent){
        if(gameManager.deadPlayers.contains(event.player.uniqueId)){
            event.isCancelled = true
        }
    }
}