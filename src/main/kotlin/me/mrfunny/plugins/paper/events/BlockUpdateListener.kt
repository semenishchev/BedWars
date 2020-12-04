package me.mrfunny.plugins.paper.events

import me.mrfunny.plugins.paper.gamemanager.GameManager
import me.mrfunny.plugins.paper.gamemanager.GameState
import me.mrfunny.plugins.paper.messages.MessageUser
import me.mrfunny.plugins.paper.messages.MessagesManager.Companion.message
import me.mrfunny.plugins.paper.util.Colorize
import me.mrfunny.plugins.paper.worlds.Island
import org.bukkit.*
import org.bukkit.block.Block
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.entity.TNTPrimed
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockExplodeEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityExplodeEvent
import org.bukkit.metadata.FixedMetadataValue

class BlockUpdateListener(val gameManager: GameManager) : Listener {

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
                    it.sendTitle(message("bed-destruction-title", gameManager), message("bed-destruction-subtitle", gameManager), 0, 40, 0)
                }

                location.world.spigot().strikeLightningEffect(location, false)

                Bukkit.getOnlinePlayers().forEach {
                    it.playSound(it.location, Sound.ENTITY_WITHER_DEATH, 1f, 1f)
                }

                Bukkit.broadcastMessage(message("bed-destruction-all", gameManager).replace("{island}", "${island.color.getChatColor()}${island.color.formattedName()}")
                    .replace("{player-island}", gameManager.world.getIslandForPlayer(player)?.color?.getChatColor().toString()).replace("{player}", player.name))
            } else {
                player.sendMessage("${ChatColor.RED}You cannot break your bed...")
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
            player.sendMessage("${ChatColor.RED}You can break placed by players blocks")
            return
        }
    }

    @EventHandler
    fun onExplode(event: EntityExplodeEvent){
        val blockIterator: MutableIterator<Block> = event.blockList().iterator()

        while(blockIterator.hasNext()){
            try{
                val block: Block = blockIterator.next()
                if(!block.hasMetadata("placed") || block.type.name.contains("GLASS") || block.type.name.contains("BED")){
                    blockIterator.remove()
                }
            } catch (ex: Exception){
                blockIterator.remove()
            }
        }
    }

    @EventHandler
    fun onBlockExplode(event: BlockExplodeEvent){
        val blockIterator: MutableIterator<Block> = event.blockList().iterator()

        while(blockIterator.hasNext()){
            try{
                val block: Block = blockIterator.next()
                if(!block.hasMetadata("placed") || block.type.name.contains("GLASS") || block.type.name.contains("BED")){
                    blockIterator.remove()
                }
            } catch (ex: Exception){
                blockIterator.remove()
            }
        }
    }

    @EventHandler
    fun onPlace(event: BlockPlaceEvent){
        if(event.player.gameMode == GameMode.CREATIVE) return
        if(gameManager.state != GameState.ACTIVE && gameManager.state != GameState.WON) {
            event.isCancelled = true
            return
        }

        if(event.block.y > 110){
            event.isCancelled = true
            event.player.sendMessage("${ChatColor.RED}You cannot place blocks on Y: 110 and more")
            return
        }

        for (island: Island in gameManager.world.islands) {
            if(island.isBlockWithinProtectedZone(event.block)){
                event.player.sendMessage("You cannot place blocks here")
                event.isCancelled = true
                return
            }
        }

        if(event.blockPlaced.type == Material.TNT) {
            event.isCancelled = true
            val location = event.blockPlaced.location
            location.x += 0.5
            location.y += 0.5
            location.z += 0.5
            (location.world!!.spawnEntity(location, EntityType.PRIMED_TNT) as TNTPrimed).fuseTicks = 40
            val newItem = event.itemInHand
            newItem.amount = newItem.amount - 1
            event.player.inventory.remove(event.itemInHand)
            event.player.inventory.addItem(newItem)
            event.player.updateInventory()
            return
        }

        event.block.setMetadata("placed", FixedMetadataValue(gameManager.plugin, "block"))

    }
}