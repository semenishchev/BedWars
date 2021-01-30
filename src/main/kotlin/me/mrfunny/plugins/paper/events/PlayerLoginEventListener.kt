package me.mrfunny.plugins.paper.events

import me.mrfunny.plugins.paper.BedWars.Companion.colorize
import me.mrfunny.plugins.paper.gamemanager.GameManager
import me.mrfunny.plugins.paper.gamemanager.GameState
import me.mrfunny.plugins.paper.players.PlayerData
import me.mrfunny.plugins.paper.worlds.Island
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.OfflinePlayer
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.*
import java.util.*

class PlayerLoginEventListener(private val gameManager: GameManager): Listener {

    val maxPercentage = gameManager.gameConfig.get().getInt("percentage-to-start")

    @EventHandler
    fun onLogin(event: AsyncPlayerPreLoginEvent){
        if(gameManager.state == GameState.RESET){
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "Game restarting")
            return
        }

        val uuid: UUID = event.uniqueId
        val player: OfflinePlayer = Bukkit.getOfflinePlayer(uuid)

        if(!player.isOp && gameManager.state == GameState.PRELOBBY){
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "The game didn't started yet")
            return
        }

        if(gameManager.state == GameState.LOBBY || gameManager.state == GameState.STARTING){
            if(Bukkit.getOnlinePlayers().size == gameManager.world.maxTeamSize * gameManager.world.maxTeams){
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "The game is full")
            }
        }
    }

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        event.player.activePotionEffects.forEach {
            event.player.removePotionEffect(it.type)
        }
        event.joinMessage = null
        if(!gameManager.world.isMapInit()){
            gameManager.setupWizardManager.activateSetupWizard(event.player, gameManager.world)
            return
        }
        PlayerData.PLAYERS[event.player.uniqueId] = PlayerData(event.player.uniqueId, gameManager)
        gameManager.scoreboard.addPlayer(event.player)
        gameManager.updateScoreboard()
        gameManager.bossBar.addPlayer(event.player)
        gameManager.endGameIfNeeded()

        if (gameManager.state == GameState.ACTIVE) {
            // todo: make reconnect work
            gameManager.playerManager.setSpectatorMode(event.player)
        } else if (gameManager.state == GameState.LOBBY || gameManager.state == GameState.STARTING) {
            event.player.gameMode = GameMode.ADVENTURE
            event.player.health = 20.0
            event.player.teleport(gameManager.world.lobbyPosition)
            event.player.enderChest.clear()
            event.player.inventory.clear()
            gameManager.playerManager.playerTeamSelector(event.player)
            val maxPlayers: Int = gameManager.world.islands.size * gameManager.world.maxTeamSize
            val percentage: Double = (Bukkit.getOnlinePlayers().size.toDouble() / maxPlayers.toDouble()) * 100.0
            println("$maxPlayers $maxPercentage $percentage")
            if(percentage >= maxPercentage.toDouble()){
                gameManager.state = GameState.STARTING
            }
//            gameManager.playerToLocaleMap[event.player] = (event.player as CraftPlayer).handle.locale
            event.joinMessage = "${event.player.name} connected (${Bukkit.getOnlinePlayers().size}/${gameManager.world.maxTeamSize * gameManager.world.islands.size})".colorize()
        }
    }


    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        event.quitMessage = null
        PlayerData.PLAYERS.remove(event.player.uniqueId)
        gameManager.scoreboard.removePlayer(event.player)
        gameManager.updateScoreboard()
        event.player.activePotionEffects.forEach {
            event.player.removePotionEffect(it.type)
        }
        if(gameManager.world.getIslandForPlayer(event.player) != null){
            val island: Island = gameManager.world.getIslandForPlayer(event.player)!!
            gameManager.scoreboard.findTeam(island.color.formattedName()).get().removePlayer(event.player)
            island.removeMember(event.player)
//            island.leavedPlayers[event.player.uniqueId] = NPC(event.player, island)
        }
        gameManager.endGameIfNeeded()

        if(gameManager.state == GameState.STARTING || gameManager.state == GameState.LOBBY){
            event.quitMessage = "${event.player.name} disconnected (${Bukkit.getOnlinePlayers().size - 1}/${gameManager.world.maxTeamSize * gameManager.world.islands.size})".colorize()
            val maxPlayers: Int = gameManager.world.islands.size * gameManager.world.maxTeamSize
            val percentage: Double = ((Bukkit.getOnlinePlayers().size.toDouble() - 1.0) / maxPlayers.toDouble()) * 100.0
            if(percentage < maxPercentage.toDouble()){
                gameManager.state = GameState.LOBBY
            }
            return
        }
    }

    @EventHandler
    fun onDrop(event: PlayerDropItemEvent){
        if(gameManager.state != GameState.ACTIVE) event.isCancelled = true
        if(gameManager.state == GameState.ACTIVE && event.player.fallDistance > 0){
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onPortal(event: PlayerPortalEvent){
        event.isCancelled = true
    }
}