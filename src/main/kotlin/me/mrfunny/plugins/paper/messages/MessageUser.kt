package me.mrfunny.plugins.paper.messages

import me.mrfunny.plugins.paper.BedWars.Companion.colorize
import me.mrfunny.plugins.paper.gamemanager.GameManager

open class MessageUser(val gameManager: GameManager) {
    val mapName: String = gameManager.world.name.replace("_playing", "")
    fun String.message() = gameManager.messagesConfig.get().getString("$mapName.$this")?.colorize()
}