package me.mrfunny.plugins.paper

import com.google.common.io.ByteArrayDataOutput
import com.google.common.io.ByteStreams
import com.ruverq.rubynex.economics.Main
import me.mrfunny.api.UuidKick
import me.mrfunny.plugins.paper.commands.*
import me.mrfunny.plugins.paper.events.*
import me.mrfunny.plugins.paper.gamemanager.GameManager
import me.mrfunny.plugins.paper.gamemanager.GameState
import me.mrfunny.plugins.paper.util.Colorize
import me.mrfunny.plugins.paper.players.PlayerData
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin


class BedWars : JavaPlugin() {

    lateinit var gameManager: GameManager

    override fun onEnable() {

        server.messenger.registerOutgoingPluginChannel(this, "selector:bedwars")
        server.messenger.registerOutgoingPluginChannel(this, "coins:in")
        gameManager = GameManager(this, 2.1)

        server.pluginManager.getPlugin("ExampleJScoreboardPlugin")?.let {
            server.pluginManager.disablePlugin(
                it
            )
        }

        getCommand("setup")?.setExecutor(SetupWizardCommand(gameManager))
        getCommand("start")?.setExecutor(StartCommand(gameManager))
        getCommand("assign")?.setExecutor(AssignCommand(gameManager))
        getCommand("resetgame")?.setExecutor(ResetGameCommand())
        getCommand("forcekick")?.setExecutor(UuidKick())

        server.onlinePlayers.forEach {
            val addPlayerData = PlayerData(it.uniqueId, gameManager)
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

        server.messenger.registerOutgoingPluginChannel(this, "BungeeCord")


        ("\n${ChatColor.RED} ____           ___          __            \n" +
                "|  _ \\         | \\ \\        / /            \n" +
                "| |_) | ___  __| |\\ \\  /\\  / /_ _ _ __ ___ \n" +
                "|  _ < / _ \\/ _` | \\ \\/  \\/ / _` | '__/ __|\n" +
                "| |_) |  __/ (_| |  \\  /\\  / (_| | |  \\__ \\\n" +
                "|____/ \\___|\\__,_|   \\/  \\/ \\__,_|_|  |___/\n\n" +
                "${ChatColor.GREEN}---///||| Bedwars 1.0 By MisterFunny01 for Rubynex |||\\\\\\---\n" +
                "${ChatColor.GOLD}Code name: ${ChatColor.RED}ruby").log()
    }



    override fun onDisable() {
        gameManager.scoreboard.destroy()
        gameManager.world.resetWorld()
        PlayerData.disable()
        Bukkit.getScheduler().cancelTasks(this)
    }


    fun sendPluginMessage(channel: String, vararg values: Any){
        val out: ByteArrayDataOutput = ByteStreams.newDataOutput()
        values.forEach {
            when (it) {
                is String -> out.writeUTF(it)
                is Int -> out.writeInt(it)
                is Boolean -> out.writeBoolean(it)
                is Float -> out.writeFloat(it)
                is Double -> out.writeDouble(it)
            }

        }
        Bukkit.getServer().sendPluginMessage(this, channel, out.toByteArray())
    }

    fun sendPluginMessage(channel: String, player: Player, vararg values: Any){
        val out: ByteArrayDataOutput = ByteStreams.newDataOutput()
        values.forEach {
            when (it) {
                is String -> out.writeUTF(it)
                is Int -> out.writeInt(it)
                is Boolean -> out.writeBoolean(it)
                is Float -> out.writeFloat(it)
                is Double -> out.writeDouble(it)
            }

        }
        player.sendPluginMessage(this, channel, out.toByteArray())
    }

    companion object {
        fun String.colorize() = Colorize.c(this)
        fun String.log() = Bukkit.getConsoleSender().sendMessage(this.colorize())

//        val coins: Main = Bukkit.getPluginManager().getPlugin("Economics")!! as Main
    }

}