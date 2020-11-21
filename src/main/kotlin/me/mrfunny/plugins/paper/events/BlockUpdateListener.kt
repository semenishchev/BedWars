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
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.metadata.MetadataValue

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
                event.isDropItems = false
                island.players.forEach {
                    it.sendTitle(Colorize.c("&cВАША КРОВАТЬ СЛОМАНА"), Colorize.c("&aВЫ БОЛЬШЕ НЕ ВОЗРОДИТЕСЬ"), 0, 40, 0)
                }

                location.world.spigot().strikeLightningEffect(location, false)

                Bukkit.getOnlinePlayers().forEach {
                    it.playSound(it.location, Sound.ENTITY_WITHER_DEATH, 1f, 1f)
                }

                Bukkit.broadcastMessage(Colorize.c("&fКровать сломана> ${island.color.getChatColor()}${island.color.russianName()}ая&f кровать была разрушена ${gameManager.world.getIslandForPlayer(player)!!.color.getChatColor()}${player.name}"))
            } else {
                player.sendMessage("${ChatColor.RED}Вы не можете сломать свою кровать...")
                event.isCancelled = true
            }
            return
        }

        for (island: Island in gameManager.world.islands) {
            if(island.isBlockWithinProtectedZone(event.block)){
                event.isCancelled = true
                return
            }
        }

        if(!event.block.hasMetadata("placed")){
            event.isCancelled = true
            player.sendMessage("${ChatColor.RED}Вы можете ломать только не блоки, которые поставили другие игроки")
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

        if(event.block.type.name.contains("BED")){
            event.isCancelled = true
            event.player.sendMessage("Ууу ясно читор")
            return
        }

        for (island: Island in gameManager.world.islands) {
            if(island.isBlockWithinProtectedZone(event.block)){
                event.player.sendMessage("Вы не можете ставить сдесь блоки")
                event.isCancelled = true
                return
            }
        }

        event.block.setMetadata("placed", FixedMetadataValue(gameManager.plugin, event.player.name))

    }
}