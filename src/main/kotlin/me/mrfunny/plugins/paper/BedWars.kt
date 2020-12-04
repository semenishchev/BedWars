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
import org.bukkit.ChatColor
import org.bukkit.plugin.java.JavaPlugin


class BedWars : JavaPlugin() {

    lateinit var gameManager: GameManager
    lateinit var instance: BedWars

    override fun onEnable() {
        Bukkit.getConsoleSender().sendMessage("\n${ChatColor.RED} ____           ___          __            \n" +
                "|  _ \\         | \\ \\        / /            \n" +
                "| |_) | ___  __| |\\ \\  /\\  / /_ _ _ __ ___ \n" +
                "|  _ < / _ \\/ _` | \\ \\/  \\/ / _` | '__/ __|\n" +
                "| |_) |  __/ (_| |  \\  /\\  / (_| | |  \\__ \\\n" +
                "|____/ \\___|\\__,_|   \\/  \\/ \\__,_|_|  |___/\n\n" +
                "${ChatColor.GREEN}---///||| Bedwars 1.0 By MisterFunny01 for Rubynex |||\\\\\\---\n" +
                "${ChatColor.GOLD}Code name: ${ChatColor.RED}ruby")

        instance = this
        gameManager = GameManager(this)

        server.pluginManager.getPlugin("ExampleJScoreboardPlugin")?.let {
            server.pluginManager.disablePlugin(
                it
            )
        }

        getCommand("setup")?.setExecutor(SetupWizardCommand(gameManager))
        getCommand("start")?.setExecutor(StartCommand(gameManager))
        getCommand("forcestart")?.setExecutor(ForcestartCommand(gameManager))

        server.onlinePlayers.forEach {
            val addPlayerData = PlayerData(it.uniqueId)
            addPlayerData.health = it.health
            addPlayerData.lastCombat = 0L
            PlayerData.PLAYERS[it.uniqueId] = addPlayerData
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
        server.pluginManager.registerEvents(PotionListener(gameManager), this)
        server.pluginManager.registerEvents(ItemListener, this)
        server.pluginManager.registerEvents(MobSpawnListener, this)
}



override fun onDisable() {
    gameManager.scoreboard.destroy()
    gameManager.world.resetWorld()
    PlayerData.disable()
}

    companion object {
        fun String.colorize() = Colorize.c(this)
    }

}