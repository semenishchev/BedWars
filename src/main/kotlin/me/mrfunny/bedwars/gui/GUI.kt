package me.mrfunny.bedwars.gui

import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack

interface GUI {
    val inventory: Inventory
    val name: String
    fun handleClick(player: Player, itemStack: ItemStack, view: InventoryView): GUI?
    fun isInventory(view: InventoryView): Boolean {
        return view.title == name
    }
}