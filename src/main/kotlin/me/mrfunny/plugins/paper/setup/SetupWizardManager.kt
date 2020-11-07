package me.mrfunny.plugins.paper.setup

import me.mrfunny.plugins.paper.worlds.TeamColor
import me.mrfunny.plugins.paper.util.ItemBuilder
import me.mrfunny.plugins.paper.worlds.GameWorld
import me.mrfunny.plugins.paper.worlds.Island
import org.bukkit.*
import org.bukkit.entity.Player

object SetupWizardManager {

    private var playerToIslandMap = hashMapOf<Player, Island>()
    private var playerToGameWorldMap = hashMapOf<Player, GameWorld>()
    private var playerToStartLocationMap = hashMapOf<Player, Location>()

    fun isInWizard(player: Player): Boolean {
        return playerToGameWorldMap.containsKey(player)
    }

    fun activateSetupWizard(player: Player, world: GameWorld){
        player.inventory.clear()
        player.gameMode = GameMode.CREATIVE
        player.teleport(Location(world.world, 0.0, 77.0, 0.0))

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
            ItemBuilder(Material.COAL).setName("&aSet lobby spawn").toItemStack()
        )
        player.inventory.addItem(
            ItemBuilder(Material.STICK).setName("&aChange island").toItemStack()
        )

        playerToGameWorldMap[player] = world
        playerToStartLocationMap[player] = player.location
    }

    fun teamSetupWizard(player: Player, teamColor: TeamColor){
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
            ItemBuilder(Material.IRON_INGOT).setName("&aSet generator location").toItemStack()
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
            ItemBuilder(teamColor.woolMaterial()).setName("&aChange island").toItemStack()
        )
        player.inventory.addItem(
            ItemBuilder(Material.RED_MUSHROOM).setName("&aSave island").toItemStack()

        )
        if(getWorld(player) != null){
            playerToIslandMap[player] = Island(getWorld(player)!!, teamColor)
        }
    }

    fun getWorld(player: Player): GameWorld?{
        return playerToGameWorldMap[player]
    }

    fun getIsland(player: Player): Island?{
        return playerToIslandMap[player]
    }

    fun removeFromWizard(player: Player) {
        if(playerToStartLocationMap[player] != null){
            player.teleport(playerToStartLocationMap[player]!!)
        } else {
            player.sendMessage("${ChatColor.RED}You are not in wizard")
        }

    }

}