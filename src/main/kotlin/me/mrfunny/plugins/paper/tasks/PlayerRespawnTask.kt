package me.mrfunny.plugins.paper.tasks

import me.mrfunny.plugins.paper.gamemanager.GameManager
import me.mrfunny.plugins.paper.util.Colorize
import me.mrfunny.plugins.paper.players.PlayerData
import me.mrfunny.plugins.paper.worlds.Island
import org.bukkit.GameMode
import org.bukkit.entity.Player

class PlayerRespawnTask(private var player: Player, var playerIsland: Island, private var gamaManager: GameManager) : Runnable {

    init {
        playerIsland.absolutelyAlive.add(player.uniqueId)
    }

    var tick: Int = 0

    override fun run() {
        if(tick == 5){
            player.sendTitle(Colorize.c("&aRespawned"), null, 20, 20, 20)
            playerIsland.absolutelyAlive.remove(player.uniqueId)
            player.gameMode = GameMode.SURVIVAL
            player.teleport(playerIsland.spawnLocation!!)
            PlayerData.PLAYERS[player.uniqueId]?.health = 20.0
            return
        }

        player.sendTitle(Colorize.c("&cYOU DIED"), Colorize.c("&aRespawn at ${5 - tick}..."), 0, 30, 30)

        tick++
    }
}