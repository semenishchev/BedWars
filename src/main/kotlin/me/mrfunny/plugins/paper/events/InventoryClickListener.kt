package me.mrfunny.plugins.paper.events

import me.mrfunny.plugins.paper.gui.GUI
import me.mrfunny.plugins.paper.gamemanager.GameManager
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent

class InventoryClickListener(private val gameManager: GameManager) : Listener {

    @EventHandler
    fun onClick(event: InventoryClickEvent){
        if(event.currentItem == null) return
        if(!event.currentItem!!.hasItemMeta()) return
        if(event.currentItem == null) return

        val player: Player = event.whoClicked as Player

        val gui: GUI = gameManager.guiManager.getOpenGui(player) ?: return
        if(event.view.title != "Select island") return
        event.isCancelled = true
        if(ChatColor.stripColor(event.currentItem?.itemMeta?.displayName)!!.toLowerCase() == "exit") {
            player.closeInventory()
            return
        }
        val newGUI: GUI = gui.handleClick(player, event.currentItem!!, event.view) ?: return

        event.view.close()

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