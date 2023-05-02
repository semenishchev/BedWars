package me.mrfunny.bedwars.gui.shops.shop.categories

import me.mrfunny.bedwars.game.GameManager
import me.mrfunny.bedwars.gui.PrimaryShopGUI
import me.mrfunny.bedwars.gui.shops.shop.ShopCategory
import me.mrfunny.bedwars.gui.shops.shop.ShopItem
import me.mrfunny.bedwars.players.PlayerData
import me.mrfunny.bedwars.util.ItemBuilder
import me.mrfunny.bedwars.worlds.generators.GeneratorType
import org.bukkit.Material
import org.bukkit.inventory.ItemStack


// FIXME: proper localisation messages
class UtilsCategoryGUI(gameManager: GameManager, data: PlayerData): PrimaryShopGUI(gameManager, data.getPlayer()!!, arrayOf(
    ShopItem(5, GeneratorType.GOLD.getMaterial(), ShopCategory.UTILITY, ItemBuilder(Material.BLAZE_ROD).setName("Rescue platform").toItemStack(), "Click while falling to save yourself"),
    ShopItem(3, GeneratorType.GOLD.getMaterial(), ShopCategory.UTILITY, ItemBuilder(Material.GUNPOWDER).setName("Home teleporter").toItemStack(), "Teleports you to your base's spawn point"),
    ShopItem(20, GeneratorType.IRON.getMaterial(), ShopCategory.UTILITY, ItemBuilder(Material.SNOWBALL).setName("Building snowball").toItemStack(), "Same as bridge egg on gaypixel"),
    ShopItem(20, GeneratorType.GOLD.getMaterial(), ShopCategory.UTILITY, ItemBuilder(Material.TOTEM_OF_UNDYING).toItemStack()),
    ShopItem(15, GeneratorType.GOLD.getMaterial(), ShopCategory.UTILITY, ItemBuilder(Material.FISHING_ROD).setUnbreakable(false).setName("Grappling hook").toItemStack(), "Pulls you to a location"),
    ShopItem(40, GeneratorType.IRON.getMaterial(), ShopCategory.UTILITY, ItemBuilder(Material.FIRE_CHARGE).toItemStack()),
    ShopItem(16, GeneratorType.IRON.getMaterial(), ShopCategory.UTILITY, ItemBuilder(Material.COBWEB, 4).toItemStack()),
    ShopItem(3, GeneratorType.GOLD.getMaterial(), ShopCategory.UTILITY, ItemBuilder(Material.GOLDEN_APPLE).toItemStack()),
    ShopItem(3, Material.FERMENTED_SPIDER_EYE, ShopCategory.UTILITY, ItemBuilder(Material.ENDER_PEARL).toItemStack()),
    ShopItem(6, GeneratorType.IRON.getMaterial(), ShopCategory.UTILITY, ItemBuilder(Material.COMPASS).setName("&aGPS tracker").toItemStack(), "Tracks nearest players"),
    ShopItem(3, GeneratorType.GOLD.getMaterial(), ShopCategory.UTILITY, ItemStack(Material.WATER_BUCKET)),
    ShopItem(1, Material.FERMENTED_SPIDER_EYE, ShopCategory.UTILITY, ItemBuilder(Material.TNT).setName("Flying TNT").toItemStack(), "It... flies?"),
))