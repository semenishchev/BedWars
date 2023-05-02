package me.mrfunny.bedwars.gui.shops.shop.categories

import me.mrfunny.bedwars.game.GameManager
import me.mrfunny.bedwars.gui.PrimaryShopGUI
import me.mrfunny.bedwars.gui.shops.shop.ShopCategory
import me.mrfunny.bedwars.gui.shops.shop.ShopItem
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class PotionCategoryGUI(gameManager: GameManager, player: Player, invisibilityPotion: ItemStack, speedPotion: ItemStack, jumpPotion: ItemStack, strengthPotion: ItemStack): PrimaryShopGUI(gameManager, player, arrayOf(
    ShopItem(2, Material.FERMENTED_SPIDER_EYE, ShopCategory.POTIONS, invisibilityPotion),
    ShopItem(1, Material.FERMENTED_SPIDER_EYE, ShopCategory.POTIONS, speedPotion),
    ShopItem(1, Material.FERMENTED_SPIDER_EYE, ShopCategory.POTIONS, jumpPotion),
    ShopItem(3, Material.FERMENTED_SPIDER_EYE, ShopCategory.POTIONS, strengthPotion),
))