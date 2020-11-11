package me.mrfunny.plugins.paper.events

import me.mrfunny.plugins.paper.gamemanager.GameManager
import me.mrfunny.plugins.paper.gamemanager.GameState
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerPreLoginEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.*

class PlayerLoginEventListener(private val gameManager: GameManager) : Listener {

    @EventHandler
    fun onLogin(event: AsyncPlayerPreLoginEvent){
        if(gameManager.state == GameState.RESET){
            event.disallow(PlayerPreLoginEvent.Result.KICK_OTHER, "Game restarting")
            return
        }

        val uuid: UUID = event.uniqueId
        val player: OfflinePlayer = Bukkit.getOfflinePlayer(uuid)

        if(!player.isOp && gameManager.state == GameState.PRELOBBY){
            event.disallow(PlayerPreLoginEvent.Result.KICK_OTHER, "The game didn't started yet")
        }
    }

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        event.joinMessage = null
        gameManager.scoreboard.addPlayer(event.player)

        if (gameManager.state == GameState.ACTIVE) {
            val world = gameManager.world
            val playerIsland = world.getIslandForPlayer(event.player)

            if(playerIsland != null){
                if(playerIsland.isBedPlaced()){
                    event.player.teleport(playerIsland.spawnLocation!!)
                }
            }
        } else if (gameManager.state == GameState.LOBBY) {
            event.player.teleport(gameManager.world.lobbyPosition)
        }
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        event.quitMessage = null
        gameManager.scoreboard.removePlayer(event.player)
    }
}