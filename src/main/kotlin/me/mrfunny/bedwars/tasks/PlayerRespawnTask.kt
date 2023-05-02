package me.mrfunny.bedwars.tasks

import me.mrfunny.bedwars.players.PlayerData
import me.mrfunny.bedwars.worlds.islands.Island
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class PlayerRespawnTask(private var player: Player, private var playerIsland: Island) : Runnable {

    init {
        playerIsland.deadPlayers.add(player.uniqueId)
        player.closeInventory()
    }

    private var timer: Int = 5

    override fun run() {
        if(timer == 0){
            player.sendTitle("${ChatColor.GREEN}RESPAWNED!", "", 20, 20, 20)
            playerIsland.deadPlayers.remove(player.uniqueId)
            player.teleport(playerIsland.spawnLocation)
            PlayerData.get(player).lastRespawn = System.currentTimeMillis()
            player.addPotionEffect(PotionEffect(PotionEffectType.INCREASE_DAMAGE, 15 * 20, 0))
            player.gameMode = GameMode.SURVIVAL
            playerIsland.gameWorld.gameManager.deadPlayers.remove(player.uniqueId)
            return
        }

        player.sendTitle("${ChatColor.RED}YOU DIED", "You'll respawn in ${timer}", 0, 30, 30)

        timer--
    }
}