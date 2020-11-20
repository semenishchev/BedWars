package me.mrfunny.plugins.paper.gui

import me.mrfunny.plugins.paper.gamemanager.GameManager
import me.mrfunny.plugins.paper.util.Colorize
import me.mrfunny.plugins.paper.util.ItemBuilder
import me.mrfunny.plugins.paper.worlds.Island
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack

class ItemShopGUI(private val gameManager: GameManager, private val player: Player) : GUI {
    override val inventory: Inventory = Bukkit.createInventory(null, 54, "Shop")
    override val name: String = "Shop"

    init {
        val island: Island = gameManager.world.getIslandForPlayer(player)!!

        inventory.addItem(ItemBuilder(island.color.woolMaterial(), 16)
            .setLore("&74 iron")
            .toItemStack())

        inventory.addItem(ItemBuilder(Material.IRON_SWORD, 1)
            .setLore("&710 iron")
            .toItemStack())

        inventory.addItem(ItemBuilder(Material.DIAMOND_SWORD, 1)
            .setLore("&77 gold")
            .toItemStack())

        inventory.addItem(ItemBuilder(Material.NETHERITE_SWORD, 1)
            .setLore("&74 emeralds")
            .toItemStack())

        inventory.addItem(ItemBuilder(Material.ENDER_PEARL, 1)
            .setLore("&72 emeralds")
            .toItemStack())
    }

    override fun handleClick(player: Player, itemStack: ItemStack, view: InventoryView): GUI? {
        if(!isInventory(view)) return null
        if(!itemStack.hasItemMeta()) return null
        if(!itemStack.itemMeta.hasLore()) return null

        var priceString: String = itemStack.itemMeta.lore?.get(0) ?: return null
        priceString = ChatColor.stripColor(priceString)!!
        val price: Int = priceString.replace("[\\D]".toRegex(), "").toInt()

        val currencyMaterial: Material = when {
            priceString.contains("iron") -> {
                Material.IRON_INGOT
            }
            priceString.contains("gold") -> {
                Material.GOLD_INGOT
            }
            priceString.contains("emerald") -> {
                Material.EMERALD
            }
            else -> {
                null
            }
        } ?: return null

        for(inventoryItem in player.inventory){
            if(inventoryItem == null) continue
            if(inventoryItem.type == currencyMaterial){
                if(inventoryItem.amount >= price){
                    inventoryItem.setAmount(inventoryItem.getAmount() - price)
                    player.inventory.addItem(ItemBuilder(itemStack).setLore(arrayListOf()).toItemStack())
                } else {
                    player.sendMessage(Colorize.c("&cУ вас недостаточно денег для покупки"))
                }
            }
        }

        return null
    }

    override fun isInventory(view: InventoryView): Boolean {
        return view.title == name
    }
}