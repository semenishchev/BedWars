package me.mrfunny.plugins.paper.gui.shops.teamupgrades

import org.bukkit.inventory.ItemStack

// todo: make upgrades with this system
abstract class UpgradeItem {
    abstract val upgradeName: String
    abstract val description: String
    abstract val displayItem: ItemStack
}