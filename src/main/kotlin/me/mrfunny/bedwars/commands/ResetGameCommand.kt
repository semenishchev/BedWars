package me.mrfunny.bedwars.commands

import me.mrfunny.bedwars.game.GameManager
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class ResetGameCommand: CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if(sender.hasPermission("bedwars.admin")){
            GameManager.isLagged = true
        }
        return true
    }
}