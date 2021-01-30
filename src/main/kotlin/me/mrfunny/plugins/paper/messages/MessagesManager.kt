package me.mrfunny.plugins.paper.messages

import me.mrfunny.plugins.paper.BedWars.Companion.colorize
import me.mrfunny.plugins.paper.gamemanager.GameManager
import me.mrfunny.plugins.paper.players.PlayerData
import org.bukkit.Bukkit
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player

class MessagesManager(val gameManager: GameManager){
    private val mapName: String = gameManager.world.name.replace("_playing", "")
    init {
        if(!gameManager.englishMessages.get().isConfigurationSection(mapName)){
            register(gameManager.englishMessages.get(),"bed-destruction-all", "&fBED DESTRUCTION> {island}&f bed has been destroyed by {player-island}{player}")
            register(gameManager.englishMessages.get(),"bed-destruction-title", "&cYOU BED HAS BEEN DESTROYED")
            register(gameManager.englishMessages.get(),"bed-destruction-subtitle", "&aYOU WILL NO LONGER RESPAWN")
            register(gameManager.englishMessages.get(),"team-destruction", "{team} has been destroyed")
            register(gameManager.englishMessages.get(),"ruby-two", "&4Ruby&f generators has been upgraded to level II")
            register(gameManager.englishMessages.get(),"ruby-three", "&4Ruby&f generators has been upgraded to level III")
            register(gameManager.englishMessages.get(),"cannot-break-your-bed", "&cYou cannot break your bed...")
            register(gameManager.englishMessages.get(),"all-bed-destruction", "&c&lALL BEDS DESTRUCTION IN {time}")
            register(gameManager.englishMessages.get(),"all-bed-destroyed", "&c&lALL BEDS HAS BEEN DESTROYED")
            register(gameManager.englishMessages.get(),"game-ending", "&c&lGAME ENDING IN {time}")
            gameManager.englishMessages.save()
        }
        if(!gameManager.russianMessages.get().isConfigurationSection(mapName)){
            register(gameManager.russianMessages.get(),"bed-destruction-all", "&fBED DESTRUCTION> {island}ая&f кровать была разрушена {player-island}{player}")
            register(gameManager.russianMessages.get(),"bed-destruction-title", "&cВАША КРОВАТЬ РАЗРУШЕНА")
            register(gameManager.russianMessages.get(),"bed-destruction-subtitle", "&aВЫ БОЛЬШЕ НЕ СМОЖЕТЕ ВОЗРОЖДАТСЯ")
            register(gameManager.russianMessages.get(),"team-destruction", "{team}ые &fбыли уничтожены")
            register(gameManager.russianMessages.get(),"ruby-two", "&4Ruby&f generators has been upgraded to level II")
            register(gameManager.russianMessages.get(),"ruby-three", "&4Ruby&f generators has been upgraded to level III")
            register(gameManager.russianMessages.get(),"cannot-break-your-bed", "&cYou cannot break your bed...")
            register(gameManager.russianMessages.get(),"all-bed-destruction", "&c&lALL BEDS DESTRUCTION IN {time}")
            register(gameManager.russianMessages.get(),"all-bed-destroyed", "&c&lALL BEDS HAS BEEN DESTROYED")
            register(gameManager.russianMessages.get(),"game-ending", "&c&lGAME ENDING IN {time}")
            gameManager.russianMessages.save()
        }
    }

    private fun register(fileConfig: FileConfiguration, path: String, value: Any?){
        fileConfig.set("$mapName.$path", value)
    }


    companion object {
        fun message(value: String, gameManager: GameManager, player: Player): String {

            return if(PlayerData.PLAYERS[player.uniqueId]!!.isRussian()){
                gameManager.russianMessages.get().getString("${gameManager.world.name.replace("_playing", "")}.$value")!!
            } else {
                gameManager.englishMessages.get().getString("${gameManager.world.name.replace("_playing", "")}.$value")!!
            }.colorize()
        }

//        fun String.broadcast(value: String, gameManager: GameManager){
//            Bukkit.getOnlinePlayers().forEach {
//                it.sendMessage(message(value, gameManager, it))
//            }
//        }
//        fun String.broadcast(value: String, gameManager: GameManager, players: ArrayList<Player>){
//            players.forEach {
//                it.sendMessage(message(value, gameManager, it))
//            }
//        }

    }

}