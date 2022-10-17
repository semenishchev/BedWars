package me.mrfunny.plugins.paper.players

import com.mojang.datafixers.util.Pair
import me.mrfunny.plugins.paper.gamemanager.GameManager
import me.mrfunny.plugins.paper.util.InventoryApi
import me.mrfunny.plugins.paper.util.ItemBuilder
import me.mrfunny.plugins.paper.worlds.Island
import me.mrfunny.plugins.paper.worlds.generators.GeneratorType
import net.minecraft.network.protocol.game.PacketPlayOutEntityEquipment
import net.minecraft.world.entity.EnumItemSlot
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector

class PlayerManager(private val gameManager: GameManager) {
    fun setSpectatorMode(player: Player){
        InventoryApi.clearInventoryExceptArmor(player)
        player.gameMode = GameMode.SPECTATOR
        player.teleport(gameManager.world.lobbyPosition)
        gameManager.endGameIfNeeded()
    }

    fun setPlaying(player: Player) {
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

    fun playerTeamSelector(player: Player) {
        val island: Island? = gameManager.world.getIslandForPlayer(player)

        var woolMaterial: Material = Material.WHITE_WOOL
        if(island != null){
            woolMaterial = island.color.woolMaterial()
        }

        player.inventory.clear()
        player.inventory.setItem(4, ItemBuilder(woolMaterial).setName("&a${if (PlayerData.PLAYERS[player.uniqueId]!!.isRussian()) "Выбрать команду" else "Select team"}").toItemStack())
        player.inventory.setItem(7, ItemBuilder(Material.BLAZE_POWDER).setName("&6${if (PlayerData.PLAYERS[player.uniqueId]!!.isRussian()) "Стартовая сила" else "Start Power"}").toItemStack())
        player.inventory.setItem(8, ItemBuilder(Material.RED_BED).setName("&c${if (PlayerData.PLAYERS[player.uniqueId]!!.isRussian()) "Выйти из игры &8[ПКМ]" else "Leave"}").toItemStack())
    }

    fun getIronCount(player: Player): Int {
        var count = 0
        for(item in player.inventory){
            if(item == null) continue
            if(item.type == Material.GHAST_TEAR){
                count += item.amount
            }
        }
        return count
    }

    fun getGoldCount(player: Player): Int {
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

    fun hideItems(player: Player){
        val equipmentList: ArrayList<Pair<EnumItemSlot, net.minecraft.world.item.ItemStack>> = arrayListOf()

        equipmentList.add(Pair(EnumItemSlot.f, net.minecraft.world.item.ItemStack.b))
        equipmentList.add(Pair(EnumItemSlot.e, net.minecraft.world.item.ItemStack.b))
        equipmentList.add(Pair(EnumItemSlot.d, net.minecraft.world.item.ItemStack.b))
        equipmentList.add(Pair(EnumItemSlot.c, net.minecraft.world.item.ItemStack.b))

        equipmentList.add(Pair(EnumItemSlot.a, net.minecraft.world.item.ItemStack.b))
        equipmentList.add(Pair(EnumItemSlot.b, net.minecraft.world.item.ItemStack.b))

        val entityEquipment = PacketPlayOutEntityEquipment(player.entityId, equipmentList)

        for(players in Bukkit.getOnlinePlayers()){
            if(players.uniqueId == player.uniqueId) continue
            if(gameManager.world.getIslandForPlayer(player)!!.isMember(players)) continue
            (players as CraftPlayer).handle.b.sendPacket(entityEquipment)
        }
    }

    fun showItems(player: Player){
        val equipmentList: ArrayList<Pair<EnumItemSlot, net.minecraft.world.item.ItemStack>> = arrayListOf()

        equipmentList.add(Pair(EnumItemSlot.f, CraftItemStack.asNMSCopy(player.inventory.helmet)))
        equipmentList.add(Pair(EnumItemSlot.e, CraftItemStack.asNMSCopy(player.inventory.chestplate)))
        equipmentList.add(Pair(EnumItemSlot.d, CraftItemStack.asNMSCopy(player.inventory.leggings)))
        equipmentList.add(Pair(EnumItemSlot.c, CraftItemStack.asNMSCopy(player.inventory.boots)))

        equipmentList.add(Pair(EnumItemSlot.a, CraftItemStack.asNMSCopy(player.inventory.itemInMainHand)))
        equipmentList.add(Pair(EnumItemSlot.b, CraftItemStack.asNMSCopy(player.inventory.itemInOffHand)))

        val entityEquipment = PacketPlayOutEntityEquipment(player.entityId, equipmentList)

        for(players in Bukkit.getOnlinePlayers()){
            if(players.uniqueId == player.uniqueId) continue
            (players as CraftPlayer).handle.b.sendPacket(entityEquipment)
        }
    }
}