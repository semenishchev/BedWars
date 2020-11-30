package me.mrfunny.plugins.paper.worlds

import me.mrfunny.plugins.paper.BedWars.Companion.colorize
import me.mrfunny.plugins.paper.gamemanager.GameManager
import me.mrfunny.plugins.paper.util.Colorize
import me.mrfunny.plugins.paper.worlds.generators.Generator
import me.mrfunny.plugins.paper.worlds.generators.GeneratorTier
import me.mrfunny.plugins.paper.worlds.generators.GeneratorType
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.*
import org.bukkit.entity.Player
import java.io.*
import java.util.*
import java.util.stream.Collectors
import kotlin.collections.ArrayList

class GameWorld(var name: String, val gameManager: GameManager) {
    lateinit var world: World
    var islands = arrayListOf<Island>()

    var generators: ArrayList<Generator> = arrayListOf()

    lateinit var lobbyPosition: Location
    lateinit var destinationWorldFolder: File

    val maxTeamSize: Int = 1
    val maxComands: Int = 8

    var diamondTier: GeneratorTier = GeneratorTier.ONE
    var emeraldTier: GeneratorTier = GeneratorTier.ONE

    fun loadWorld(gameManager: GameManager, loadingIntoPlaying: Boolean, runnable: Runnable) {
        val sourceFolder = File("${gameManager.plugin.dataFolder.canonicalPath}${File.separator}..${File.separator}..${File.separator}$name")
        destinationWorldFolder = File(sourceFolder.path + if (loadingIntoPlaying) "_playing" else "")
        try{
            copyFolder(sourceFolder, destinationWorldFolder)
        } catch (ex: IOException){
            ex.printStackTrace()
        }

        val creator = WorldCreator(name + if (loadingIntoPlaying) "_playing" else "")
        world = creator.createWorld()!!

        world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false)
        world.setGameRule(GameRule.DISABLE_RAIDS, true)
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false)
        world.setGameRule(GameRule.DO_WEATHER_CYCLE, false)
        world.difficulty = Difficulty.NORMAL

        runnable.run()
    }

    fun isMapInit(): Boolean{
        return this::lobbyPosition.isInitialized
    }

    private fun copyFolder(src: File, destination: File){
        if(src.isDirectory){
            if(!destination.exists()){
                destination.mkdir()
                println("[BedWars] directory copied from $src to $destination")
            }

            val list: Array<String> = src.list()

            for (file in list) {
                val srcFile = File(src, file)
                val destFile = File(destination, file)

                copyFolder(srcFile, destFile)
            }
        } else {
            val input: InputStream = FileInputStream(src)
            val out: OutputStream = FileOutputStream(destination)

            val buffer = ByteArray(1024)

            var length: Int

            while (input.read(buffer).also { length = it } > 0){
                out.write(buffer, 0, length)
            }

            input.close()
            out.close()
        }
    }

    fun isSolo(): Boolean{
        return maxTeamSize == 1
    }

    fun resetWorld(){
        val worldName: String = world.name

        Bukkit.unloadWorld(world, false)

        if(delete(destinationWorldFolder)){
            println("[BedWars] Reset map $worldName")
        } else {
            println("[BedWard] Failed to delete $worldName")
        }
    }

    private fun delete(toDelete: File): Boolean {

        toDelete.listFiles()?.forEach {
            delete(it)
        }

        return toDelete.delete()
    }

    fun getIslandForBedLocation(location: Location): Island? {
        val islandOptional: Optional<Island> = islands.stream().filter {
            if(it.bedLocation == location){
                return@filter true
            }

            val oneExtraZ = location.clone().add(0.0, .0, 1.0)
            val oneLessZ = location.clone().add(0.0, .0, -1.0)
            val oneExtraX = location.clone().add(1.0, .0, 0.0)
            val oneLessX = location.clone().add(-1.0, 0.0, 0.0)


            val locations: Array<Location> = arrayOf(oneExtraZ, oneExtraX, oneLessZ, oneLessX)

            for(toCheck: Location in locations){
                if(toCheck == it.bedLocation && toCheck.block.type.name.contains("BED")){
                    return@filter true
                }
            }

            return@filter false
        }.findFirst()

        return islandOptional.orElse(null)
    }

    fun getIslandForPlayer(player: Player): Island? {
        return islands.stream().filter{island ->
            return@filter island.isMember(player)
        }.findFirst().orElse(null)
    }

    fun getActiveIslands(): List<Island>{
        return islands.stream().filter{ island -> island.alivePlayerCount() != 0}.collect(
            Collectors.toList())
    }

    fun tick(currentSecond: Int) {
        val minuteOfGame: Double = secondsToMinutes(currentSecond)

        if(minuteOfGame == 5.0){
            Bukkit.broadcastMessage(Colorize.c("&4Ruby&f generators has been upgraded to level II"))
            emeraldTier = GeneratorTier.TWO
        }

        if(minuteOfGame == 10.0){
            Bukkit.broadcastMessage(Colorize.c("&4Ruby&f generators has been upgraded to level III"))
            diamondTier = GeneratorTier.THREE
        }

        if(minuteOfGame == 20.0){
            Bukkit.broadcastMessage("&cALL BEDS DESTRUCTION IN 10 MINUTES")
        }

        if(minuteOfGame == 25.0){
            Bukkit.broadcastMessage("&cALL BEDS DESTRUCTION IN 5 MINUTES")
        }

        if(currentSecond == 1795){
            Bukkit.broadcastMessage("&c&lALL BEDS DESTRUCTION IN 5")
        }

        if(currentSecond == 1796){
            Bukkit.broadcastMessage("&c&lALL BEDS DESTRUCTION IN 4")
        }

        if(currentSecond == 1797){
            Bukkit.broadcastMessage("&c&lALL BEDS DESTRUCTION IN 3")
        }

        if(currentSecond == 1798){
            Bukkit.broadcastMessage("&c&lALL BEDS DESTRUCTION IN 2")
        }

        if(currentSecond == 1799){
            Bukkit.broadcastMessage("&c&lALL BEDS DESTRUCTION IN 1")
        }

        if(currentSecond == 1800){
            islands.forEach {
                it.bedLocation?.block?.breakNaturally()
                world.spigot().strikeLightningEffect(it.bedLocation!!, false)
                world.spawnParticle(Particle.EXPLOSION_HUGE, it.bedLocation!!, 1)
                world.playSound(it.bedLocation!!, Sound.ENTITY_GENERIC_EXPLODE, 1f, 1f)
                for(player in it.players){
                    player.playSound(player.location, Sound.ENTITY_ENDER_DRAGON_DEATH, 1f, 1f)
                }
            }
        }

        Bukkit.getOnlinePlayers().forEach {

            it.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent("&7${gameManager.playerManager.getIronCount(it)} iron &f• &6${gameManager.playerManager.getGoldCount(it)} gold &f• &4${gameManager.playerManager.getRubyCount(it)} ruby &f• &b${getIslandForPlayer(it)?.totalSouls} souls".colorize()))
        }

        for(island: Island in islands){
            island.islandGenerators.forEach {
                it.spawn()
            }
        }

        generators.forEach {
            it.activated = true
            if(it.type == GeneratorType.DIAMOND){
                it.currentTier = diamondTier
            }

            if(it.type == GeneratorType.EMERALD){
                it.currentTier = emeraldTier
            }
            it.spawn()
        }
    }

    private fun secondsToMinutes(seconds: Int): Double{
        return seconds / 60.0
    }
}