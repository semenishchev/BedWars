package me.mrfunny.plugins.paper.worlds

import me.mrfunny.api.PlayerApi
import me.mrfunny.plugins.paper.BedWars.Companion.colorize
import me.mrfunny.plugins.paper.gamemanager.GameEvent
import me.mrfunny.plugins.paper.gamemanager.GameManager
import me.mrfunny.plugins.paper.messages.MessagesManager.Companion.message
import me.mrfunny.plugins.paper.util.Colorize
import me.mrfunny.plugins.paper.worlds.generators.Generator
import me.mrfunny.plugins.paper.worlds.generators.GeneratorTier
import me.mrfunny.plugins.paper.worlds.generators.GeneratorType
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.*
import org.bukkit.block.Block
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Damageable
import org.bukkit.entity.Monster
import org.bukkit.entity.Player
import org.bukkit.entity.Wither
import java.io.*
import java.util.*
import java.util.stream.Collectors
import kotlin.collections.ArrayList
import kotlin.math.round

class GameWorld(var name: String, val gameManager: GameManager) {
    lateinit var world: World
    var islands = arrayListOf<Island>()

    var generators: ArrayList<Generator> = arrayListOf()

    lateinit var lobbyPosition: Location
    lateinit var destinationWorldFolder: File

    var maxTeamSize: Int = 0
    var maxTeams: Int = 0

    var diamondTier: GeneratorTier = GeneratorTier.ONE
    var emeraldTier: GeneratorTier = GeneratorTier.ONE

    fun loadWorld(gameManager: GameManager, loadingIntoPlaying: Boolean, runnable: Runnable) {
        println("Set file separator as ${File.separator}. Path is ${gameManager.plugin.dataFolder.canonicalPath}")
        val sourceFolder = File("${gameManager.plugin.dataFolder.canonicalPath}${File.separator}..${File.separator}..${File.separator}$name")
        destinationWorldFolder = File(sourceFolder.path + if (loadingIntoPlaying) "_playing" else "")
        try{
            copyFolder(sourceFolder, destinationWorldFolder)
        } catch (ex: IOException){
            ex.printStackTrace()
        }

        println(sourceFolder.path)
        println(destinationWorldFolder.path)

        val creator = WorldCreator(name + if (loadingIntoPlaying) "_playing" else "")
        world = if(Bukkit.getWorld(name + if (loadingIntoPlaying) "_playing" else "") == null)creator.createWorld()!! else Bukkit.getWorld(name + if (loadingIntoPlaying) "_playing" else "")!!
        println(world.name)

//        println("\n\n\n${gameManager.gameConfig.get().isConfigurationSection("max-teams")} ${gameManager.gameConfig.get().isConfigurationSection("max-team-size")}\n\n\n")

        if(!gameManager.gameConfig.get().contains("border")){
            gameManager.gameConfig.get().set("border", 250.0)
        }
        if(!gameManager.gameConfig.get().contains("center")){
            gameManager.gameConfig.get().set("center.x", 0.5)
            gameManager.gameConfig.get().set("center.z", 0.5)
        }
        if(!gameManager.gameConfig.get().contains("max-team-size")){
            gameManager.gameConfig.get().set("max-team-size", 1)
        }

        if(!gameManager.gameConfig.get().contains("max-teams")){
            gameManager.gameConfig.get().set("max-teams", -1)
        }

        if(!gameManager.gameConfig.get().contains("percentage-to-start")){
            gameManager.gameConfig.get().set("percentage-to-start", 80)
        }

        gameManager.gameConfig.save()

        world.worldBorder.reset()

        maxTeams = if(gameManager.gameConfig.get().getInt("max-teams") == -1) Byte.MAX_VALUE.toInt() else gameManager.gameConfig.get().getInt("max-teams")
        maxTeamSize = gameManager.gameConfig.get().getInt("max-team-size")

        world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false)
        world.setGameRule(GameRule.DISABLE_RAIDS, true)
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false)
        world.setGameRule(GameRule.DO_WEATHER_CYCLE, false)
        world.setGameRule(GameRule.RANDOM_TICK_SPEED, 0)
        world.setGameRule(GameRule.DO_FIRE_TICK, false)
        world.setGameRule(GameRule.REDUCED_DEBUG_INFO, true)
        world.difficulty = Difficulty.NORMAL
        world.setStorm(false)
        world.isThundering = false

        world.entities.forEach {
            if(it is Monster && !it.hasMetadata("protected")){
                it.remove()
            } else if(it is Wither){
                it.remove()
            }
        }

        runnable.run()
    }

    fun isMapInit(): Boolean{
        return this::lobbyPosition.isInitialized
    }

    private fun copyFolder(src: File, destination: File){
        if(src.name == "uid.dat") return
        if(src.isDirectory){
            if(!destination.exists()){
                destination.mkdir()
                println("[BedWars] directory copied from $src to $destination")
            }

            val list: Array<String> = src.list()

            for (file in list) {
                val srcFile = File(src, file)
                if(srcFile.name == "uid.dat") continue
                val destFile = File(destination, file)

                try {
                    copyFolder(srcFile, destFile)
                } catch (ex: Exception){
                    continue
                }
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

    fun resetWorld(unload: Boolean, save: Boolean){
        val worldName: String = world.name

        if(unload){
            Bukkit.unloadWorld(world, save)
        }

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
        gameManager.secondsTimer--
        val minuteOfGame: Double = secondsToMinutes(currentSecond)

        if(minuteOfGame == 5.0){
            Bukkit.getOnlinePlayers().forEach{
                it.sendMessage(message("ruby-two", gameManager, it))
            }
            emeraldTier = GeneratorTier.THREE
            gameManager.currentEvent = GameEvent.RUBY_THREE
        }

        if(minuteOfGame == 10.0){
            Bukkit.getOnlinePlayers().forEach{ it.sendMessage(message("ruby-three", gameManager, it)) }
            emeraldTier = GeneratorTier.THREE
            gameManager.currentEvent = GameEvent.ALL_BEDS_DESTRUCTION
        }

        if(minuteOfGame == 20.0){
            Bukkit.getOnlinePlayers().forEach {
                gameManager.bossBar.addPlayer(it)
                it.sendMessage(message("all-bed-destruction", gameManager, it).replace("{time}", "10m"))
            }
        }

        if(minuteOfGame == 25.0){
            Bukkit.getOnlinePlayers().forEach { it.sendMessage(message("all-bed-destruction", gameManager, it).replace("{time}", "5m")) }
        }

        if(currentSecond == 1795){
            Bukkit.getOnlinePlayers().forEach{ it.sendMessage(message("all-bed-destruction", gameManager, it).replace("{time}", "5")) }
        }

        if(currentSecond == 1796){
            Bukkit.getOnlinePlayers().forEach{ it.sendMessage(message("all-bed-destruction", gameManager, it).replace("{time}", "4")) }
        }

        if(currentSecond == 1797){
            Bukkit.getOnlinePlayers().forEach{ it.sendMessage(message("all-bed-destruction", gameManager, it).replace("{time}", "3")) }
        }

        if(currentSecond == 1798){
            Bukkit.getOnlinePlayers().forEach{ it.sendMessage(message("all-bed-destruction", gameManager, it).replace("{time}", "2")) }
        }

        if(currentSecond == 1799){
            Bukkit.getOnlinePlayers().forEach{ it.sendMessage(message("all-bed-destruction", gameManager, it).replace("{time}", "1")) }
        }

        if(currentSecond == 1800){
            islands.forEach {
                it.bedLocation?.block?.type = Material.AIR
                world.spigot().strikeLightningEffect(it.bedLocation!!, false)
                world.spawnParticle(Particle.EXPLOSION_HUGE, it.bedLocation!!, 1)
                world.playSound(it.bedLocation!!, Sound.ENTITY_GENERIC_EXPLODE, 1f, 1f)
                for(player in it.players){
                    player.playSound(player.location, Sound.ENTITY_ENDER_DRAGON_DEATH, 1f, 1f)
                }
            }

            Bukkit.getOnlinePlayers().forEach{ it.sendMessage(message("all-bed-destroyed", gameManager, it)) }
            Bukkit.getOnlinePlayers().forEach{ it.sendMessage(message("game-ending", gameManager, it).replace("{time}", "15 MINUTES")) }

            gameManager.currentEvent = GameEvent.GAME_END
        }

        if(minuteOfGame == 45.0){
            gameManager.forceEndGame()
        }

        Bukkit.getOnlinePlayers().forEach {
            if(it.gameMode != GameMode.SPECTATOR){
                val closestPlayer: Player? = PlayerApi.getNearestPlayerFromOtherTeam(it, gameManager)
                var possibleAddition = ""
                if(closestPlayer != null){
                    it.compassTarget = closestPlayer.location
                    if(it.inventory.itemInMainHand.type == Material.COMPASS || it.inventory.itemInOffHand.type == Material.COMPASS){
                        possibleAddition = "&l${getIslandForPlayer(closestPlayer)!!.color.getChatColor()}${closestPlayer.name}&7 — &a${round(closestPlayer.location.distance(it.location))}M &f• "
                    }
                }
                it.sendActionBar(possibleAddition.colorize() + "&f${gameManager.playerManager.getIronCount(it)} iron &f• &6${gameManager.playerManager.getGoldCount(it)} gold &f• &4${gameManager.playerManager.getRubyCount(it)} ruby &f• &b${getIslandForPlayer(it)?.totalSouls} souls".colorize())
            }
        }

        for(island: Island in islands){
            island.islandGenerators.forEach {
                it.spawn(gameManager)
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
            it.spawn(gameManager)
        }
        gameManager.updateScoreboard(false)
    }

    private fun secondsToMinutes(seconds: Int): Double{
        return seconds / 60.0
    }

    fun isBlockInProtectedZone(block: Block): Boolean{
        for(island in islands){
            if(island.isBlockWithinProtectedZone(block)){
                return true
            }
        }
        return false
    }
}