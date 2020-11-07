package me.mrfunny.plugins.paper.worlds

import org.bukkit.ChatColor
import org.bukkit.Material

enum class TeamColor {
    GREEN,
    RED,
    BLUE,
    YELLOW,
    AQUA,
    WHITE,
    GRAY,
    BLACK,
    ORANGE,
    PINK;

    fun formattedName(): String{
        val caps: String = this.toString()
        return "${caps[0].toUpperCase()}${caps.substring(1).toLowerCase()}"
    }

    fun getChatColor(): ChatColor{
        if(this == PINK){
            return ChatColor.LIGHT_PURPLE
        } else if(this == ORANGE){
            return ChatColor.GOLD
        }
        return ChatColor.valueOf(this.toString())
    }

    fun woolMaterial(): Material{
        return when(this){
            GREEN -> Material.LIME_WOOL
            RED -> Material.RED_WOOL
            BLUE -> Material.BLUE_WOOL
            YELLOW -> Material.YELLOW_WOOL
            AQUA -> Material.CYAN_WOOL
            WHITE -> Material.WHITE_WOOL
            GRAY -> Material.GRAY_WOOL
            BLACK -> Material.BLACK_WOOL
            ORANGE -> Material.ORANGE_WOOL
            PINK -> Material.PINK_WOOL
        }
    }
}