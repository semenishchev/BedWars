package me.mrfunny.plugins.paper.util

import me.mrfunny.plugins.paper.players.NoFallPlayers
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.util.Vector

object TeleportUtil {

    const val gravity: Double = -0.08

    fun pullEntityToLocation(entity: Player, loc: Location) {
        NoFallPlayers.add(entity)
        val entityLoc = entity.location
        entityLoc.y = entityLoc.y + 0.5
        val distance = entityLoc.distance(loc)
        val vX: Double = (1.0 + 0.07 * distance) * (loc.x - entityLoc.x) / distance * 1.259
        val vY: Double = (1.0 + 0.03 * distance) * (loc.y - entityLoc.y) / distance - 0.5 * gravity * distance * 1.3
        val vZ: Double = (1.0 + 0.07 * distance) * (loc.z - entityLoc.z) / distance * 1.259
        entity.velocity = Vector(vX, vY, vZ)
    }

}