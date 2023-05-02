package me.mrfunny.bedwars.players

import org.bukkit.Material

enum class ToolLevel {
    WOODEN, IRON, DIAMOND, NETHERITE;

    fun toPic(): Material {
        return Material.valueOf(this.name + "_PICKAXE")
    }

    fun toAxe(): Material {
        return Material.valueOf(this.name + "_AXE")
    }
}