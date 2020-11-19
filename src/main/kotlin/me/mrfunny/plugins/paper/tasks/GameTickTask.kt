package me.mrfunny.plugins.paper.tasks

import me.mrfunny.plugins.paper.gamemanager.GameManager
import org.bukkit.scheduler.BukkitRunnable

class GameTickTask(private val gameManager: GameManager): BukkitRunnable() {

    override fun run() {
        gameManager.updateScoreboard()
    }
}