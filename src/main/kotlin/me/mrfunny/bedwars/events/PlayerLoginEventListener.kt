package me.mrfunny.bedwars.events

import me.mrfunny.bedwars.BedWars.Companion.colorize
import me.mrfunny.bedwars.BedWars.Companion.removeIfBad
import me.mrfunny.bedwars.game.GameManager
import me.mrfunny.bedwars.game.GameState
import me.mrfunny.bedwars.players.PlayerData
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.OfflinePlayer
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.*
import java.util.*

class PlayerLoginEventListener(private val gameManager: GameManager): Listener {

    private val maxPercentage = gameManager.gameConfig.get().getInt("percentage-to-start")

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

    @EventHandler(priority = EventPriority.MONITOR)
    fun onLogin(event: PlayerLoginEvent){
        if(!gameManager.state.isPreGame()) {
            gameManager.playerManager.setSpectatorMode(event.player)
        }
        event.player.teleport(gameManager.world.lobbyPosition)
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onJoin(event: PlayerJoinEvent) {
        val player = event.player
        val world = gameManager.world
        player.isCollidable = true
        for (it in player.activePotionEffects) {
            event.player.removePotionEffect(it.type)
        }
        event.joinMessage(null)
        if(!gameManager.world.isMapInit()){
            gameManager.setupWizardManager.activateSetupWizard(player, world)
            return
        }
        PlayerData.new(player, gameManager)

        if (gameManager.state.isPreGame()) {
            event.player.gameMode = GameMode.ADVENTURE
            event.player.health = 20.0
            event.player.teleport(world.lobbyPosition)
            event.player.enderChest.clear()
            event.player.inventory.clear()
            gameManager.playerManager.playerTeamSelector(event.player)
            val maxPlayers: Int = gameManager.world.islands.size * world.maxTeamSize
            val percentage: Double = (Bukkit.getOnlinePlayers().size.toDouble() / maxPlayers.toDouble()) * 100.0
            if(percentage >= maxPercentage.toDouble()){
                if(gameManager.state != GameState.STARTING){
                    gameManager.state = GameState.STARTING
                }
            }
            gameManager.endGameIfNeeded()
//            event.joinMessage(player.displayName())
            //            gameManager.playerToLocaleMap[event.player] = (event.player as CraftPlayer).handle.locale
            event.joinMessage = "${event.player.name} connected (${Bukkit.getOnlinePlayers().size}/${gameManager.world.maxTeamSize * gameManager.world.islands.size})".colorize()
        }
        gameManager.scoreboard.addPlayer(event.player)
        gameManager.updateScoreboard(true)
        gameManager.bossBar.addPlayer(event.player)

        if(Bukkit.getOnlinePlayers().size != 1) return
        for (entity in player.world.entities) {
            entity?.removeIfBad()
        }
    }


    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        event.quitMessage = null
        PlayerData.remove(event.player)
        gameManager.scoreboard.removePlayer(event.player)
        event.player.activePotionEffects.forEach {
            event.player.removePotionEffect(it.type)
        }
        gameManager.world.getIslandForPlayer(event.player)?.let { island ->
            gameManager.scoreboard.findTeam(island.color.formattedName()).get().removePlayer(event.player)
            island.removeMember(event.player)
        }

        gameManager.endGameIfNeeded()

        if(gameManager.state == GameState.STARTING || gameManager.state == GameState.LOBBY){
            val onlinePlayers = PlayerData.values().size - 1
            val maxPlayers: Int = gameManager.world.islands.size * gameManager.world.maxTeamSize
            event.quitMessage = "${event.player.name} disconnected (${onlinePlayers}/$maxPlayers)".colorize()

            val percentage: Double = (onlinePlayers / maxPlayers) * 100.0
            if(percentage < maxPercentage.toDouble()){
                gameManager.state = GameState.LOBBY
            }
            return
        }
        gameManager.updateScoreboard(true)
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