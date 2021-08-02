package me.mrfunny.plugins.paper.gui.shops.shop.categories

import me.mrfunny.plugins.paper.gamemanager.GameManager
import me.mrfunny.plugins.paper.gui.PrimaryShopGUI
import me.mrfunny.plugins.paper.gui.shops.shop.ShopCategory
import me.mrfunny.plugins.paper.gui.shops.shop.ShopItem
import me.mrfunny.plugins.paper.util.ItemBuilder
import me.mrfunny.plugins.paper.worlds.Island
import me.mrfunny.plugins.paper.worlds.generators.GeneratorType
import org.bukkit.Material
import org.bukkit.entity.Player

class BlockCategoryGUI(gameManager: GameManager, player: Player, val island: Island): PrimaryShopGUI(gameManager, player, arrayOf(
    ShopItem(
        4,
        GeneratorType.IRON.getMaterial(),
        ShopCategory.BLOCKS,
        ItemBuilder(island.color.concreteMaterial(), 16).toItemStack(),
    ),
    ShopItem(
        6,
        GeneratorType.GOLD.getMaterial(),
        ShopCategory.BLOCKS,
        ItemBuilder(Material.OAK_PLANKS, 12).toItemStack(),
    ),
    ShopItem(
        24,
        GeneratorType.IRON.getMaterial(),
        ShopCategory.BLOCKS,
        ItemBuilder(Material.END_STONE, 16).toItemStack(),
    ),
    ShopItem(10, Material.FERMENTED_SPIDER_EYE, ShopCategory.BLOCKS, ItemBuilder(Material.OBSIDIAN, 2).toItemStack()),
    ShopItem(3, GeneratorType.IRON.getMaterial(), ShopCategory.BLOCKS, ItemBuilder(Material.LADDER, 6).toItemStack()),
    ShopItem(2, GeneratorType.GOLD.getMaterial(), ShopCategory.BLOCKS, ItemBuilder(Material.CHEST).toItemStack()),
))

