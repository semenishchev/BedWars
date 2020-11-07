package me.mrfunny.plugins.paper.worlds

import com.sk89q.worldedit.math.BlockVector3
import com.sk89q.worldedit.regions.CuboidRegion
import me.mrfunny.plugins.paper.worlds.generators.Generator
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.entity.Player
import java.util.stream.Collectors

class Island(var gameWorld: GameWorld, var color: TeamColor) {

    var protectedCorner1: Location? = null
    var protectedCorner2: Location? = null
    var upgradeEntityLocation: Location? = null
    var shopEntityLocation: Location? = null
    var bedLocation: Location? = null
    var spawnLocation: Location? = null

    var islandGenerators = arrayListOf<Generator>()

    private var players = arrayListOf<Player>()
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
        return !bedLocation?.block?.type.toString().contains("BED")
    }

    fun alivePlayerCount(): Int{
        return players.stream().filter{player -> player.gameMode != GameMode.SPECTATOR}.collect(Collectors.toList()).size
    }

}