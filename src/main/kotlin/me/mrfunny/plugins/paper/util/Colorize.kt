package me.mrfunny.plugins.paper.util

import org.bukkit.ChatColor

class Colorize {

    companion object{
        fun c(s: String): String{
            return ChatColor.translateAlternateColorCodes('&', s)
        }
    }
}