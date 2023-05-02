package me.mrfunny.bedwars.setup

import com.google.common.io.ByteArrayDataOutput
import com.google.common.io.ByteStreams
import me.mrfunny.bedwars.game.GameManager
import me.mrfunny.bedwars.util.ItemBuilder
import me.mrfunny.bedwars.worlds.GameWorld
import me.mrfunny.bedwars.worlds.islands.Island
import me.mrfunny.bedwars.worlds.islands.IslandColor
import org.bukkit.*
import org.bukkit.entity.Player

object SetupWizardManager {

    private val playerToIslandMap = hashMapOf<Player, Island>()
    private val playerToGameWorldMap = hashMapOf<Player, GameWorld>()
    private val playerToStartLocationMap = hashMapOf<Player, Location>()
    private val playersInWizard = arrayListOf<Player>()

    fun isInWizard(player: Player): Boolean {
        return playerToGameWorldMap.containsKey(player)
    }

    fun activateSetupWizard(player: Player, world: GameWorld){
        player.inventory.clear()
        player.gameMode = GameMode.CREATIVE
        player.teleport(Location(world.world, 0.0, 77.0, 0.0))

        playersInWizard.add(player)

        worldSetupWizard(player, world)
    }

    fun worldSetupWizard(player: Player, world: GameWorld){
        player.inventory.clear()
        player.inventory.addItem(
            ItemBuilder(Material.DIAMOND).setName("&bSet diamond generator").toItemStack()
        )
        player.inventory.addItem(
            ItemBuilder(Material.EMERALD).setName("&aSet emerald generator").toItemStack()
        )
        player.inventory.addItem(
            ItemBuilder(Material.GOLD_BLOCK).setName("&aSet gold generator").toItemStack()
        )
        player.inventory.addItem(
            ItemBuilder(Material.COAL).setName("&aSet lobby spawn").toItemStack()
        )
        player.inventory.addItem(
            ItemBuilder(Material.STICK).setName("&aChange island").toItemStack()
        )
        player.inventory.addItem(
            ItemBuilder(Material.RED_BANNER).setName("&aSet map centre").toItemStack()
        )

        playerToGameWorldMap[player] = world
        playerToStartLocationMap[player] = player.location
    }

    fun teamSetupWizard(player: Player, islandColor: IslandColor){
        player.inventory.clear()

        player.inventory.addItem(
            ItemBuilder(Material.STICK).setName("&aFirst Corner stick").toItemStack()
        )
        player.inventory.addItem(
            ItemBuilder(Material.BLAZE_ROD).setName("&aSecond Corner stick").toItemStack()
        )
        player.inventory.addItem(
            ItemBuilder(Material.EGG).setName("&aSet shop location").toItemStack()
        )
        player.inventory.addItem(
            ItemBuilder(Material.IRON_INGOT).setName("&aSet all generators location").toItemStack()
        )
        player.inventory.addItem(
            ItemBuilder(Material.DIAMOND_SWORD).setName("&aSet Team Upgrade location").toItemStack()
        )
        player.inventory.addItem(
            ItemBuilder(Material.BOWL).setName("&aSet spawn location").toItemStack()
        )
        player.inventory.addItem(
            ItemBuilder(Material.MAGMA_CREAM).setName("&aSet bed location").toItemStack()
        )
        player.inventory.addItem(
            ItemBuilder(islandColor.woolMaterial()).setName("&aChange island").toItemStack()
        )
        player.inventory.addItem(
            ItemBuilder(Material.RED_MUSHROOM).setName("&aSave island").toItemStack()

        )
        if(getWorld(player) != null){
            playerToIslandMap[player] = Island(getWorld(player)!!, islandColor)
        }
    }

    fun getWorld(player: Player): GameWorld?{
        return playerToGameWorldMap[player]
    }

    fun getIsland(player: Player): Island?{
        return playerToIslandMap[player]
    }

    fun removeFromWizard(player: Player, gameManager: GameManager) {
        if(playerToStartLocationMap[player] != null){
            playersInWizard.forEach {
                val out: ByteArrayDataOutput = ByteStreams.newDataOutput()
                out.writeUTF("Connect")
                out.writeUTF("hub-1")

                it.sendPluginMessage(gameManager.plugin, "BungeeCord", out.toByteArray())
                it.kickPlayer("Setup loading")
                it.inventory.clear()
            }
            gameManager.world.resetWorld(unload = true, save = false)
            Bukkit.unloadWorld(playerToGameWorldMap[player]!!.world, true)
            Bukkit.shutdown()
        } else {
            player.sendMessage("${ChatColor.RED}You are not in wizard")
        }

    }

}