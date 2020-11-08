package me.mrfunny.plugins.paper.worlds

import me.mrfunny.plugins.paper.manager.GameManager
import me.mrfunny.plugins.paper.worlds.generators.Generator
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.WorldCreator
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import java.io.*
import java.util.*
import java.util.stream.Collectors
import kotlin.collections.ArrayList

class GameWorld(var name: String) {
    var world: World = if(Bukkit.getWorld(name) != null) {
        Bukkit.getWorld(name)!!
    } else {
        Bukkit.getWorld("world")!!
    }

    var islands = arrayListOf<Island>()
    var generators: ArrayList<Generator> = arrayListOf()
    lateinit var lobbyPosition: Location

    fun loadWorld(gameManager: GameManager, loadingIntoPlaying: Boolean, runnable: Runnable) {
        val sourceFolder = File("${gameManager.plugin.dataFolder.canonicalPath}${File.separator}..${File.separator}..${File.separator}$name")
        val dest = File(if (loadingIntoPlaying) "${sourceFolder.path}_playing" else "")
        try{
            copyFolder(sourceFolder, dest)
        } catch (ex: IOException){
            ex.printStackTrace()
        }

        val creator = WorldCreator(if (loadingIntoPlaying) "${sourceFolder.path}_playing" else "")
        world = creator.createWorld()!!

        val section: ConfigurationSection = gameManager.configurationManager.configuration

        runnable.run()
    }

    private fun copyFolder(src: File, destination: File){
        if(src.isDirectory){
            if(!destination.exists()){
                destination.mkdir()
                println("[BedWars] directory copied from $src to $destination")
            }

            for (file in src.list()) {
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
        if(world == null) return

        val worldName: String = world.name
        Bukkit.unloadWorld(world, false)

        val file = File("${Bukkit.getWorldContainer().absolutePath.replace(".", "")}${world.name}")

        if(delete(file)){
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

            val oneExtraZ = location.add(.0, .0, 1.0)
            if(it.bedLocation == oneExtraZ){
                return@filter true
            }

            val oneExtraX = location.add(1.0, .0, .0)
            if(it.bedLocation == oneExtraX){
                return@filter true
            }

            val oneLessZ = location.add(.0, .0, -1.0)
            if(it.bedLocation == oneLessZ){
                return@filter true
            }

            val oneLessX = location.add(-1.0, .0, .0)
            if(it.bedLocation == oneLessX){
                return@filter true
            }

            return@filter false
        }.findFirst()

        return islandOptional.orElse(null)
    }

    fun getSpawnForTeamColor(color: TeamColor): Location? {
        val optional: Optional<Island> = islands.stream().filter{
            it.color == color
        }.findFirst()

        if(!optional.isPresent){
            return null
        }

        return optional.get().spawnLocation
    }

    fun getIslandForPlayer(player: Player): Island? {
        return islands.stream().filter{island ->
            return@filter island.isMember(player)
        }.findFirst().orElse(null)
    }

    fun getActiveIslands(): List<Island>{
        return islands.stream().filter{ island -> island.isBedPlaced() && island.alivePlayerCount() != 0}.collect(
            Collectors.toList())
    }

}