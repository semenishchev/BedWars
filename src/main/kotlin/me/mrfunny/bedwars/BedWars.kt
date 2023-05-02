package me.mrfunny.bedwars

import com.google.common.io.ByteArrayDataOutput
import com.google.common.io.ByteStreams
import me.mrfunny.api.UuidKick
import me.mrfunny.bedwars.commands.AssignCommand
import me.mrfunny.bedwars.commands.ResetGameCommand
import me.mrfunny.bedwars.commands.SetupWizardCommand
import me.mrfunny.bedwars.commands.StartCommand
import me.mrfunny.bedwars.events.*
import me.mrfunny.bedwars.game.GameManager
import me.mrfunny.bedwars.game.GameState
import me.mrfunny.bedwars.players.PlayerData
import me.mrfunny.bedwars.util.Colorize
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.block.Block
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Entity
import org.bukkit.entity.Monster
import org.bukkit.inventory.ItemStack
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.plugin.messaging.PluginMessageRecipient


class BedWars : JavaPlugin() {

    lateinit var gameManager: GameManager


    override fun onEnable() {
        me.mrfunny.bedwars.BedWars.Companion.instance = this
        server.messenger.registerOutgoingPluginChannel(this, "selector:bedwars")
        server.messenger.registerOutgoingPluginChannel(this, "coins:in")
        gameManager = GameManager(this, 3.0)
        PlayerData.init(gameManager)


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

        for (player in server.onlinePlayers) {
            PlayerData.new(player, gameManager)
            gameManager.scoreboard.addPlayer(player)
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
        server.pluginManager.registerEvents(ItemListener(gameManager), this)
        server.pluginManager.registerEvents(MobSpawnListener, this)

        server.messenger.registerOutgoingPluginChannel(this, "BungeeCord")


        ("\n${ChatColor.RED} ____           ___          __            \n" +
                "|  _ \\         | \\ \\        / /            \n" +
                "| |_) | ___  __| |\\ \\  /\\  / /_ _ _ __ ___ \n" +
                "|  _ < / _ \\/ _` | \\ \\/  \\/ / _` | '__/ __|\n" +
                "| |_) |  __/ (_| |  \\  /\\  / (_| | |  \\__ \\\n" +
                "|____/ \\___|\\__,_|   \\/  \\/ \\__,_|_|  |___/\n\n").log()
    }



    override fun onDisable() {
        gameManager.scoreboard.destroy()
        gameManager.world.resetWorld()
        PlayerData.disable()
        Bukkit.getScheduler().cancelTasks(this)
    }


    fun sendPluginMessage(channel: String, vararg values: Any){
        sendPluginMessage(channel, Bukkit.getServer(), values)
    }

    private fun sendPluginMessage(channel: String, player: PluginMessageRecipient, vararg values: Any){
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
        lateinit var instance: me.mrfunny.bedwars.BedWars
        fun String.colorize() = Colorize.c(this)
        fun String.log() = Bukkit.getConsoleSender().sendMessage(this.colorize())
        fun Entity.removeIfBad() {
            if(isProtected()) return
            if(this is Monster || this is ArmorStand){
                this.remove()
            }
        }

        fun Entity.isProtected(): Boolean {
            return this.hasMetadata("protected")
        }

        fun Entity.protect() {
            this.setMetadata("protected", FixedMetadataValue(me.mrfunny.bedwars.BedWars.Companion.instance, true))
        }

        fun ChatColor.toNamed(): NamedTextColor {
            return when(this) {
                ChatColor.BLACK -> NamedTextColor.BLACK
                ChatColor.DARK_BLUE -> NamedTextColor.DARK_BLUE
                ChatColor.DARK_GREEN -> NamedTextColor.DARK_GREEN
                ChatColor.DARK_AQUA -> NamedTextColor.DARK_AQUA
                ChatColor.DARK_RED -> NamedTextColor.DARK_RED
                ChatColor.DARK_PURPLE -> NamedTextColor.DARK_PURPLE
                ChatColor.GOLD -> NamedTextColor.GOLD
                ChatColor.GRAY -> NamedTextColor.GRAY
                ChatColor.DARK_GRAY -> NamedTextColor.DARK_GRAY
                ChatColor.BLUE -> NamedTextColor.BLUE
                ChatColor.GREEN -> NamedTextColor.GREEN
                ChatColor.AQUA -> NamedTextColor.AQUA
                ChatColor.RED -> NamedTextColor.RED
                ChatColor.LIGHT_PURPLE -> NamedTextColor.LIGHT_PURPLE
                ChatColor.YELLOW -> NamedTextColor.YELLOW
                else -> NamedTextColor.WHITE
            }
        }
        fun Block.canEnvironmentBreak(): Boolean {
            return !this.type.name.contains("BED") && !this.hasMetadata("placed")
        }
        private val interactionKey = NamespacedKey.fromString("bedwars:interactionitem")!!
        fun ItemStack.addInteractionKey(key: String): ItemStack {
            itemMeta.also {
                it.persistentDataContainer.set(me.mrfunny.bedwars.BedWars.Companion.interactionKey, PersistentDataType.STRING, key)
            }
            return this
        }

        fun ItemStack.getInteractionKey(): String? {
            return itemMeta.persistentDataContainer.get(me.mrfunny.bedwars.BedWars.Companion.interactionKey, PersistentDataType.STRING)
        }

        fun Material.isBetterThan(other: Material?): Boolean {
            if(other == null || other == Material.AIR) return false
            if(this == other) return false
            return when(this) {
                Material.NETHERITE_BOOTS -> true
                Material.DIAMOND_BOOTS -> other != Material.NETHERITE_BOOTS
                Material.IRON_BOOTS -> other != Material.NETHERITE_BOOTS && other != Material.DIAMOND_BOOTS
                else -> false
            }
        }
    }

}