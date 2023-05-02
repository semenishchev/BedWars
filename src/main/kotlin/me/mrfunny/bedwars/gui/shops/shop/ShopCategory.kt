package me.mrfunny.bedwars.gui.shops.shop

import me.mrfunny.bedwars.BedWars.Companion.addInteractionKey
import me.mrfunny.bedwars.util.ItemBuilder
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

enum class ShopCategory(val item: ItemStack) {
    BLOCKS(ItemBuilder(Material.TERRACOTTA).setName("&aBlocks").toItemStack().addInteractionKey("blocks")),
    MELEE(ItemBuilder(Material.DIAMOND_SWORD).setName("&aMelee").toItemStack().addInteractionKey("melee")),
    ARMOR(ItemBuilder(Material.DIAMOND_CHESTPLATE).setName("&aArmor").toItemStack().addInteractionKey("armor")),
    TOOLS(ItemBuilder(Material.STONE_PICKAXE).setName("&aTools").toItemStack().addInteractionKey("tools")),
    RANGED(ItemBuilder(Material.BOW).setName("&aRanged").toItemStack().addInteractionKey("ranged")),
    POTIONS(ItemBuilder(Material.POTION).setName("&aPotions").toItemStack().addInteractionKey("potions")),
    UTILITY(ItemBuilder(Material.TNT).setName("&aUtils").toItemStack().addInteractionKey("utils"));

}