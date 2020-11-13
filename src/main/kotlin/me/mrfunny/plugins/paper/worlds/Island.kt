package me.mrfunny.plugins.paper.worlds

import com.sk89q.worldedit.math.BlockVector3
import com.sk89q.worldedit.regions.CuboidRegion
import me.mrfunny.plugins.paper.worlds.generators.Generator
import org.bukkit.*
import org.bukkit.block.Block
import org.bukkit.entity.Player
import java.util.*
import java.util.stream.Collectors

class Island(var gameWorld: GameWorld, var color: IslandColor) {

    var protectedCorner1: Location? = null
    var protectedCorner2: Location? = null
    var upgradeEntityLocation: Location? = null
    var shopEntityLocation: Location? = null
    var bedLocation: Location? = null
    var spawnLocation: Location? = null

    var islandGenerators = arrayListOf<Generator>()

    var players = arrayListOf<Player>()
    var absolutelyAlive = arrayListOf<UUID>()
    var isAlive: Boolean = true

    fun isMember(player: Player): Boolean{
        return players.contains(player)
    }

    fun isBlockWithinProtectedZone(block: Block): Boolean{
        val blockLocation: Location = block.location

        val one: BlockVector3 = BlockVector3.at(protectedCorner1!!.x, protectedCorner1!!.y, protectedCorner1!!.z)
        val two: BlockVector3 = BlockVector3.at(protectedCorner2!!.x, protectedCorner2!!.y, protectedCorner2!!.z)

        val region: CuboidRegion = CuboidRegion(one, two)

        return region.contains(BlockVector3.at(blockLocation.x, blockLocation.y, blockLocation.z))

    }

    fun isBedPlaced(): Boolean{
        if(bedLocation == null) return false

        if(bedLocation!!.block.type.name.contains("BED")){
            return true
        }

        val oneExtraZ = bedLocation!!.add(.0, .0, 1.0)
        if(oneExtraZ.block.type.name.contains("BED")){
            return true
        }

        val oneExtraX = bedLocation!!.add(1.0, .0, .0)
        if(oneExtraX.block.type.name.contains("BED")){
            return true
        }

        val oneLessZ = bedLocation!!.add(.0, .0, -1.0)
        if(oneLessZ.block.type.name.contains("BED")){
            return true
        }

        val oneLessX = bedLocation!!.add(-1.0, .0, .0)
        if(oneLessX.block.type.name.contains("BED")){
            return true
        }

        return false
    }

    fun alivePlayerCount(): Int{
        if(isBedPlaced()){
            return players.size
        }

        val alive: List<Player> = players.stream().filter{player -> player.gameMode != GameMode.SPECTATOR}.collect(Collectors.toList())
        var count = alive.size
        for(absolutelyAlivePlayer: UUID in absolutelyAlive){
            if(alive.stream().noneMatch { player -> player.uniqueId == absolutelyAlivePlayer }){
                val oPlayer: OfflinePlayer = Bukkit.getOfflinePlayer(absolutelyAlivePlayer)
                if(oPlayer.isOnline){
                    count++
                } else {
                    absolutelyAlive.remove(absolutelyAlivePlayer)
                }
            }
        }
        return count
    }

}