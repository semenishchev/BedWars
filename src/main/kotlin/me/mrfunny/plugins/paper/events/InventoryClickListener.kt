package me.mrfunny.plugins.paper.events

import me.mrfunny.plugins.paper.gui.GUI
import me.mrfunny.plugins.paper.gamemanager.GameManager
import me.mrfunny.plugins.paper.gamemanager.GameState
import me.mrfunny.plugins.paper.gui.Cancelled
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.*
import org.bukkit.inventory.PlayerInventory

class InventoryClickListener(private val gameManager: GameManager): Listener {

    @EventHandler
    fun onClick(event: InventoryClickEvent){
        if(event.currentItem == null) {
            event.isCancelled = true
            return
        }
        val materialName: String = event.currentItem!!.type.name
        if(materialName.contains("HELMET")
            || materialName.contains("CHESTPLATE")
            || materialName.contains("LEGGINGS")
            || materialName.contains("BOOTS")){
            event.isCancelled = true
        }
        if(event.clickedInventory == null) return
        if(event.clickedInventory is PlayerInventory) return
        if(event.clickedInventory!!.holder != null) return
//        if(event.currentItem!!.type == Material.AIR) event.isCancelled = true

        event.isCancelled = true

        val player: Player = event.whoClicked as Player

        val gui: GUI? = gameManager.guiManager.getOpenGui(player)
        if(gui is Cancelled){
            event.isCancelled = true
            return
        }
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
        val newGUI: GUI? = gui.handleClick(player, event.currentItem!!, event.view)
        if(newGUI is Cancelled){
            event.isCancelled = true
            return
        }

        gameManager.guiManager.setGUI(player, newGUI)

    }

    @EventHandler
    fun onClose(event: InventoryCloseEvent){
        val player: Player = event.player as Player
        if(event.view.title.contains("island") || event.view.title.contains("shop") || event.view.title.contains("upgrades")){
            gameManager.guiManager.clear(player)
        }
    }

    @EventHandler
    fun onDrag(event: InventoryDragEvent){
        if(gameManager.guiManager.getOpenGui(event.whoClicked as Player) == null) return else event.isCancelled = true
    }

    @Deprecated("Now it does nothing")
    fun create(){}

}