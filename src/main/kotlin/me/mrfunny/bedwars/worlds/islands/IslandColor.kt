package me.mrfunny.bedwars.worlds.islands

import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import org.bukkit.ChatColor
import org.bukkit.Color
import org.bukkit.DyeColor
import org.bukkit.Material

enum class IslandColor {
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
        return "${caps[0].uppercaseChar()}${caps.substring(1).lowercase()}"
    }

    fun getChatColor(): ChatColor{
        if(this == PINK){
            return ChatColor.LIGHT_PURPLE
        } else if(this == ORANGE){
            return ChatColor.GOLD
        }
        return ChatColor.valueOf(this.toString())
    }

    fun getColor(): Color{
        return when(this){
            GREEN -> Color.LIME
            RED -> Color.RED
            BLUE -> Color.BLUE
            YELLOW -> Color.YELLOW
            AQUA -> Color.AQUA
            WHITE -> Color.WHITE
            GRAY -> Color.GRAY
            BLACK -> Color.BLACK
            ORANGE -> Color.ORANGE
            PINK -> Color.FUCHSIA
        }
    }

    fun dyeColor(): DyeColor{
        return when(this){
            GREEN -> DyeColor.LIME
            RED -> DyeColor.RED
            BLUE -> DyeColor.BLUE
            YELLOW -> DyeColor.YELLOW
            AQUA -> DyeColor.LIGHT_BLUE
            WHITE -> DyeColor.WHITE
            GRAY -> DyeColor.GRAY
            BLACK -> DyeColor.BLACK
            ORANGE -> DyeColor.ORANGE
            PINK -> DyeColor.PINK
        }
    }

    fun woolMaterial(): Material{
        return when(this){
            GREEN -> Material.LIME_WOOL
            RED -> Material.RED_WOOL
            BLUE -> Material.BLUE_WOOL
            YELLOW -> Material.YELLOW_WOOL
            AQUA -> Material.LIGHT_BLUE_WOOL
            WHITE -> Material.WHITE_WOOL
            GRAY -> Material.GRAY_WOOL
            BLACK -> Material.BLACK_WOOL
            ORANGE -> Material.ORANGE_WOOL
            PINK -> Material.PINK_WOOL
        }
    }

    fun concreteMaterial(): Material {
        return when(this){
            GREEN -> Material.LIME_CONCRETE
            RED -> Material.RED_CONCRETE
            BLUE -> Material.BLUE_CONCRETE
            YELLOW -> Material.YELLOW_CONCRETE
            AQUA -> Material.LIGHT_BLUE_CONCRETE
            WHITE -> Material.WHITE_CONCRETE
            GRAY -> Material.GRAY_CONCRETE
            BLACK -> Material.BLACK_CONCRETE
            ORANGE -> Material.ORANGE_CONCRETE
            PINK -> Material.PINK_CONCRETE
        }
    }

    fun toNamed(): TextColor {
        return when(this) {
            GREEN -> NamedTextColor.GREEN
            RED -> NamedTextColor.RED
            BLUE -> NamedTextColor.BLUE
            YELLOW -> NamedTextColor.YELLOW
            AQUA -> NamedTextColor.AQUA
            WHITE -> NamedTextColor.WHITE
            GRAY -> NamedTextColor.GRAY
            BLACK -> NamedTextColor.GOLD
            ORANGE -> TextColor.color(Color.ORANGE.asRGB())
            PINK -> NamedTextColor.LIGHT_PURPLE
        }
    }

    fun glassMaterial(): Material {
        return when(this){
            GREEN -> Material.LIME_STAINED_GLASS
            RED -> Material.RED_STAINED_GLASS
            BLUE -> Material.BLUE_STAINED_GLASS
            YELLOW -> Material.YELLOW_STAINED_GLASS
            AQUA -> Material.LIGHT_BLUE_STAINED_GLASS
            WHITE -> Material.WHITE_STAINED_GLASS
            GRAY -> Material.LIME_STAINED_GLASS
            BLACK -> Material.BLACK_STAINED_GLASS
            ORANGE -> Material.ORANGE_STAINED_GLASS
            PINK -> Material.PINK_STAINED_GLASS
        }
    }

    fun russianName(): String{ // метод вовзращает только основу слова, поскольку в разных ситуациях оно разное
        return when(this){     // краснАЯ, краснЫХ, зеленЫХ, зеленАЯ
            GREEN -> "Зелен"
            RED -> "Красн"
            BLUE -> "Син"
            YELLOW -> "Желт"
            AQUA -> "Голуб"
            WHITE -> "Бел"
            GRAY -> "Сер"
            BLACK -> "Черн"
            ORANGE -> "Оранж"
            PINK -> "Розов"
        }
    }
}