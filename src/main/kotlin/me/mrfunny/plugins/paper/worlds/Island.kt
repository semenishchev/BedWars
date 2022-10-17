package me.mrfunny.plugins.paper.worlds

import me.mrfunny.plugins.paper.gui.shops.teamupgrades.UpgradeItem
import me.mrfunny.plugins.paper.players.PlayerData
import me.mrfunny.plugins.paper.util.Colorize
import me.mrfunny.plugins.paper.worlds.generators.Generator
import me.mrfunny.plugins.paper.worlds.generators.GeneratorType
import org.bukkit.*
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.entity.Villager
import java.util.*
import java.util.stream.Collectors
import kotlin.math.max
import kotlin.math.min

class Island(var gameWorld: GameWorld, var color: IslandColor) {

    var protectedCorner1: Location? = null
    var protectedCorner2: Location? = null
    var upgradeEntityLocation: Location? = null
    var shopEntityLocation: Location? = null
    var bedLocation: Location? = null
    var spawnLocation: Location? = null

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
    var absolutelyAlive = arrayListOf<UUID>()
//    var leftPlayers = hashMapOf<UUID, NPC>()

    val upgrades = arrayListOf<UpgradeItem>()

    fun hasUpgrade(name: String): Boolean{
        for(upgrade in upgrades){
            return upgrade.id == name
        }
        return false
    }

    fun addMember(player: Player){
        players.add(player)
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
        val itemShopEntity: Villager = shopEntityLocation!!.world!!.spawn(shopEntityLocation!!, Villager::class.java)
        itemShopEntity.profession = Villager.Profession.BUTCHER
        itemShopEntity.customName = Colorize.c("&eItem shop")
        itemShopEntity.isCustomNameVisible = true
        itemShopEntity.setAI(false)

        val teamUpgradeShop: Villager = upgradeEntityLocation!!.world!!.spawn(upgradeEntityLocation!!, Villager::class.java)
        teamUpgradeShop.customName = Colorize.c("&eTeam upgrades")
        teamUpgradeShop.isCustomNameVisible = true
        teamUpgradeShop.setAI(false)
    }

    fun activateEmeraldGenerators(){
        islandGenerators.forEach {
            if(it.type == GeneratorType.EMERALD){
                it.activated = true
            }
        }
    }

    fun getUpgrade(id: String): UpgradeItem?{
        upgrades.forEach {
            if(it.id == id){
                return it
            }
        }
        return null
    }

    fun isBlockWithinProtectedZone(block: Block): Boolean{
        val containts: Boolean = blocksFromTwoPoints(protectedCorner1!!, protectedCorner2!!).contains(block.location)
        return containts

    }

    fun blocksFromTwoPoints(loc1: Location, loc2: Location): ArrayList<Location>{
        val blocks = arrayListOf<Location>()

        val topBlockX = (max(loc1.blockX, loc2.blockX))
        val bottomBlockX = (min(loc1.blockX, loc2.blockX))

        val topBlockY = (max(loc1.blockY, loc2.blockY))
        val bottomBlockY = (min(loc1.blockY, loc2.blockY))

        val topBlockZ = (max(loc1.blockZ, loc2.blockZ))
        val bottomBlockZ = (min(loc1.blockZ, loc2.blockZ))

        for (x in bottomBlockX..topBlockX){
            for(y in bottomBlockY..topBlockY) {
                for (z in bottomBlockZ..topBlockZ) {
                    blocks.add(loc1.world!!.getBlockAt(x, y, z).location)
                }
            }
        }

        return blocks
    }

    fun calculateStat(): Int{
        var result = 0
        for(it in players) {
            if(PlayerData.PLAYERS[it.uniqueId]?.totalDeaths!! == 0){
                result += PlayerData.PLAYERS[it.uniqueId]?.totalKills!!
                continue
            }
            result += PlayerData.PLAYERS[it.uniqueId]?.totalKills!! / PlayerData.PLAYERS[it.uniqueId]?.totalDeaths!!
        }
        return result
    }

    fun isBedPlaced(): Boolean{
        if(bedLocation == null) return false

        if(bedLocation!!.block.type.name.contains("BED")){
            return true
        }

        val oneExtraZ = bedLocation!!.clone().add(0.0, 0.0, 1.0)
        if(oneExtraZ.block.type.name.contains("BED")){
            return true
        }

        val oneExtraX = bedLocation!!.clone().add(1.0, 0.0, 0.0)
        if(oneExtraX.block.type.name.contains("BED")){
            return true
        }

        val oneLessZ = bedLocation!!.clone().add(0.0, .0, -1.0)
        if(oneLessZ.block.type.name.contains("BED")){
            return true
        }

        val oneLessX = bedLocation!!.clone().add(-1.0, 0.0, .0)
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