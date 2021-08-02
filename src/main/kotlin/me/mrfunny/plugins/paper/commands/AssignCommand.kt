package me.mrfunny.plugins.paper.commands

import me.mrfunny.plugins.paper.gamemanager.GameManager
import me.mrfunny.plugins.paper.worlds.IslandColor
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.EnumUtils
import org.bukkit.entity.Player

class AssignCommand(private val gameManager: GameManager): CommandExecutor {
    override fun onCommand(sender: CommandSender, p1: Command, p2: String, args: Array<out String>): Boolean {
        if(sender.hasPermission("bedwars.admin")){
            if(args.size == 2){
                if(Bukkit.getPlayer(args[0]) != null && EnumUtils.isValidEnum(IslandColor::class.java, args[1])){
                    val player: Player = Bukkit.getPlayer(args[0])!!
                    if(gameManager.world.getIslandForPlayer(player) != null){
                        gameManager.world.getIslandForPlayer(player)!!.removeMember(player)
                    }
                    gameManager.getIslandByColor(IslandColor.valueOf(args[1])).addMember(player)
                }
            }
        }
        return true
    }
}