package me.mrfunny.plugins.paper.messages

import me.mrfunny.plugins.paper.BedWars.Companion.colorize
import me.mrfunny.plugins.paper.gamemanager.GameManager

class MessagesManager(val gameManager: GameManager){
    private val mapName: String = gameManager.world.name.replace("_playing", "")
    init {
        if(!gameManager.messagesConfig.get().isConfigurationSection(mapName)){
            gameManager.messagesConfig.get().set("$mapName.bed-destruction-all", "&fBED DESTRUCTION> {island}&f bed has been destroyed by {player-island}{player}")
            gameManager.messagesConfig.get().set("$mapName.bed-destruction-title", "&cYOU BED HAS BEEN DESTROYED")
            gameManager.messagesConfig.get().set("$mapName.bed-destruction-subtitle", "&aYOU WILL NO LONGER RESPAWN")
            gameManager.messagesConfig.get().set("$mapName.team-destruction", "{team} has been destroyed")
            gameManager.messagesConfig.get().set("$mapName.ruby-two", "&4Ruby &f generators has been upgraded to level I")
            gameManager.messagesConfig.get().set("$mapName.ruby-three", "&4Ruby &f generators has been upgraded to level II")
            gameManager.messagesConfig.get().set("$mapName.cannot-break-your-bed", "&4Ruby &f generators has been upgraded to level II")
        }

    }

    companion object {
        fun message(value: String, gameManager: GameManager): String { return gameManager.messagesConfig.get().getString("${gameManager.world.name.replace("_playing", "")}.$value")!!.colorize() }
    }

}