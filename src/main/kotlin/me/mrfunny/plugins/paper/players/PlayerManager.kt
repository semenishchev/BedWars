package me.mrfunny.plugins.paper.players

import me.mrfunny.plugins.paper.gamemanager.GameManager
import me.mrfunny.plugins.paper.util.ItemBuilder
import me.mrfunny.plugins.paper.worlds.Island
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.entity.Player

class PlayerManager(private val gameManager: GameManager) {

    fun setSpectatorMode(player: Player){
        player.teleport(gameManager.world.lobbyPosition)
        player.inventory.clear()
        player.gameMode = GameMode.SPECTATOR

        gameManager.endGameIfNeeded()
    }

    fun setPlaying(player: Player){
        val island: Island? = gameManager.world.getIslandForPlayer(player)

        if(island == null || !island.isBedPlaced()){
            setSpectatorMode(player)
            return
        }

        player.gameMode = GameMode.SURVIVAL
        player.teleport(island.spawnLocation!!)

        player.enderChest.clear()
        player.inventory.clear()

        giveTeamArmor(player, island)
    }

    fun giveTeamArmor(player: Player, island: Island){
        player.inventory.helmet = ItemBuilder(Material.LEATHER_HELMET)
            .setLeatherArmorColor(island.color.getColor())
            .setUnbreakable(true)
            .toItemStack()

        player.inventory.chestplate = ItemBuilder(Material.LEATHER_CHESTPLATE)
            .setLeatherArmorColor(island.color.getColor())
            .setUnbreakable(true)
            .toItemStack()

        // todo: проверить на наличие улучшений брони

        player.inventory.leggings = ItemBuilder(Material.LEATHER_LEGGINGS)
            .setLeatherArmorColor(island.color.getColor())
            .setUnbreakable(true)
            .toItemStack()

        player.inventory.boots = ItemBuilder(Material.LEATHER_BOOTS)
            .setLeatherArmorColor(island.color.getColor())
            .setUnbreakable(true)
            .toItemStack()
    }

    fun giveAllTeamSelector(){
        Bukkit.getOnlinePlayers().forEach {
            playerTeamSelector(it)
        }
    }

    fun playerTeamSelector(player: Player){
        val island: Island? = gameManager.world.getIslandForPlayer(player)

        var woolMaterial: Material = Material.WHITE_WOOL
        if(island != null){
            woolMaterial = island.color.woolMaterial()
        }

        player.inventory.clear()
        player.inventory.addItem(ItemBuilder(woolMaterial).setName("Select team").toItemStack())
    }
}