package me.mrfunny.plugins.paper.players

import me.mrfunny.plugins.paper.gamemanager.GameManager
import me.mrfunny.plugins.paper.util.InventoryApi
import me.mrfunny.plugins.paper.util.ItemBuilder
import me.mrfunny.plugins.paper.worlds.Island
import me.mrfunny.plugins.paper.worlds.generators.GeneratorType
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector

class PlayerManager(private val gameManager: GameManager) {

    fun setSpectatorMode(player: Player){
        player.teleport(gameManager.world.lobbyPosition)
        InventoryApi.clearInventoryExceptArmor(player)
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

        player.closeInventory()

        player.enderChest.clear()
        player.inventory.clear()
        giveTeamArmor(player, island)
    }

    private fun giveTeamArmor(player: Player, island: Island){
        player.inventory.chestplate = ItemBuilder(Material.LEATHER_CHESTPLATE)
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
        player.inventory.setItem(4, ItemBuilder(woolMaterial).setName("&a${if (PlayerData.PLAYERS[player.uniqueId]!!.isRussian()) "Выбрать команду" else "Select team"}").toItemStack())
        player.inventory.setItem(7, ItemBuilder(Material.BLAZE_POWDER).setName("&6${if (PlayerData.PLAYERS[player.uniqueId]!!.isRussian()) "Стартовая сила" else "Start Power"}").toItemStack())
        player.inventory.setItem(8, ItemBuilder(Material.BARRIER).setName("&c${if (PlayerData.PLAYERS[player.uniqueId]!!.isRussian()) "Выйти" else "Leave"}").toItemStack())
    }

    fun getIronCount(player: Player): Int{
        var count = 0
        for(item in player.inventory){
            if(item == null) continue
            if(item.type == Material.GHAST_TEAR){
                count += item.amount
            }
        }
        return count
    }

    fun getGoldCount(player: Player): Int{
        var count = 0
        for(item in player.inventory){
            if(item == null) continue
            if(item.type == GeneratorType.GOLD.getMaterial()){
                count += item.amount
            }
        }
        return count
    }

    fun getRubyCount(player: Player): Int{
        var count = 0
        for(item in player.inventory){
            if(item == null) continue
            if(item.type == Material.FERMENTED_SPIDER_EYE){
                count += item.amount
            }
        }
        return count
    }
}