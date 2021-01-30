package me.mrfunny.plugins.paper.gameutils

import me.mrfunny.plugins.paper.players.PlayerData
import org.bukkit.inventory.ItemStack
import java.util.function.Consumer

class StartingPower(val id: String, val item: ItemStack, private val onEnable: Consumer<PlayerData>, private val onDisable: Consumer<PlayerData>) {

    val players = arrayListOf<PlayerData>()

    fun enable(){
        players.forEach {
            onEnable.accept(it)
        }
    }

    fun disable(){
        players.forEach {
            onDisable.accept(it)
        }
    }
}