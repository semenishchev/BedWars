package me.mrfunny.bedwars.commands

import me.mrfunny.bedwars.BedWars.Companion.colorize
import me.mrfunny.bedwars.game.GameManager
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class BetatestCommand(private val gameManager: GameManager): CommandExecutor {
    override fun onCommand(sender: CommandSender, p1: Command, p2: String, args: Array<out String>): Boolean {
        if(!sender.hasPermission("bedwars.admin")){
            sender.sendMessage("&c[BedWars] You have not enough permissions for this".colorize())
            return true
        }

        sender.sendMessage("&a[BedWars] Beta test game is set to ${!gameManager.isBetatest}")
        gameManager.isBetatest = !gameManager.isBetatest

        return true
    }
}