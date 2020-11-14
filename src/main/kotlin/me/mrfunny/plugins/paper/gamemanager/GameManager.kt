package me.mrfunny.plugins.paper.gamemanager

import dev.jcsoftware.jscoreboards.JPerPlayerScoreboard
import dev.jcsoftware.jscoreboards.JScoreboardOptions
import dev.jcsoftware.jscoreboards.JScoreboardTabHealthStyle
import dev.jcsoftware.jscoreboards.JScoreboardTeam
import dev.jcsoftware.jscoreboards.exception.JScoreboardException
import me.mrfunny.plugins.paper.BedWars
import me.mrfunny.plugins.paper.config.ConfigurationManager
import me.mrfunny.plugins.paper.gui.GUIManager
import me.mrfunny.plugins.paper.players.PlayerManager
import me.mrfunny.plugins.paper.setup.SetupWizardManager
import me.mrfunny.plugins.paper.tasks.GameStartingTask
import me.mrfunny.plugins.paper.util.Colorize
import me.mrfunny.plugins.paper.worlds.GameWorld
import me.mrfunny.plugins.paper.worlds.Island
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import java.util.*

class GameManager(var plugin: BedWars) {

    lateinit var scoreboard: JPerPlayerScoreboard
    var setupWizardManager: SetupWizardManager = SetupWizardManager
    var configurationManager: ConfigurationManager = ConfigurationManager(this)
    var guiManager: GUIManager = GUIManager

    val playerManager: PlayerManager = PlayerManager(this)

    lateinit var world: GameWorld

    lateinit var gameStartingTask: GameStartingTask

    init {
        configurationManager.loadWorld(configurationManager.randomMapName()) { world: GameWorld ->
            this.world = world
            this.scoreboard = JPerPlayerScoreboard({ player: Player ->
                val lines = arrayListOf<String>()
                if(state != GameState.ACTIVE){
                    lines.add("&aWaiting...")
                } else {
                    for (island: Island in world.islands){
                        val builder: StringBuilder = StringBuilder()

                        builder.append(island.color.getChatColor()).append(island.color.formattedName()[0]).append(" &f")
                        builder.append(island.color.formattedName()).append(": ")

                        if(island.isBedPlaced()){
                            builder.append("&a✓")
                        } else {
                            if(island.alivePlayerCount() == 0){
                                builder.append("&c✘")
                            } else {
                                builder.append("&a" + island.alivePlayerCount())
                            }
                        }

                        lines.add(builder.toString())
                    }
                }
                lines
            }, JScoreboardOptions("&a&lBedWars", JScoreboardTabHealthStyle.NUMBER, true))
            state = GameState.LOBBY

            for(island: Island in world.islands){
                try{
                    this.scoreboard.createTeam(island.color.formattedName(), island.color.toString())
                } catch (ex: JScoreboardException){
                    ex.printStackTrace()
                }
            }
        }


    }

    var state: GameState = GameState.PRELOBBY
    set(value) {
        field = value
        when(value){
            GameState.LOBBY -> {
                Bukkit.getOnlinePlayers().forEach {
                    it.teleport(world.lobbyPosition)
                }

                playerManager.giveAllTeamSelector()
            }

            GameState.STARTING -> {
                gameStartingTask = GameStartingTask(this)
                gameStartingTask.runTaskTimer(plugin, 0, 20)
            }

            GameState.ACTIVE -> {
                gameStartingTask.cancel()

                for(player: Player in Bukkit.getOnlinePlayers()) {
                    playerManager.setPlaying(player)
                    val island: Island? = world.getIslandForPlayer(player)

                    if(island == null){
                        val optionalIsland: Optional<Island> = world.islands.stream().filter {
                            return@filter it.players.size < world.maxTeamSize
                        }.findAny()

                        if(!optionalIsland.isPresent){
                            player.kickPlayer("Not enough islands")
                            continue
                        }
                        optionalIsland.get().players.add(player)
                        scoreboard.findTeam(optionalIsland.get().color.formattedName()).get().addPlayer(player)
                    }

                    playerManager.setPlaying(player)
                }

            }
            GameState.WON -> {
                val finalIsland: Optional<Island> = world.getActiveIslands().stream().findFirst()
                if(!finalIsland.isPresent){
                    Bukkit.broadcastMessage(Colorize.c("&fНИЧЬЯ"))
                } else {
                    val island: Island = finalIsland.get()
                    Bukkit.broadcastMessage(Colorize.c("Команда ${island.color.formattedName()} победили!"))
                    var winners: String = ""
                    island.players.forEach {
                        winners += it.name + ", "
                        it.sendTitle(Colorize.c("&l&6ПОБЕДА"), null, 0, 30, 20)
                    }
                    Bukkit.broadcastMessage(Colorize.c("&8Победители: &a$winners"))

                    Bukkit.getScheduler().runTaskLater(plugin, {task ->

                        println("Reseting task ${task.taskId}")
                        state = GameState.RESET
                    }
                    , 20 * 10)
                }
            }
            GameState.RESET -> {
                Bukkit.getOnlinePlayers().forEach {
                    it.kickPlayer("Server restarting")
                }

                Bukkit.getScheduler().runTaskLater(plugin, {task ->
                    world.resetWorld()
                    println("[BedWars] Game ${task.taskId} reset")
                    Bukkit.spigot().restart()
                }, 20)
            }
            else -> {
                println("###################")
                println("\n\nInvalid game state. If you see this, it is most likely a bug. Report on https://github.com/SashaSemenishchev/BedWars/issues\n\n")
                println("###################")
            }
        }
    }

    fun endGameIfNeeded() {
        if(world.getActiveIslands().size > 1){
            return
        }

        state = GameState.WON
    }

}