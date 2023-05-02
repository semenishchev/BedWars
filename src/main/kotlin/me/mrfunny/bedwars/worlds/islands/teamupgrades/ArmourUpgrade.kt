package me.mrfunny.bedwars.worlds.islands.teamupgrades

import org.bukkit.Material

class ArmourUpgrade : UpgradeItem(
    3,
    "Armor",
    Material.DIAMOND_CHESTPLATE,
    "Improves protection level of your armor",
    MaxLevel.FOUR,
    2, 4, 8, 16
) {
}