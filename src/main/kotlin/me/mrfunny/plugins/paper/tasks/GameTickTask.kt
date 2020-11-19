package me.mrfunny.plugins.paper.tasks

import me.mrfunny.plugins.paper.gamemanager.GameManager
import org.bukkit.scheduler.BukkitRunnable

class GameTickTask(private val gameManager: GameManager): BukkitRunnable() {

    var currentSecond: Int = 0

    override fun run() {
        gameManager.updateScoreboard()
        currentSecond++

        gameManager.world.tick(currentSecond)
    }
}