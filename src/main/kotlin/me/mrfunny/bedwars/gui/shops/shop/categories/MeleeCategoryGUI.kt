package me.mrfunny.bedwars.gui.shops.shop.categories

import me.mrfunny.bedwars.game.GameManager
import me.mrfunny.bedwars.gui.PrimaryShopGUI
import me.mrfunny.bedwars.gui.shops.shop.ShopCategory
import me.mrfunny.bedwars.gui.shops.shop.ShopItem
import me.mrfunny.bedwars.util.ItemBuilder
import me.mrfunny.bedwars.worlds.generators.GeneratorType
import org.bukkit.Material
import org.bukkit.entity.Player

class MeleeCategoryGUI(gameManager: GameManager, player: Player, ironSword: ItemBuilder, diamondSword: ItemBuilder, netheriteSword: ItemBuilder): PrimaryShopGUI(gameManager, player, arrayOf(
    ShopItem(10, GeneratorType.IRON.getMaterial(), ShopCategory.MELEE, ironSword.toItemStack()),
    ShopItem(7, GeneratorType.GOLD.getMaterial(), ShopCategory.MELEE, diamondSword.toItemStack()),
    ShopItem(3, Material.FERMENTED_SPIDER_EYE, ShopCategory.MELEE, netheriteSword.toItemStack()),
    ShopItem(15, GeneratorType.IRON.getMaterial(), ShopCategory.MELEE, ItemBuilder(Material.SHIELD).toItemStack())))