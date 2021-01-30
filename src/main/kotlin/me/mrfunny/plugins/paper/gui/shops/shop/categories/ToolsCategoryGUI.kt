package me.mrfunny.plugins.paper.gui.shops.shop.categories

import me.mrfunny.plugins.paper.gamemanager.GameManager
import me.mrfunny.plugins.paper.gui.PrimaryShopGUI
import me.mrfunny.plugins.paper.gui.shops.shop.ShopCategory
import me.mrfunny.plugins.paper.gui.shops.shop.ShopItem
import me.mrfunny.plugins.paper.util.ItemBuilder
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player

class ToolsCategoryGUI(gameManager: GameManager, player: Player, wooden: ItemBuilder, iron: ItemBuilder, diamond: ItemBuilder, netherite: ItemBuilder): PrimaryShopGUI(gameManager, player, arrayOf(
    ShopItem(20, Material.GHAST_TEAR, ShopCategory.TOOLS, ItemBuilder(Material.SHEARS).toItemStack()),
    ShopItem(10, Material.GHAST_TEAR, ShopCategory.TOOLS, wooden.setUnbreakable(true).toItemStack()),
    ShopItem(15, Material.GHAST_TEAR, ShopCategory.TOOLS, iron.setUnbreakable(true).toItemStack()),
    ShopItem(5, Material.GOLD_NUGGET, ShopCategory.TOOLS, diamond.setUnbreakable(true).toItemStack()),
    ShopItem(3, Material.FERMENTED_SPIDER_EYE, ShopCategory.TOOLS, netherite.setUnbreakable(true).toItemStack()),
    ShopItem(15, Material.GHAST_TEAR, ShopCategory.TOOLS, ItemBuilder(Material.WOODEN_AXE).addEnchant(Enchantment.DIG_SPEED, 2).toItemStack())))