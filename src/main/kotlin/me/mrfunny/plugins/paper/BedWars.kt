package me.mrfunny.plugins.paper

import me.mrfunny.plugins.paper.commands.SetupWizardCommand
import me.mrfunny.plugins.paper.commands.StartCommand
import me.mrfunny.plugins.paper.events.BlockUpdateListener
import me.mrfunny.plugins.paper.events.InventoryClickListener
import me.mrfunny.plugins.paper.events.PlayerItemInteractListener
import me.mrfunny.plugins.paper.events.PlayerLoginEventListener
import me.mrfunny.plugins.paper.manager.GameManager
import org.bukkit.Bukkit

import org.bukkit.plugin.java.JavaPlugin


class BedWars : JavaPlugin() {
    lateinit var gameManager: GameManager
    override fun onEnable() {
        gameManager = GameManager(this)

        server.pluginManager.getPlugin("ExampleJScoreboardPlugin")?.let {
            server.pluginManager.disablePlugin(
                it
            )
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
    }

    override fun onDisable() {
        gameManager.scoreboard.destroy()
    }

}