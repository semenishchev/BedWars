package me.mrfunny.bedwars.gui.shops.shop.categories

import me.mrfunny.bedwars.game.GameManager
import me.mrfunny.bedwars.gui.PrimaryShopGUI
import me.mrfunny.bedwars.gui.shops.shop.ShopCategory
import me.mrfunny.bedwars.gui.shops.shop.ShopItem
import me.mrfunny.bedwars.util.ItemBuilder
import me.mrfunny.bedwars.worlds.generators.GeneratorType
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player

class RandedCategoryGUI(gameManager: GameManager, player: Player): PrimaryShopGUI(gameManager, player, arrayOf(
    ShopItem(10, GeneratorType.GOLD.getMaterial(), ShopCategory.RANGED, ItemBuilder(
    Material.BOW).setUnbreakable(true).toItemStack()),
    ShopItem(2, Material.FERMENTED_SPIDER_EYE, ShopCategory.RANGED, ItemBuilder(Material.BOW).setUnbreakable(true).addEnchant(Enchantment.ARROW_DAMAGE, 1).toItemStack()),
    ShopItem(6, Material.FERMENTED_SPIDER_EYE, ShopCategory.RANGED, ItemBuilder(Material.BOW).setUnbreakable(false).addEnchant(Enchantment.ARROW_KNOCKBACK, 1).addEnchant(Enchantment.ARROW_DAMAGE, 2).toItemStack()),
    ShopItem(10, Material.FERMENTED_SPIDER_EYE, ShopCategory.RANGED, ItemBuilder(Material.CROSSBOW).setUnbreakable(true)
        .addUnsafeEnchantment(Enchantment.QUICK_CHARGE, 5).addEnchant(Enchantment.ARROW_DAMAGE, 1)
        .addEnchant(Enchantment.MULTISHOT, 1)
        .toItemStack()),
    ShopItem(8, GeneratorType.IRON.getMaterial(), ShopCategory.RANGED, ItemBuilder(Material.ARROW, 8).toItemStack()),
))