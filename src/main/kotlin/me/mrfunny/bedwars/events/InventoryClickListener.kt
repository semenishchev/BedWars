package me.mrfunny.bedwars.events

import me.mrfunny.bedwars.game.GameManager
import me.mrfunny.bedwars.game.GameState
import me.mrfunny.bedwars.gui.Cancelled
import me.mrfunny.bedwars.gui.GUI
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
        if(gameManager.state != GameState.ACTIVE) event.isCancelled = true
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


        val player: Player = event.whoClicked as Player

        val gui: GUI = gameManager.guiManager.getOpenGui(player) ?: return

        if(event.clickedInventory == player.inventory) {
            return
        }
        val newGUI: GUI? = gui.handleClick(player, event.currentItem!!, event.view)
        event.isCancelled = true
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