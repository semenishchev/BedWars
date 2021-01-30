package me.mrfunny.plugins.paper.gui.shops.shop

import me.mrfunny.plugins.paper.util.ItemBuilder
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

enum class ShopCategory(val item: ItemStack) {
    BLOCKS(ItemBuilder(Material.TERRACOTTA).setName("&aBlocks").toItemStack()),
    MELEE(ItemBuilder(Material.DIAMOND_SWORD).setName("&aMelee").toItemStack()),
    ARMOR(ItemBuilder(Material.DIAMOND_CHESTPLATE).setName("&aArmor").toItemStack()),
    TOOLS(ItemBuilder(Material.STONE_PICKAXE).setName("&aTools").toItemStack()),
    RANGED(ItemBuilder(Material.BOW).setName("&aRanged").toItemStack()),
    POTIONS(ItemBuilder(Material.POTION).setName("&aPotions").toItemStack()),
    UTILITY(ItemBuilder(Material.TNT).setName("&aUtils").toItemStack());

}