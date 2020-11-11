package me.mrfunny.plugins.paper.gamemanager

import dev.jcsoftware.jscoreboards.JPerPlayerScoreboard
import dev.jcsoftware.jscoreboards.JScoreboardOptions
import dev.jcsoftware.jscoreboards.JScoreboardTabHealthStyle
import me.mrfunny.plugins.paper.BedWars
import me.mrfunny.plugins.paper.config.ConfigurationManager
import me.mrfunny.plugins.paper.gui.GUIManager
import me.mrfunny.plugins.paper.players.PlayerManager
import me.mrfunny.plugins.paper.setup.SetupWizardManager
import me.mrfunny.plugins.paper.tasks.GameStartingTask
import me.mrfunny.plugins.paper.worlds.GameWorld
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player

class GameManager(var plugin: BedWars) {


    var scoreboard: JPerPlayerScoreboard
    var setupWizardManager: SetupWizardManager = SetupWizardManager
    var configurationManager: ConfigurationManager = ConfigurationManager(this)
    var guiManager: GUIManager = GUIManager

    private val playerManager: PlayerManager = PlayerManager(this)

    lateinit var world: GameWorld

    lateinit var gameStartingTask: GameStartingTask

    var state: GameState = GameState.PRELOBBY
    set(value) {
        field = value
        when(value){
            GameState.LOBBY -> {
                Bukkit.getOnlinePlayers().forEach {
                    it.teleport(world.lobbyPosition)
                }
            }

            GameState.STARTING -> {
                gameStartingTask = GameStartingTask(this)
                gameStartingTask.runTaskTimer(plugin, 0, 20)
            }

            GameState.ACTIVE -> {
                gameStartingTask.cancel()

                Bukkit.getOnlinePlayers().forEach {
                    playerManager.setPlaying(it)

                }
            }

            GameState.WON -> {
                Bukkit.broadcastMessage("${ChatColor.GREEN}MisterFunny01 won the game!")
            }
            GameState.RESET -> {
                Bukkit.getOnlinePlayers().forEach {
                    it.kickPlayer("Server restarting")
                }

                Bukkit.getScheduler().runTaskLater(plugin, {task ->
                    world.resetWorld()
                    println("[BedWars] Game ${task.taskId} reset")
                    Bukkit.shutdown()
                }, 20)
            }
            else -> {
                println("###################")
                println("\n\nInvalid game state. If you see this, it is most likely a bug. Report on https://github.com/SashaSemenishchev/BedWars/issues\n\n")
                println("###################")
            }
        }
    }

    init {
        this.configurationManager.loadWorld(this.configurationManager.randomMapName()) { state = GameState.LOBBY }

        this.scoreboard = JPerPlayerScoreboard({ player: Player ->
            val lines = arrayListOf<String>()
            lines.add("State: $state")
            lines
        }, JScoreboardOptions("&a&lBedWars", JScoreboardTabHealthStyle.NUMBER, true))

    }

    fun endGameIfNeeded() {
        if(world.getActiveIslands().size > 1){
            return
        }

        state = GameState.WON
    }

}