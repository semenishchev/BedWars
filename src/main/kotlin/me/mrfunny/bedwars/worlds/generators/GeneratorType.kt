package me.mrfunny.bedwars.worlds.generators

import org.bukkit.Material

enum class GeneratorType {
    IRON,GOLD,DIAMOND,EMERALD;

    fun getName(): String {
        return when(this){
            IRON -> "&fIron"
            GOLD -> "&6Gold"
            DIAMOND -> "&bYou found PEPEGA"
            EMERALD -> "&4Ruby"
        }
    }

    fun getMaterial(): Material {
        return when(this){
            IRON -> Material.GHAST_TEAR
            GOLD -> Material.GOLD_NUGGET
            DIAMOND -> Material.DIAMOND
            EMERALD -> Material.FERMENTED_SPIDER_EYE
        }
    }
}