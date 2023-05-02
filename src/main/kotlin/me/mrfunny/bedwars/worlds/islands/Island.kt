package me.mrfunny.bedwars.worlds.islands

import me.mrfunny.bedwars.BedWars.Companion.colorize
import me.mrfunny.bedwars.BedWars.Companion.protect
import me.mrfunny.bedwars.players.PlayerData
import me.mrfunny.bedwars.util.Colorize
import me.mrfunny.bedwars.worlds.GameWorld
import me.mrfunny.bedwars.worlds.generators.Generator
import me.mrfunny.bedwars.worlds.generators.GeneratorType
import me.mrfunny.bedwars.worlds.islands.teamupgrades.UpgradeItem
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.entity.Villager
import java.util.*
import java.util.stream.Collectors
import kotlin.math.max
import kotlin.math.min

class Island(var gameWorld: GameWorld, var color: IslandColor) {

    lateinit var protectedCorner1: Location
    lateinit var protectedCorner2: Location
    lateinit var upgradeEntityLocation: Location
    lateinit var shopEntityLocation: Location
    lateinit var bedLocation: Location
    lateinit var spawnLocation: Location

    var totalSouls: Int = 0
    
    var islandGenerators = arrayListOf<Generator>()
    set(value) {
        field = value

        islandGenerators.forEach {
            if(it.type != GeneratorType.EMERALD){
                it.activated = true
            }
        }
    }

    var players = arrayListOf<Player>()
    var deadPlayers = arrayListOf<UUID>()

    val upgrades = arrayListOf<UpgradeItem>()

    fun hasUpgrade(name: String): Boolean{
        for(upgrade in upgrades){
            return upgrade.id == name
        }
        return false
    }

    fun addMember(player: Player){
        players.add(player)
        PlayerData.get(player).assignedIsland = this
    }

    fun removeMember(player: Player){
        players.remove(player)
    }

    fun isMember(player: Player): Boolean{
        return players.contains(player)
    }

//    fun rejoin(uuid: UUID){
//        if(leavedPlayers.contains(uuid)){
//            players.add(Bukkit.getPlayer(uuid)!!)
//        }
//    }

    fun spawnShops(){
        shopEntityLocation.world.spawn(shopEntityLocation, Villager::class.java) {
            it.profession = Villager.Profession.BUTCHER
            it.customName = "&eItem shop".colorize()
            it.isCustomNameVisible = true
            it.setAI(false)
        }

        upgradeEntityLocation.world.spawn(upgradeEntityLocation, Villager::class.java) {
            it.customName = "&eTeam upgrades".colorize()
            it.isCustomNameVisible = true
            it.setAI(false)
            it.protect()
        }

    }

    fun activateEmeraldGenerators(){
        for (it in islandGenerators) {
            if(it.type != GeneratorType.EMERALD) continue
            it.activated = true
        }
    }

    fun getUpgrade(id: String): UpgradeItem?{
        for (it in upgrades) {
            if(it.id == id){
                return it
            }
        }
        return null
    }

    fun getUpgrade(upgrade: Class<out UpgradeItem>): UpgradeItem {
        for(it in upgrades) {
            if(it::class.java == upgrade) {
                return it
            }
        }

        throw RuntimeException("Upgrade class not registered!")
    }

    fun isBlockWithinProtectedZone(block: Block): Boolean {
        return block.x in min(protectedCorner1.blockX, protectedCorner2.blockX)..max(protectedCorner1.blockX, protectedCorner2.blockX) &&
                block.y in min(protectedCorner1.blockY, protectedCorner2.blockY)..max(protectedCorner1.blockY, protectedCorner2.blockY) &&
                block.z in min(protectedCorner1.blockZ, protectedCorner2.blockZ)..max(protectedCorner1.blockZ, protectedCorner2.blockZ)
    }

    fun calculateStat(): Int{
        var result = 0
        for(it in players) {
            val data = PlayerData.get(it)
            if(data.totalDeaths == 0) {
                result += data.totalKills
                continue
            }
            result += data.totalKills / data.totalDeaths
        }
        return result
    }

    fun isBedPlaced(): Boolean{
        if(!this::bedLocation.isInitialized) return false

        if(bedLocation.block.type.name.contains("BED")){
            return true
        }

        val oneExtraZ = bedLocation.clone().add(0.0, 0.0, 1.0)
        if(oneExtraZ.block.type.name.contains("BED")){
            return true
        }

        val oneExtraX = bedLocation.clone().add(1.0, 0.0, 0.0)
        if(oneExtraX.block.type.name.contains("BED")){
            return true
        }

        val oneLessZ = bedLocation.clone().add(0.0, .0, -1.0)
        if(oneLessZ.block.type.name.contains("BED")){
            return true
        }

        val oneLessX = bedLocation.clone().add(-1.0, 0.0, .0)
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
        for(deadPlayer: UUID in deadPlayers){
            if(alive.stream().noneMatch { player -> player.uniqueId == deadPlayer }){
                val possiblePlayer = Bukkit.getPlayer(deadPlayer)
                if(possiblePlayer != null){
                    count++
                } else {
                    deadPlayers.remove(deadPlayer)
                }
            }
        }
        return count
    }
}