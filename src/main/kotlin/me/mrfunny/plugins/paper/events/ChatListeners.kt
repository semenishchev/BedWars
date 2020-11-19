package me.mrfunny.plugins.paper.events

import me.mrfunny.plugins.paper.gamemanager.GameManager
import me.mrfunny.plugins.paper.util.Colorize
import me.mrfunny.plugins.paper.worlds.Island
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent

class ChatListeners(private val gameManager: GameManager): Listener {

    @EventHandler
    fun onChat(event: AsyncPlayerChatEvent){
        val player: Player = event.player

        val prefix: StringBuilder = StringBuilder()

        if(player.hasPermission("bedwars.admin")){
            prefix.append("&c&lADMIN &r")
        }

        if(player.gameMode == GameMode.SPECTATOR){
            prefix.append("&6&lSPECTATOR &r")
        }

        val island: Island? = gameManager.world.getIslandForPlayer(player)
        var islandColorCode = "&3"
        if(island != null){
            islandColorCode = island.color.getChatColor().char.toString()
            prefix.append(islandColorCode).append("&l").append(island.color.formattedName()).append(" ")
        } else {
            prefix.append(islandColorCode)
        }

        event.format = Colorize.c("$prefix&r$islandColorCode%s &7>") + "%s"
    }
}