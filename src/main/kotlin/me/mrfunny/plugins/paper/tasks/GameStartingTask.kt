package me.mrfunny.plugins.paper.tasks

import me.mrfunny.plugins.paper.BedWars.Companion.colorize
import me.mrfunny.plugins.paper.gamemanager.GameManager
import me.mrfunny.plugins.paper.gamemanager.GameState
import me.mrfunny.plugins.paper.players.PlayerData
import me.mrfunny.plugins.paper.util.Colorize
import org.bukkit.Bukkit
import org.bukkit.Instrument
import org.bukkit.Note
import org.bukkit.Sound
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.boss.BossBar
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
        gameManager.bossBar.setTitle(Colorize.c("&aGame starting in $timer"))
        if((timer <= 5 || timer == 20) && timer != 0){
            for(it in Bukkit.getOnlinePlayers()){
                if(PlayerData.PLAYERS[it.uniqueId] == null)continue
                it.sendMessage("&a${if(PlayerData.PLAYERS[it.uniqueId]!!.isRussian()) "Игра начнется через" else "Game starting in"} $timer".colorize())
            }
            Bukkit.getOnlinePlayers().forEach {
                it.playSound(it.location, Sound.BLOCK_NOTE_BLOCK_PLING, 1.0.toFloat(), 1.0.toFloat())
                it.sendTitle(Colorize.c("${if (timer > 10) "&a" else if(timer > 3) "&e" else "&c"}$timer"), "", 0, 20, 20)
            }
        }

        timer--
    }

    override fun cancel() {
        super.cancel()
        gameManager.bossBar.removeAll()
    }
}