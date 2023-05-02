package me.mrfunny.bedwars.gui

import org.bukkit.entity.Player

object GUIManager {

    val playerToGUIMap: HashMap<Player, GUI?> = hashMapOf()

    fun getOpenGui(player: Player): GUI?{
        return playerToGUIMap[player]
    }

    fun setGUI(player: Player, gui: GUI?){
        if(gui == null){
            player.closeInventory()
            return
        }
        clear(player)
        playerToGUIMap[player] = gui
        player.openInventory(gui.inventory)
    }

    fun clear(player: Player){
        playerToGUIMap.remove(player)
    }
}