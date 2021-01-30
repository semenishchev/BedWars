package me.mrfunny.plugins.paper.events

import me.mrfunny.plugins.paper.BedWars.Companion.colorize
import me.mrfunny.plugins.paper.gamemanager.GameManager
import me.mrfunny.plugins.paper.gamemanager.GameState
import me.mrfunny.plugins.paper.util.Colorize
import me.mrfunny.plugins.paper.worlds.Island
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent

class ChatListeners(private val gameManager: GameManager): Listener {

    @EventHandler
    fun onChat(event: AsyncPlayerChatEvent){
        event.isCancelled = true
        val player: Player = event.player

        val island: Island? = gameManager.world.getIslandForPlayer(player)

        if(event.message.startsWith("!")){
            if(player.gameMode == GameMode.SPECTATOR){
                Bukkit.broadcastMessage("&8Spectator &7${player.name}&8: &7${event.message}".colorize())
                return
            }

            if(island != null){
                Bukkit.broadcastMessage("${island.color.getChatColor()}${island.color.formattedName()} &f${player.name}&8: &7${event.message.substring(1)}".colorize())
                return
            } else {
                Bukkit.broadcastMessage("&3${player.name}&8: &7${event.message.substring(1)}".colorize())
                return
            }
        } else {
            if(island != null){
                island.players.forEach {
                    it.sendMessage("${island.color.getChatColor()}Team &f${player.name}&8: &f${event.message}".colorize())
                }
            } else if(!event.message.startsWith("!") && gameManager.state == GameState.ACTIVE) {
                Bukkit.getOnlinePlayers().forEach {
                    if(it.gameMode == GameMode.SPECTATOR){
                        it.sendMessage("${if(player.gameMode == GameMode.SPECTATOR) "&8Spectator" else ""} &7${player.name}&8: &7${event.message.substring(1)}".colorize())
                    }
                }
            } else {
                Bukkit.broadcastMessage("&3${player.name}&8: &7${event.message}".colorize())
                return
            }
        }

    }
}