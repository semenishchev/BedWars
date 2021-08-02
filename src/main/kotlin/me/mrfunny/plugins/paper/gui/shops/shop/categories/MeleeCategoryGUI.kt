package me.mrfunny.plugins.paper.gui.shops.shop.categories

import me.mrfunny.plugins.paper.gamemanager.GameManager
import me.mrfunny.plugins.paper.gui.PrimaryShopGUI
import me.mrfunny.plugins.paper.gui.shops.shop.ShopCategory
import me.mrfunny.plugins.paper.gui.shops.shop.ShopItem
import me.mrfunny.plugins.paper.util.ItemBuilder
import me.mrfunny.plugins.paper.worlds.generators.GeneratorType
import org.bukkit.Material
import org.bukkit.entity.Player

class MeleeCategoryGUI(gameManager: GameManager, player: Player, ironSword: ItemBuilder, diamondSword: ItemBuilder, netheriteSword: ItemBuilder): PrimaryShopGUI(gameManager, player, arrayOf(
    ShopItem(10, GeneratorType.IRON.getMaterial(), ShopCategory.MELEE, ironSword.toItemStack()),
    ShopItem(7, GeneratorType.GOLD.getMaterial(), ShopCategory.MELEE, diamondSword.toItemStack()),
    ShopItem(3, Material.FERMENTED_SPIDER_EYE, ShopCategory.MELEE, netheriteSword.toItemStack()),
    ShopItem(15, GeneratorType.IRON.getMaterial(), ShopCategory.MELEE, ItemBuilder(Material.SHIELD).toItemStack())))