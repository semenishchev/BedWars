package me.mrfunny.plugins.paper.gui

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack

object Cancelled : GUI {
    override val inventory: Inventory = Bukkit.createInventory(null, InventoryType.CHEST)
    override val name: String = "beb"

    override fun handleClick(player: Player, itemStack: ItemStack, view: InventoryView): GUI? {
        return null
    }
}