package me.mrfunny.plugins.paper.tasks

import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable

class PlatformWaitTask(val player: Player?, vararg val platform: Location): BukkitRunnable(){

    var counter = 15

    override fun run() {
        if(counter == 0){
            this.cancel()
            return
        }
        if(player != null){
            if(player.gameMode != GameMode.SPECTATOR){
                player.sendTitle("", "$counter", 0, 20, 20)
            }
        }

        counter--
    }

    override fun cancel() {
        super.cancel()
        platform.forEach {
            if(!it.block.hasMetadata("placed") && it.block.type.name.contains("GLASS")){
                it.block.type = Material.AIR
            }
        }
    }

}