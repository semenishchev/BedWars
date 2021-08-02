package me.mrfunny.plugins.paper.gui.shops.shop.categories

import me.mrfunny.plugins.paper.gamemanager.GameManager
import me.mrfunny.plugins.paper.gui.PrimaryShopGUI
import me.mrfunny.plugins.paper.gui.shops.shop.ShopCategory
import me.mrfunny.plugins.paper.gui.shops.shop.ShopItem
import me.mrfunny.plugins.paper.players.PlayerData
import me.mrfunny.plugins.paper.util.ItemBuilder
import me.mrfunny.plugins.paper.worlds.generators.GeneratorType
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class UtilsCategoryGUI(gameManager: GameManager, data: PlayerData): PrimaryShopGUI(gameManager, data.player, arrayOf(
    ShopItem(5, GeneratorType.GOLD.getMaterial(), ShopCategory.UTILITY, ItemBuilder(Material.BLAZE_ROD).setName(gameManager.itemsLocalization.get().getString("rescue-platform.${if(data.isRussian()) "ru" else "en"}.name")!!).toItemStack(), *gameManager.itemsLocalization.get()
        .getStringList("rescue-platform.${if(data.isRussian()) "ru" else "en"}.description").toTypedArray()),
    ShopItem(3, GeneratorType.GOLD.getMaterial(), ShopCategory.UTILITY, ItemBuilder(Material.GUNPOWDER).setName(gameManager.itemsLocalization.get().getString("home-teleporter.${if(data.isRussian()) "ru" else "en"}.name")!!).toItemStack(), *gameManager.itemsLocalization.get()
        .getStringList("home-teleporter.${if(data.isRussian()) "ru" else "en"}.description").toTypedArray()),
    ShopItem(12, GeneratorType.IRON.getMaterial(), ShopCategory.UTILITY, ItemBuilder(Material.SNOWBALL).setName(gameManager.itemsLocalization.get().getString("building-snowball.${if(data.isRussian()) "ru" else "en"}.name")!!).toItemStack(), *gameManager.itemsLocalization.get()
        .getStringList("building-snowball.${if(data.isRussian()) "ru" else "en"}.description").toTypedArray()),
    ShopItem(20, GeneratorType.GOLD.getMaterial(), ShopCategory.UTILITY, ItemBuilder(Material.TOTEM_OF_UNDYING).toItemStack()),
    ShopItem(10, GeneratorType.GOLD.getMaterial(), ShopCategory.UTILITY, ItemBuilder(Material.FISHING_ROD).setUnbreakable(false).setName(gameManager.itemsLocalization.get().getString("grappling-hook.${if(data.isRussian()) "ru" else "en"}.name")!!).toItemStack(), *gameManager.itemsLocalization.get()
        .getStringList("grappling-hook.${if(data.isRussian()) "ru" else "en"}.description").toTypedArray()),
    ShopItem(40, GeneratorType.IRON.getMaterial(), ShopCategory.UTILITY, ItemBuilder(Material.FIRE_CHARGE).toItemStack()),
    ShopItem(16, GeneratorType.IRON.getMaterial(), ShopCategory.UTILITY, ItemBuilder(Material.COBWEB, 4).toItemStack()),
    ShopItem(3, GeneratorType.GOLD.getMaterial(), ShopCategory.UTILITY, ItemBuilder(Material.GOLDEN_APPLE).toItemStack()),
    ShopItem(3, Material.FERMENTED_SPIDER_EYE, ShopCategory.UTILITY, ItemBuilder(Material.ENDER_PEARL).toItemStack()),
    ShopItem(6, GeneratorType.IRON.getMaterial(), ShopCategory.UTILITY, ItemBuilder(Material.COMPASS).setName("&aGPS tracker").toItemStack(), *gameManager.itemsLocalization.get()
        .getStringList("gps-tracker.${if(data.isRussian()) "ru" else "en"}.description").toTypedArray()),
    ShopItem(3, GeneratorType.GOLD.getMaterial(), ShopCategory.UTILITY, ItemStack(Material.WATER_BUCKET)),
    ShopItem(1, Material.FERMENTED_SPIDER_EYE, ShopCategory.UTILITY, ItemBuilder(Material.TNT).setName(gameManager.itemsLocalization.get().getString("flying-tnt.${if(data.isRussian()) "ru" else "en"}.name")!!).toItemStack(), *gameManager.itemsLocalization.get()
        .getStringList("flying-tnt.${if(data.isRussian()) "ru" else "en"}.description").toTypedArray()),
))