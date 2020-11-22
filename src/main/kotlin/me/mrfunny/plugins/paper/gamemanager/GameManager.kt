package me.mrfunny.plugins.paper.gamemanager

import dev.jcsoftware.jscoreboards.JScoreboard
import dev.jcsoftware.jscoreboards.JScoreboardOptions
import dev.jcsoftware.jscoreboards.JScoreboardTabHealthStyle
import dev.jcsoftware.jscoreboards.exception.JScoreboardException
import me.mrfunny.plugins.paper.BedWars
import me.mrfunny.plugins.paper.config.ConfigurationManager
import me.mrfunny.plugins.paper.gui.GUIManager
import me.mrfunny.plugins.paper.players.PlayerManager
import me.mrfunny.plugins.paper.setup.SetupWizardManager
import me.mrfunny.plugins.paper.tasks.GameStartingTask
import me.mrfunny.plugins.paper.tasks.GameTickTask
import me.mrfunny.plugins.paper.util.Colorize
import me.mrfunny.plugins.paper.worlds.GameWorld
import me.mrfunny.plugins.paper.worlds.Island
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*

class GameManager(var plugin: BedWars) {

    var scoreboard: JScoreboard = JScoreboard(JScoreboardOptions("&a&lBedWars", JScoreboardTabHealthStyle.NUMBER, true))
    var setupWizardManager: SetupWizardManager = SetupWizardManager
    var configurationManager: ConfigurationManager = ConfigurationManager(this)
    var guiManager: GUIManager = GUIManager

    val playerManager: PlayerManager = PlayerManager(this)

    lateinit var world: GameWorld

    lateinit var gameStartingTask: GameStartingTask
    lateinit var gameTickTask: GameTickTask

    init {

        configurationManager.loadWorld(configurationManager.randomMapName()) { world: GameWorld ->
            this.world = world
            state = GameState.LOBBY

            for(island: Island in world.islands){
                try{
                    val islandColorChar: Char = island.color.getChatColor().char
                    this.scoreboard.createTeam(island.color.formattedName(), "&$islandColorChar")
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
                updateScoreboard()
                Bukkit.getOnlinePlayers().forEach {
                    it.teleport(world.lobbyPosition)
                }

                playerManager.giveAllTeamSelector()
            }

            GameState.STARTING -> {
                updateScoreboard()
                gameStartingTask = GameStartingTask(this)
                gameStartingTask.runTaskTimer(plugin, 0, 20)
            }

            GameState.ACTIVE -> {
                gameStartingTask.cancel()
                this.gameTickTask = GameTickTask(this)
                this.gameTickTask.runTaskTimer(plugin, 0, 20)

                for(player: Player in Bukkit.getOnlinePlayers()) {
                    val island: Island? = world.getIslandForPlayer(player)
                    player.saturation = 20f
                    player.health = 20.0
                    player.foodLevel = 20

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

                world.islands.forEach {
                    it.spawnShops()
                }

            }
            GameState.WON -> {
                this.gameTickTask.cancel()
                val finalIsland: Optional<Island> = world.getActiveIslands().stream().findFirst()
                if(!finalIsland.isPresent){
                    Bukkit.broadcastMessage(Colorize.c("&fНИЧЬЯ"))
                } else {
                    val island: Island = finalIsland.get()
                    Bukkit.broadcastMessage(Colorize.c("Команда ${island.color.formattedName()} победили!"))
                    var winners = ""
                    island.players.forEach {
                        winners += it.name + ", "
                        it.sendTitle(Colorize.c("&l&6ПОБЕДА"), null, 0, 30, 20)
                    }
                    Bukkit.broadcastMessage(Colorize.c("&8Победители: &a$winners"))

                    updateScoreboard()

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
        if(state != GameState.ACTIVE) return

        if(world.getActiveIslands().size > 1){
            return
        }

        state = GameState.WON
    }

    fun updateScoreboard(){
            val lines = arrayListOf<String>()
            if(state == GameState.LOBBY || state == GameState.STARTING){
                lines.add("&fКарта: ${world.world.name.replace("_playing", "")}")
                lines.add("&fИгроков: &a${Bukkit.getOnlinePlayers().size}/${world.maxTeamSize * world.islands.size}")
            } else {
                for (island: Island in world.islands){
                    if(island.players.size == 0) continue
                    val builder: StringBuilder = StringBuilder()

                    builder.append("&").append(island.color.getChatColor().char).append(island.color.formattedName()[0]).append(" &f")
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

                if(lines.isEmpty()){
                    lines.add("")
                }
            }

            scoreboard.setLines(lines)

        for(player: Player in Bukkit.getOnlinePlayers()){
            val island: Island? = world.getIslandForPlayer(player)

            if(island != null){
                player.setPlayerListName(Colorize.c("&${island.color.getChatColor().char}${player.displayName}"))
            }
        }
    }

}