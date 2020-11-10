package me.mrfunny.plugins.paper.tasks

import me.mrfunny.plugins.paper.manager.GameManager
import me.mrfunny.plugins.paper.manager.GameState
import me.mrfunny.plugins.paper.util.Colorize
import org.bukkit.Bukkit
import org.bukkit.Instrument
import org.bukkit.Note
import org.bukkit.Sound
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.boss.BossBar
import org.bukkit.scheduler.BukkitRunnable

class GameStartingTask(private val gameManager: GameManager): BukkitRunnable() {

    private var timer: Int = 20
    private val bossBar: BossBar = Bukkit.createBossBar("Game starting in $timer...", BarColor.GREEN, BarStyle.SEGMENTED_20)

    init {
        bossBar.progress = 1.0

        Bukkit.getOnlinePlayers().forEach {
            bossBar.addPlayer(it)
        }
    }

    override fun run() {
        if(timer <= 0){
            gameManager.state = GameState.ACTIVE
        }
        bossBar.progress = timer / 20.0
        bossBar.setTitle(Colorize.c("&aStarting in $timer"))
        if(timer <= 5 || timer == 20){
            Bukkit.broadcastMessage(Colorize.c("&aGame starting in $timer second${if(timer == 1) "" else "s"}"))
            Bukkit.getOnlinePlayers().forEach {
                it.playSound(it.location, Sound.BLOCK_NOTE_BLOCK_PLING, 1.0.toFloat(), 1.0.toFloat())
                it.sendTitle("Game starting", Colorize.c("&a$timer"), 0, 20, 20)
            }
        }

        timer--
    }

    override fun cancel(){
        super.cancel()
        bossBar.removeAll()
    }
}