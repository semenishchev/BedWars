package me.mrfunny.bedwars.worlds.islands.teamupgrades

import org.bukkit.Material

class GeneratorUpgrade : UpgradeItem(
    5,
    "Island Generator",
    Material.FURNACE,
    "Allows spawning more resources on your islands",
    MaxLevel.FOUR,
    4, 8, 16, 32
) {
}