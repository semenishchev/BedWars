package me.mrfunny.plugins.paper.events

import me.mrfunny.plugins.paper.gamemanager.GameManager
import me.mrfunny.plugins.paper.gamemanager.GameState
import me.mrfunny.plugins.paper.messages.MessagesManager.Companion.message
import me.mrfunny.plugins.paper.players.PlayerData
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
import org.bukkit.util.Vector

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
            event.isDropItems = false
            val location: Location = event.block.location

            val island: Island = gameManager.world.getIslandForBedLocation(location)!!

            if(!island.isMember(player)){
                gameManager.updateScoreboard(true)
                island.players.forEach {
                    it.sendTitle(message("bed-destruction-title", gameManager, it), message("bed-destruction-subtitle", gameManager, it), 0, 40, 0)
                }

                location.world!!.spigot().strikeLightningEffect(location, false)

                Bukkit.getOnlinePlayers().forEach {
                    it.playSound(it.location, Sound.ENTITY_WITHER_DEATH, 1f, 1f)
                }

                Bukkit.getOnlinePlayers().forEach {
                    it.sendMessage(message("bed-destruction-all", gameManager, it).replace("{island}", "${island.color.getChatColor()}${if(PlayerData.PLAYERS[it.uniqueId]!!.isRussian()) island.color.russianName() 
                    else island.color.formattedName()}")
                    .replace("{player-island}", gameManager.world.getIslandForPlayer(player)!!.color.getChatColor().toString()).replace("{player}", player.name)) }

            } else {
                player.sendMessage("${ChatColor.RED}${if(PlayerData.PLAYERS[player.uniqueId]!!.isRussian()) "Вы не можете сломать свою кровать..." else "You cannot break your bed..."}")
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

        if(!event.block.hasMetadata("placed") && type != Material.FIRE){
            event.isCancelled = true
            return
        }
    }

    @EventHandler
    fun onExplode(event: EntityExplodeEvent){
        val blockIterator: MutableIterator<Block> = event.blockList().iterator()

        while(blockIterator.hasNext()){
            try{
                val block: Block = blockIterator.next()
                if(!block.hasMetadata("placed") || block.type.name.contains("BED")){
                    blockIterator.remove()
                }
            } catch (ex: Exception){
                blockIterator.remove()
            }
        }
        for(entity in GameManager.getNearbyPlayers(event.entity.location, 6.0)){
            entity.velocity = Vector((entity.location.x - event.entity.location.x)/5, 1.0, (entity.location.z - event.entity.location.z)/5)
        }
    }

    @EventHandler
    fun onBlockExplode(event: BlockExplodeEvent){
        val blockIterator: MutableIterator<Block> = event.blockList().iterator()

        while(blockIterator.hasNext()){
            try{
                val block: Block = blockIterator.next()
                if(!block.hasMetadata("placed") || block.type.name.contains("BED")){
                    blockIterator.remove()
                }
            } catch (ex: Exception){
                blockIterator.remove()
            }
        }
    }

    @EventHandler
    fun onPlace(event: BlockPlaceEvent){
        if (event.player.gameMode == GameMode.CREATIVE) return
        if (gameManager.state != GameState.ACTIVE && gameManager.state != GameState.WON) {
            event.isCancelled = true
            return
        }

        for (island: Island in gameManager.world.islands) {
            if (island.isBlockWithinProtectedZone(event.block)) {
                event.player.sendMessage("${ChatColor.RED}${if (PlayerData.PLAYERS[event.player.uniqueId]!!.isRussian()) "Вы не можете ставить блоки сдесь"
                else "You cannot place blocks here"}")
                event.isCancelled = true
                return
            }
        }

        if (event.blockPlaced.type == Material.TNT) {
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