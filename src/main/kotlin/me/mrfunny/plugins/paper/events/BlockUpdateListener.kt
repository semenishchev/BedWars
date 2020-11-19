package me.mrfunny.plugins.paper.events

import me.mrfunny.plugins.paper.gamemanager.GameManager
import me.mrfunny.plugins.paper.gamemanager.GameState
import me.mrfunny.plugins.paper.util.Colorize
import me.mrfunny.plugins.paper.worlds.Island
import org.bukkit.*
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

            println("Broken bed for: ${island.color.formattedName()}")

            if(!island.isMember(player)){
                event.isDropItems = false
                island.players.forEach {
                    it.sendTitle(Colorize.c("&cBED BROKEN"), Colorize.c("&aYOU ARE NO LONGER RESPAWN"), 0, 40, 0)
                }

                location.world.spigot().strikeLightningEffect(location, false)

                Bukkit.getOnlinePlayers().forEach {
                    it.playSound(it.location, Sound.ENTITY_WITHER_DEATH, 1f, 1f)
                }

                Bukkit.broadcastMessage(Colorize.c("&fBED DESTROYED> ${island.color.getChatColor()}${island.color.formattedName()}&f bed has been destroyed by ${gameManager.world.getIslandForPlayer(player)!!.color.getChatColor()}${player.name}"))
            } else {
                event.isCancelled = true
            }
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