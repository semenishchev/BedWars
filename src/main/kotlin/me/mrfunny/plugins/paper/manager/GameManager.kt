package me.mrfunny.plugins.paper.manager

import dev.jcsoftware.jscoreboards.JPerPlayerScoreboard
import dev.jcsoftware.jscoreboards.JScoreboardOptions
import dev.jcsoftware.jscoreboards.JScoreboardTabHealthStyle
import me.mrfunny.plugins.paper.BedWars
import me.mrfunny.plugins.paper.config.ConfigurationManager
import me.mrfunny.plugins.paper.gui.GUIManager
import me.mrfunny.plugins.paper.setup.SetupWizardManager
import me.mrfunny.plugins.paper.worlds.GameWorld
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class GameManager(var plugin: BedWars) {


    var scoreboard: JPerPlayerScoreboard
    var setupWizardManager: SetupWizardManager = SetupWizardManager
    var configurationManager: ConfigurationManager = ConfigurationManager(this)
    var guiManager: GUIManager = GUIManager
    lateinit var world: GameWorld

    init {
        this.scoreboard = JPerPlayerScoreboard({ player: Player ->
            val lines = arrayListOf<String>()
//            if (player.gameMode == GameMode.SPECTATOR) {
//                lines.add("&7Мертвый")
//            }
//            lines.add("&a")
            lines
        }, JScoreboardOptions("&a&lBedWars", JScoreboardTabHealthStyle.NUMBER, true))

//        this.configurationManager.loadWorld(this.configurationManager.randomMapName()) { state = GameState.LOBBY }
    }

    var state: GameState = GameState.PRELOBBY
    set(value) {
        field = value
        when(value){
            GameState.STARTING -> {
                Bukkit.getOnlinePlayers().forEach {
                    it.teleport(world.lobbyPosition)
                }
            }
            GameState.ACTIVE -> TODO()
            GameState.WON -> TODO()
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
            else -> TODO()
        }
    }

    fun endGameIfNeeded() {
        if(world.getActiveIslands().size > 1){
            return
        }

    }

}