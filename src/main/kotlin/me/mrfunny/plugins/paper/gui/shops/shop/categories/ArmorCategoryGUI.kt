package me.mrfunny.plugins.paper.gui.shops.shop.categories

import me.mrfunny.plugins.paper.gamemanager.GameManager
import me.mrfunny.plugins.paper.gui.PrimaryShopGUI
import me.mrfunny.plugins.paper.gui.shops.shop.ShopCategory
import me.mrfunny.plugins.paper.gui.shops.shop.ShopItem
import me.mrfunny.plugins.paper.util.ItemBuilder
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class ArmorCategoryGUI(gameManager: GameManager, player: Player): PrimaryShopGUI(gameManager, player, arrayOf(
    ShopItem(10, Material.GHAST_TEAR, ShopCategory.ARMOR, ItemBuilder(Material.IRON_BOOTS).toItemStack()),
    ShopItem(12, Material.GOLD_NUGGET, ShopCategory.ARMOR, ItemBuilder(Material.DIAMOND_BOOTS).toItemStack()),
    ShopItem(6, Material.FERMENTED_SPIDER_EYE, ShopCategory.ARMOR, ItemBuilder(Material.NETHERITE_BOOTS).toItemStack()),
    ShopItem(14, Material.FERMENTED_SPIDER_EYE, ShopCategory.RANGED, ItemStack(Material.ELYTRA)),
    ShopItem(1, Material.FERMENTED_SPIDER_EYE, ShopCategory.RANGED, ItemBuilder(Material.FIREWORK_ROCKET, 4).toItemStack()),
))