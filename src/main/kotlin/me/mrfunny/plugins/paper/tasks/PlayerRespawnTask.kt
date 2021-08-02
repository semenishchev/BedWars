package me.mrfunny.plugins.paper.tasks

import me.mrfunny.plugins.paper.util.Colorize
import me.mrfunny.plugins.paper.players.PlayerData
import me.mrfunny.plugins.paper.util.ItemBuilder
import me.mrfunny.plugins.paper.worlds.Island
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class PlayerRespawnTask(private var player: Player, var playerIsland: Island) : Runnable {

    init {
        playerIsland.absolutelyAlive.add(player.uniqueId)
        player.closeInventory()
    }

    var tick: Int = 0

    override fun run() {
        if(tick == 5){
            player.sendTitle(Colorize.c("&a${if(PlayerData.PLAYERS[player.uniqueId]!!.isRussian()) "ВОЗРОЖДЁН" else "RESPAWNED"}!"), "", 20, 20, 20)
            playerIsland.absolutelyAlive.remove(player.uniqueId)
            player.teleport(playerIsland.spawnLocation!!)
            PlayerData.PLAYERS[player.uniqueId]!!.lastRespawn = System.currentTimeMillis()
            player.addPotionEffect(PotionEffect(PotionEffectType.INCREASE_DAMAGE, 15 * 20, 0))
            player.gameMode = GameMode.SURVIVAL
            return
        }

        if(5 - tick != 0){
            player.sendTitle(Colorize.c("&c${if(PlayerData.PLAYERS[player.uniqueId]!!.isRussian()) "ВЫ УМЕРЛИ" else "YOU DIED"}!"),
                Colorize.c("&a${if(PlayerData.PLAYERS[player.uniqueId]!!.isRussian()) "Респавн через" else "Respawn in"} ${5 - tick}..."), 0, 30, 30)
        }

        tick++
    }
}