package me.mrfunny.plugins.paper.gui

import me.mrfunny.plugins.paper.gamemanager.GameManager
import me.mrfunny.plugins.paper.worlds.IslandColor
import me.mrfunny.plugins.paper.util.ItemBuilder
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack

class SetupWizardIslandSelectorGUI(var gameManager: GameManager) : GUI {

    override val name: String = "Select island"

    companion object Name {
        val name: String = "Select island"
    }

    override val inventory: Inventory = Bukkit.createInventory(null, 27, name)

    init {
        IslandColor.values().forEach {
            inventory.addItem(
                ItemBuilder(it.woolMaterial()).setName("${it.getChatColor()}${it.formattedName()}").toItemStack()
            )
        }

        inventory.addItem(ItemBuilder(Material.BARRIER).setName("&cExit").toItemStack())
    }

    override fun handleClick(player: Player, itemStack: ItemStack, view: InventoryView): GUI? {
        lateinit var clickedColor: IslandColor

        if(!gameManager.setupWizardManager.isInWizard(player)) return null

        val itemName: String = ChatColor.stripColor(itemStack.itemMeta.displayName)!!.substring(1)

        for(color: IslandColor in IslandColor.values()){
            if(itemName.equals(color.formattedName(), true)){
                clickedColor = color
                break
            }
        }

        gameManager.setupWizardManager.teamSetupWizard(player, clickedColor)

        return null
    }

    override fun isInventory(view: InventoryView): Boolean {
        return view.title == name
    }
}