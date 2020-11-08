package me.mrfunny.plugins.paper.players

import me.mrfunny.plugins.paper.manager.GameManager
import org.bukkit.GameMode
import org.bukkit.entity.Player

class PlayerManager(private val gameManager: GameManager) {

    fun setSpectatorMode(player: Player){
        player.teleport(gameManager.world.lobbyPosition)
        player.gameMode = GameMode.SPECTATOR

        gameManager.endGameIfNeeded()
    }
}