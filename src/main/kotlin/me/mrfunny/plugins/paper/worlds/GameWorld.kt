package me.mrfunny.plugins.paper.worlds

import me.mrfunny.plugins.paper.gamemanager.GameManager
import me.mrfunny.plugins.paper.worlds.generators.Generator
import org.bukkit.*
import org.bukkit.entity.Player
import java.io.*
import java.util.*
import java.util.stream.Collectors
import kotlin.collections.ArrayList

class GameWorld(var name: String) {
    lateinit var world: World
    var islands = arrayListOf<Island>()

    var generators: ArrayList<Generator> = arrayListOf()

    lateinit var lobbyPosition: Location
    lateinit var destinationWorldFolder: File

    val maxTeamSize: Int = 1

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
        world.setGameRule(GameRule.DO_MOB_SPAWNING, false)


        runnable.run()
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
        return islands.stream().filter{ island -> (island.isBedPlaced() && island.alivePlayerCount() != null) || island.alivePlayerCount() != 0}.collect(
            Collectors.toList())
    }
}