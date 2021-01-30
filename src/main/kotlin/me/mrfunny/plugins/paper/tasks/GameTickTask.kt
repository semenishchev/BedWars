package me.mrfunny.plugins.paper.tasks

import me.mrfunny.plugins.paper.BedWars.Companion.colorize
import me.mrfunny.plugins.paper.gamemanager.GameManager
import org.bukkit.scheduler.BukkitRunnable

class GameTickTask(private val gameManager: GameManager): BukkitRunnable() {

    var currentSecond: Int = 0

    override fun run() {
        currentSecond++
        val currentMinute = (currentSecond % 3600) / 60
        gameManager.bossBar.setTitle("&c${45 - currentMinute}&f minutes left until the end of the game".colorize())

        gameManager.world.tick(currentSecond)

        if(currentMinute != 0){
            gameManager.bossBar.progress = (45.0 - currentMinute) / 45.0
        } else {
            gameManager.bossBar.progress = 1.0
        }

    }
}