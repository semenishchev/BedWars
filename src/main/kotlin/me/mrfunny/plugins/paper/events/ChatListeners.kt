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
            prefix.append("&c&lADMIN ")
        }

        if(player.gameMode == GameMode.SPECTATOR){
            prefix.append("&6&lSPECTATOR ")
        }

        val island: Island? = gameManager.world.getIslandForPlayer(player)
        if(island != null){
            prefix.append(island.color.getChatColor()).append("&l").append(island.color.formattedName()).append(" ")
        } else {
            prefix.append("&3")
        }

        event.format = Colorize.c("$prefix%s &7>") + "%s"
    }
}