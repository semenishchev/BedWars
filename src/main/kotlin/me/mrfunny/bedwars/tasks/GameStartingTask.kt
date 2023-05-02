package me.mrfunny.bedwars.tasks

import me.mrfunny.bedwars.BedWars.Companion.colorize
import me.mrfunny.bedwars.game.GameManager
import me.mrfunny.bedwars.game.GameState
import me.mrfunny.bedwars.players.PlayerData
import me.mrfunny.bedwars.util.Colorize
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.scheduler.BukkitRunnable

class GameStartingTask(private val gameManager: GameManager, private var timer: Int): BukkitRunnable() {

    init {
        gameManager.bossBar.progress = 1.0
    }

    override fun run() {
        if(timer <= 0){
            gameManager.state = GameState.ACTIVE
            Bukkit.getOnlinePlayers().forEach {
                it.playSound(it.location, Sound.BLOCK_NOTE_BLOCK_PLING, 1.0.toFloat(), 1.0.toFloat())
                it.sendTitle("&l&n&aGO".colorize(), "", 0, 20, 20)
            }
        }
        gameManager.bossBar.progress = timer / 20.0
        gameManager.bossBar.setTitle("&aGame starting in $timer".colorize())
        if((timer <= 5 || timer == 20) && timer != 0){
            for (data in PlayerData.values()) {
                data.getPlayer()?.also {
                    it.sendMessage("Game starts in $timer")
                    it.playSound(it.location, Sound.BLOCK_NOTE_BLOCK_PLING, 1.0.toFloat(), 1.0.toFloat())
                    it.sendTitle(Colorize.c("${if (timer > 10) "&a" else if(timer > 3) "&e" else "&c"}$timer"), "", 0, 20, 20)
                }
            }
        }

        timer--
    }

    override fun cancel() {
        super.cancel()
        gameManager.bossBar.removeAll()
    }
}