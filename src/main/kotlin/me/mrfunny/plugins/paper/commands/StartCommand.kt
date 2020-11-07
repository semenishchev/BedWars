package me.mrfunny.plugins.paper.commands

import me.mrfunny.plugins.paper.manager.GameManager
import me.mrfunny.plugins.paper.manager.GameState
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class StartCommand(private val gameManager: GameManager) : CommandExecutor {

    override fun onCommand(sender: CommandSender, p1: Command, p2: String, args: Array<out String>): Boolean {

        if(sender.hasPermission("bedwars.admin")){
            gameManager.state = GameState.STARTING
        } else {
            sender.sendMessage("${ChatColor.RED}[BedWars] You don't have permissions do run this command!")
        }
        return true
    }

}