package me.mrfunny.plugins.paper.events

import me.mrfunny.plugins.paper.gamemanager.GameManager
import me.mrfunny.plugins.paper.gamemanager.GameState
import me.mrfunny.plugins.paper.worlds.Island
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent

class BlockUpdateListener(private val gameManager: GameManager) : Listener {

    @EventHandler
    fun onBreak(event: BlockBreakEvent){
        if(event.player.gameMode == GameMode.CREATIVE) return
        if(gameManager.state != GameState.ACTIVE && gameManager.state != GameState.WON) {
            event.isCancelled = true
            return
        }

        val player: Player = event.player
        val type: Material = event.block.type

        if(type.toString().contains("BED")){
            val location: Location = event.block.location

            val island: Island = gameManager.world.getIslandForBedLocation(location)!!

            if(!island.isMember(player)){
                event.block.type = Material.AIR
            }

            event.isCancelled = true
            return
        }
    }

    @EventHandler
    fun onPlace(event: BlockPlaceEvent){
        if(gameManager.state != GameState.ACTIVE && gameManager.state != GameState.WON) return
        if(event.player.gameMode == GameMode.CREATIVE) return

        if(event.block.x > 110){
            event.isCancelled = true
            event.player.sendMessage("${ChatColor.RED}You cannot place blocks on Y: 110 and more")
            return
        }

        for (island: Island in gameManager.world.islands) {
            if(island.isBlockWithinProtectedZone(event.block)){
                event.isCancelled = true
            }
        }

    }
}