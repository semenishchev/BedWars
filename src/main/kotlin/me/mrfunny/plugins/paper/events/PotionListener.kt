package me.mrfunny.plugins.paper.events

import me.mrfunny.plugins.paper.gamemanager.GameManager
import net.minecraft.server.v1_16_R2.EnumItemSlot
import net.minecraft.server.v1_16_R2.PacketPlayOutEntityEquipment
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_16_R2.inventory.CraftItemStack
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityPotionEffectEvent
import org.bukkit.inventory.ItemStack
import java.util.*
import com.mojang.datafixers.util.Pair
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftPlayer

//import org.bukkit.event.entity.PotionEffectExpireEvent


class PotionListener(private val gameManager: GameManager): Listener{

    @EventHandler
    fun onConsume(event: EntityPotionEffectEvent){
        if(event.entity !is Player) return
        val player: Player = event.entity as Player

        if(event.cause == EntityPotionEffectEvent.Cause.POTION_DRINK){
            val equipmentList: MutableList<Pair<EnumItemSlot, net.minecraft.server.v1_16_R2.ItemStack>> = ArrayList<Pair<EnumItemSlot, net.minecraft.server.v1_16_R2.ItemStack>>()

            equipmentList.add(Pair(EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(ItemStack(Material.AIR))))
            equipmentList.add(Pair(EnumItemSlot.CHEST, CraftItemStack.asNMSCopy(ItemStack(Material.AIR))))
            equipmentList.add(Pair(EnumItemSlot.LEGS, CraftItemStack.asNMSCopy(ItemStack(Material.AIR))))
            equipmentList.add(Pair(EnumItemSlot.FEET, CraftItemStack.asNMSCopy(ItemStack(Material.AIR))))

            equipmentList.add(Pair(EnumItemSlot.MAINHAND, CraftItemStack.asNMSCopy(ItemStack(Material.AIR))))
            equipmentList.add(Pair(EnumItemSlot.OFFHAND, CraftItemStack.asNMSCopy(ItemStack(Material.AIR))))

            val entityEquipment = PacketPlayOutEntityEquipment(player.entityId, equipmentList)

            for(players in Bukkit.getOnlinePlayers()){
                if(players.uniqueId == player.uniqueId) continue
                (players as CraftPlayer).handle.playerConnection.sendPacket(entityEquipment)
            }

        } else {
            val equipmentList: MutableList<Pair<EnumItemSlot, net.minecraft.server.v1_16_R2.ItemStack>> = ArrayList<Pair<EnumItemSlot, net.minecraft.server.v1_16_R2.ItemStack>>()

            equipmentList.add(Pair(EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(player.inventory.helmet)))
            equipmentList.add(Pair(EnumItemSlot.CHEST, CraftItemStack.asNMSCopy(player.inventory.chestplate)))
            equipmentList.add(Pair(EnumItemSlot.LEGS, CraftItemStack.asNMSCopy(player.inventory.leggings)))
            equipmentList.add(Pair(EnumItemSlot.FEET, CraftItemStack.asNMSCopy(player.inventory.boots)))

            equipmentList.add(Pair(EnumItemSlot.MAINHAND, CraftItemStack.asNMSCopy(player.inventory.itemInMainHand)))
            equipmentList.add(Pair(EnumItemSlot.OFFHAND, CraftItemStack.asNMSCopy(player.inventory.itemInOffHand)))

            val entityEquipment = PacketPlayOutEntityEquipment(player.entityId, equipmentList)

            for(players in Bukkit.getOnlinePlayers()){
                if(players.uniqueId == player.uniqueId) continue
                (players as CraftPlayer).handle.playerConnection.sendPacket(entityEquipment)
            }
        }



    }
}