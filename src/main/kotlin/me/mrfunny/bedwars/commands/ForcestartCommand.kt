package me.mrfunny.bedwars.commands

import me.mrfunny.bedwars.game.GameManager
import me.mrfunny.bedwars.game.GameState
import me.mrfunny.bedwars.tasks.GameStartingTask
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class ForcestartCommand(private val gameManager: GameManager): CommandExecutor {
    override fun onCommand(sender: CommandSender, p1: Command, p2: String, args: Array<out String>): Boolean {
        if(!sender.hasPermission("bedwars.admin")) return true

        gameManager.state = GameState.STARTING
        gameManager.gameStartingTask = GameStartingTask(gameManager, 10)
        return true
    }


}