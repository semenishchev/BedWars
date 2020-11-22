package me.mrfunny.plugins.paper.events

import me.mrfunny.plugins.paper.gamemanager.GameManager
import me.mrfunny.plugins.paper.gamemanager.GameState
import me.mrfunny.plugins.paper.tasks.PlayerRespawnTask
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.OfflinePlayer
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerPreLoginEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.scheduler.BukkitTask
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
        gameManager.updateScoreboard()

        if (gameManager.state == GameState.ACTIVE) {
            val world = gameManager.world
            val playerIsland = world.getIslandForPlayer(event.player)

            if(playerIsland != null){
                if(playerIsland.isBedPlaced()){
                    gameManager.playerManager.setSpectatorMode(event.player)
                    val task: BukkitTask = Bukkit.getScheduler().runTaskTimer(gameManager.plugin, PlayerRespawnTask(event.player, gameManager.world.getIslandForPlayer(event.player)!!, gameManager), 0, 20)
                    Bukkit.getScheduler().runTaskLater(gameManager.plugin, task::cancel, 20 * 6)
                    return
                }
            }

            gameManager.playerManager.setSpectatorMode(event.player)
        } else if (gameManager.state == GameState.LOBBY || gameManager.state == GameState.STARTING) {
            event.player.gameMode = GameMode.SURVIVAL
            event.player.teleport(gameManager.world.lobbyPosition)
            event.player.enderChest.clear()
            event.player.inventory.clear()
            gameManager.playerManager.playerTeamSelector(event.player)
        }
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        event.quitMessage = null
        gameManager.scoreboard.removePlayer(event.player)
        gameManager.updateScoreboard()
        gameManager.endGameIfNeeded()
    }
}