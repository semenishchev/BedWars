package me.mrfunny.plugins.paper.events

import me.mrfunny.plugins.paper.gui.GUI
import me.mrfunny.plugins.paper.gamemanager.GameManager
import me.mrfunny.plugins.paper.gamemanager.GameState
import me.mrfunny.plugins.paper.gui.ItemShopGUI
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.block.Chest
import org.bukkit.block.EnderChest
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryType

class InventoryClickListener(private val gameManager: GameManager) : Listener {

    @EventHandler
    fun onClick(event: InventoryClickEvent){
        if(gameManager.state == GameState.LOBBY && event.whoClicked.gameMode != GameMode.CREATIVE) event.isCancelled = true
        if(event.inventory.holder is Chest || event.inventory.holder is EnderChest) return
        if(event.currentItem == null) return

        val materialName: String = event.currentItem!!.type.name
        if(materialName.contains("HELMET")
            || materialName.contains("CHESTPLATE")
            || materialName.contains("LEGGINGS")
            || materialName.contains("BOOTS")){
            event.isCancelled = true
        }


        if(!event.currentItem!!.hasItemMeta()) return
        if(event.currentItem == null) return

        val player: Player = event.whoClicked as Player

        val gui: GUI? = gameManager.guiManager.getOpenGui(player)
        if(gui == null){
            event.view.close()
            event.whoClicked.closeInventory()
            return
        }

        if(event.clickedInventory == player.inventory) return

        event.isCancelled = true
        if(ChatColor.stripColor(event.currentItem?.itemMeta?.displayName)!!.toLowerCase() == "exit") {
            player.closeInventory()
            return
        }
        if(gui.handleClick(player, event.currentItem!!, event.view) == null){
            event.isCancelled = true
            return
        }
        val newGUI: GUI = gui.handleClick(player, event.currentItem!!, event.view)!!

        gameManager.guiManager.setGUI(player, newGUI)

    }

    @EventHandler
    fun onClose(event: InventoryCloseEvent){
        val player: Player = event.player as Player
        if(event.view.title == "Change island"){
            gameManager.guiManager.clear(player)
        }
    }
}