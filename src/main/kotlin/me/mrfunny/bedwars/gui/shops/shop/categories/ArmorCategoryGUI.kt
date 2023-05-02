package me.mrfunny.bedwars.gui.shops.shop.categories

import me.mrfunny.bedwars.game.GameManager
import me.mrfunny.bedwars.gui.PrimaryShopGUI
import me.mrfunny.bedwars.gui.shops.shop.ShopCategory
import me.mrfunny.bedwars.gui.shops.shop.ShopItem
import me.mrfunny.bedwars.util.ItemBuilder
import me.mrfunny.bedwars.worlds.generators.GeneratorType
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class ArmorCategoryGUI(gameManager: GameManager, player: Player): PrimaryShopGUI(gameManager, player, arrayOf(
    ShopItem(10, GeneratorType.IRON.getMaterial(), ShopCategory.ARMOR, ItemBuilder(Material.IRON_BOOTS).toItemStack()),
    ShopItem(12, GeneratorType.GOLD.getMaterial(), ShopCategory.ARMOR, ItemBuilder(Material.DIAMOND_BOOTS).toItemStack()),
    ShopItem(6, Material.FERMENTED_SPIDER_EYE, ShopCategory.ARMOR, ItemBuilder(Material.NETHERITE_BOOTS).toItemStack()),
    ShopItem(14, Material.FERMENTED_SPIDER_EYE, ShopCategory.RANGED, ItemStack(Material.ELYTRA)),
    ShopItem(1, Material.FERMENTED_SPIDER_EYE, ShopCategory.RANGED, ItemBuilder(Material.FIREWORK_ROCKET, 4).toItemStack()),
))