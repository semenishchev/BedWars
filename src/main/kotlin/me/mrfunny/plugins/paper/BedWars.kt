package me.mrfunny.plugins.paper

import me.mrfunny.plugins.paper.commands.ForcestartCommand
import me.mrfunny.plugins.paper.commands.SetupWizardCommand
import me.mrfunny.plugins.paper.commands.StartCommand
import me.mrfunny.plugins.paper.events.*
import me.mrfunny.plugins.paper.gamemanager.GameManager
import me.mrfunny.plugins.paper.gamemanager.GameState
import me.mrfunny.plugins.paper.util.Colorize
import me.mrfunny.plugins.paper.players.PlayerData
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin


class BedWars : JavaPlugin() {

    lateinit var gameManager: GameManager
    lateinit var instance: BedWars

    override fun onEnable() {
        instance = this
        gameManager = GameManager(this)

        server.pluginManager.getPlugin("ExampleJScoreboardPlugin")?.let {
            server.pluginManager.disablePlugin(
                it
            )
        }

        Bukkit.getWorlds().forEach{
            println(it)
        }

        getCommand("setup")?.setExecutor(SetupWizardCommand(gameManager))
        getCommand("start")?.setExecutor(StartCommand(gameManager))
        getCommand("forcestart")?.setExecutor(ForcestartCommand(gameManager))

        server.onlinePlayers.forEach {
            PlayerData.PLAYERS[it.uniqueId] = PlayerData(it.uniqueId)
            gameManager.scoreboard.addPlayer(it)
        }

        gameManager.state = GameState.LOBBY

        server.pluginManager.registerEvents(PlayerLoginEventListener(gameManager), this)
        server.pluginManager.registerEvents(PlayerItemInteractListener(gameManager), this)
        server.pluginManager.registerEvents(InventoryClickListener(gameManager), this)
        server.pluginManager.registerEvents(BlockUpdateListener(gameManager), this)
        server.pluginManager.registerEvents(PlayerDeathListener(gameManager), this)
        server.pluginManager.registerEvents(HungerListener(gameManager), this)
        server.pluginManager.registerEvents(ChatListeners(gameManager), this)
        server.pluginManager.registerEvents(ItemListener, this)
        server.pluginManager.registerEvents(MobSpawnListener, this)
        server.pluginManager.registerEvents(PotionListener(gameManager), this)
    }



    override fun onDisable() {
        gameManager = GameManager(instance)
        gameManager.scoreboard.destroy()
        gameManager.world.resetWorld()
    }

    companion object {
        fun String.colorize() = Colorize.c(this)
    }

}