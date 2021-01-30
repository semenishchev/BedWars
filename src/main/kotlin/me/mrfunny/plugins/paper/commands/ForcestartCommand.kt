package me.mrfunny.plugins.paper.commands

import me.mrfunny.plugins.paper.gamemanager.GameManager
import me.mrfunny.plugins.paper.gamemanager.GameState
import me.mrfunny.plugins.paper.tasks.GameStartingTask
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.event.EventHandler

class ForcestartCommand(private val gameManager: GameManager): CommandExecutor {
    override fun onCommand(sender: CommandSender, p1: Command, p2: String, args: Array<out String>): Boolean {
        if(!sender.hasPermission("bedwars.admin")) return true

        gameManager.state = GameState.STARTING
        gameManager.gameStartingTask = GameStartingTask(gameManager, 10)
        return true
    }


}