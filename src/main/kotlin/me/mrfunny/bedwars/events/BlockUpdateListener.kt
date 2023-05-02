package me.mrfunny.bedwars.events

import me.mrfunny.bedwars.BedWars.Companion.canEnvironmentBreak
import me.mrfunny.bedwars.BedWars.Companion.colorize
import me.mrfunny.bedwars.game.GameManager
import me.mrfunny.bedwars.game.GameState
import me.mrfunny.bedwars.players.PlayerData
import me.mrfunny.bedwars.worlds.islands.Island
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
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

    @EventHandler //FIXME: localisation proceed
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

            val island = gameManager.world.getIslandForBedLocation(location)
            if(island == null) {
                event.isCancelled = true
                return
            }


            if(!island.isMember(player)){
                gameManager.updateScoreboard(true)
                val attackerData = PlayerData.get(player)
                for (it in island.players) {
                    val data = PlayerData.get(it)
                    it.sendTitle("&cYour bed got destroyed".colorize(), "&fYou will no longer respawn".colorize())
//                    it.sendTitle(gameManager.messages.localise(data, "bed-destruction-title"), gameManager.messages.localise(data, "bed-destruction-subtitle"), 0, 40, 0)
                }

                location.world.spigot().strikeLightningEffect(location, false)

                for (data in PlayerData.all()) {
                    data.getPlayer()?.let {
                        it.playSound(it.location, Sound.ENTITY_WITHER_DEATH, 1f, 1f)
                        val base = Component.text("BED DESTRUCTION! ", NamedTextColor.WHITE).decorate(TextDecoration.BOLD)
                        if(data.assignedIsland == island) {
                            base.append(Component.text("Your bed was destroyed by ", NamedTextColor.GRAY))
                        } else {
                            base.append(Component.text(island.color.formattedName(), island.color.toNamed()).append(Component.text(" bed")))
                                .append(Component.text(" was destroyed by ", NamedTextColor.GRAY))
                        }
                        it.sendMessage(base.append(player.displayName().color(attackerData.assignedIsland.color.toNamed())))
                    }

                }
                attackerData.addCoinsAndSave(100, "bed destruction")
            } else {
                player.sendMessage("${ChatColor.RED}Just, don't...")
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
        handleBlocks(event.blockList().iterator())
        for(entity in event.entity.getNearbyEntities(2.5, 0.5, 2.5)){
            entity.velocity = Vector((entity.location.x - event.entity.location.x)/5 * event.yield, 1.0 * event.yield, (entity.location.z - event.entity.location.z)/5 * event.yield)
        }
    }

    @EventHandler
    fun onBlockExplode(event: BlockExplodeEvent){
        handleBlocks(event.blockList().iterator())
    }

    private fun handleBlocks(blockIterator: MutableIterator<Block>) {
        while(blockIterator.hasNext()){
            try{
                val block: Block = blockIterator.next()
                if(!block.canEnvironmentBreak()) {
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

        val player = event.player

        for (island: Island in gameManager.world.islands) {
            if (island.isBlockWithinProtectedZone(event.block)) {
                player.sendMessage(Component.text("You can't place blocks here", NamedTextColor.RED))
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
            (location.world.spawnEntity(location, EntityType.PRIMED_TNT) as TNTPrimed).fuseTicks = 40
            val newItem = event.itemInHand
            newItem.amount = newItem.amount - 1
            player.inventory.remove(event.itemInHand)
            player.inventory.addItem(newItem)
            player.updateInventory()
            return
        }

        event.block.setMetadata("placed", FixedMetadataValue(gameManager.plugin, "block"))
    }
}