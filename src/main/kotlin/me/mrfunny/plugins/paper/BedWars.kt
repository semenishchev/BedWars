package me.mrfunny.plugins.paper

import me.mrfunny.plugins.paper.commands.SetupWizardCommand
import me.mrfunny.plugins.paper.commands.StartCommand
import me.mrfunny.plugins.paper.events.*
import me.mrfunny.plugins.paper.manager.GameManager
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

        getCommand("setup")!!.setExecutor(SetupWizardCommand(gameManager))
        getCommand("start")!!.setExecutor(StartCommand(gameManager))

        server.onlinePlayers.forEach {
            gameManager.scoreboard.addPlayer(it)
        }

        server.pluginManager.registerEvents(PlayerLoginEventListener(gameManager), this)
        server.pluginManager.registerEvents(PlayerItemInteractListener(gameManager), this)
        server.pluginManager.registerEvents(InventoryClickListener(gameManager), this)
        server.pluginManager.registerEvents(BlockUpdateListener(gameManager), this)
        server.pluginManager.registerEvents(PlayerDeathListener(gameManager), this)
        server.pluginManager.registerEvents(ItemListener, this)
        server.pluginManager.registerEvents(HungerListener(gameManager), this)
    }

    override fun onDisable() {
        gameManager = GameManager(instance)
        gameManager.scoreboard.destroy()
        gameManager.world.resetWorld()
    }

}