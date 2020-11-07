package me.mrfunny.plugins.paper.gui

import org.bukkit.entity.Player

object GUIManager {

    private val playerToGUIMap: HashMap<Player, GUI?> = hashMapOf()

    fun getOpenGui(player: Player): GUI?{
        return playerToGUIMap[player]
    }

    fun setGUI(player: Player, gui: GUI?){
        if(gui == null){
            player.closeInventory()
            return
        }
        playerToGUIMap[player] = gui
        player.closeInventory()
        player.openInventory(gui.inventory)
    }

    fun clear(player: Player){
        playerToGUIMap.remove(player)
    }

}