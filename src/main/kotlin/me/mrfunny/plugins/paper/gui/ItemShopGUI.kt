package me.mrfunny.plugins.paper.gui

import me.mrfunny.plugins.paper.gamemanager.GameManager
import me.mrfunny.plugins.paper.gui.shops.shop.ShopCategory
import me.mrfunny.plugins.paper.util.Colorize
import me.mrfunny.plugins.paper.util.ItemBuilder
import me.mrfunny.plugins.paper.worlds.Island
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack
import me.mrfunny.plugins.paper.gui.shops.shop.ShopItem
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.potion.PotionData
import org.bukkit.potion.PotionEffectType
import org.bukkit.potion.PotionType

class ItemShopGUI(private val gameManager: GameManager, private val player: Player) : GUI {
    override val inventory: Inventory = Bukkit.createInventory(null, 54, "Shop")
    override val name: String = "Shop"
    private val items: Array<ShopItem>

    init {
        val island: Island = gameManager.world.getIslandForPlayer(player)!!
        items = arrayOf(
            ShopItem(4, Material.IRON_INGOT, ShopCategory.BLOCKS, ItemBuilder(island.color.woolMaterial(), 16).toItemStack(), 19, "&aНеплохие блоки, чтобы строится"),
            ShopItem(6, Material.GOLD_INGOT, ShopCategory.BLOCKS, ItemBuilder(Material.OAK_PLANKS, 12).toItemStack(), 28, "Неплох для застройки кровати"),
            ShopItem(24, Material.IRON_INGOT, ShopCategory.BLOCKS, ItemBuilder(Material.END_STONE, 16).toItemStack(), 37, "Защитит кровать от почти всех взрывов"),

            ShopItem(10, Material.IRON_INGOT, ShopCategory.MELEE, ItemBuilder(Material.IRON_SWORD).toItemStack(), 20),
            ShopItem(7, Material.GOLD_INGOT, ShopCategory.MELEE, ItemBuilder(Material.DIAMOND_SWORD).toItemStack(), 29),
            ShopItem(3, Material.EMERALD, ShopCategory.MELEE, ItemBuilder(Material.NETHERITE_SWORD).toItemStack(), 38),
            ShopItem(15, Material.IRON_INGOT, ShopCategory.MELEE, ItemBuilder(Material.SHIELD).toItemStack(), 47),

            ShopItem(10, Material.IRON_INGOT, ShopCategory.ARMOR, ItemBuilder(Material.IRON_BOOTS).toItemStack(), 21),
            ShopItem(12, Material.GOLD_INGOT, ShopCategory.ARMOR, ItemBuilder(Material.DIAMOND_BOOTS).toItemStack(), 30),
            ShopItem(6, Material.EMERALD, ShopCategory.ARMOR, ItemBuilder(Material.NETHERITE_BOOTS).toItemStack(), 39),
            ShopItem(20, Material.GOLD_INGOT, ShopCategory.ARMOR, ItemBuilder(Material.TOTEM_OF_UNDYING).toItemStack(), 48),

            ShopItem(20, Material.IRON_INGOT, ShopCategory.TOOLS, ItemBuilder(Material.SHEARS).toItemStack(), 22),
            ShopItem(10, Material.IRON_INGOT, ShopCategory.TOOLS, ItemBuilder(Material.WOODEN_PICKAXE).setUnbreakable(true).addEnchant(Enchantment.DIG_SPEED, 2).toItemStack(), 31),
            ShopItem(10, Material.EMERALD, ShopCategory.TOOLS, ItemBuilder(Material.WOODEN_AXE).setUnbreakable(true).addEnchant(Enchantment.DIG_SPEED, 2).toItemStack(), 40),

            ShopItem(10, Material.GOLD_INGOT, ShopCategory.RANGED, ItemBuilder(Material.BOW).setUnbreakable(true).toItemStack(), 23),
            ShopItem(10, Material.GOLD_INGOT, ShopCategory.RANGED, ItemBuilder(Material.BOW).setUnbreakable(true).addEnchant(Enchantment.ARROW_KNOCKBACK, 1).addEnchant(Enchantment.ARROW_DAMAGE, 2).toItemStack(), 32),
            ShopItem(8, Material.IRON_INGOT, ShopCategory.RANGED, ItemBuilder(Material.ARROW, 8).toItemStack(), 41),

            ShopItem(2, Material.EMERALD, ShopCategory.POTIONS, ItemBuilder(Material.POTION).setPotion(PotionEffectType.INVISIBILITY, 600, 2).toItemStack(), 24),
            ShopItem(1, Material.EMERALD, ShopCategory.POTIONS, ItemBuilder(Material.POTION).setPotion(PotionEffectType.SPEED, 900, 3).toItemStack(), 33),
            ShopItem(2, Material.EMERALD, ShopCategory.POTIONS, ItemBuilder(Material.POTION).setPotion(PotionEffectType.JUMP, 900, 5).toItemStack(), 42),

            ShopItem(40, Material.IRON_INGOT, ShopCategory.UTILITY, ItemBuilder(Material.FIRE_CHARGE).toItemStack(), 25),
            ShopItem(8, Material.GOLD_INGOT, ShopCategory.UTILITY, ItemBuilder(Material.TNT).toItemStack(), 34),
            ShopItem(3, Material.GOLDEN_APPLE, ShopCategory.UTILITY, ItemBuilder(Material.GOLDEN_APPLE).toItemStack(), 43),
            ShopItem(3, Material.EMERALD, ShopCategory.UTILITY, ItemBuilder(Material.ENDER_PEARL).toItemStack(), 51),
        )
        ShopCategory.values().forEach {
            when(it){
                ShopCategory.BLOCKS -> inventory.setItem(2, ItemStack(Material.TERRACOTTA))
                ShopCategory.MELEE -> inventory.setItem(3, ItemStack(Material.GOLDEN_SWORD))
                ShopCategory.ARMOR -> inventory.setItem(4, ItemStack(Material.IRON_BOOTS))
                ShopCategory.TOOLS -> inventory.setItem(5, ItemStack(Material.STONE_HOE))
                ShopCategory.RANGED -> inventory.setItem(3, ItemStack(Material.BOW))
                ShopCategory.POTIONS -> inventory.setItem(3, ItemStack(Material.BREWING_STAND))
                ShopCategory.UTILITY -> inventory.setItem(3, ItemStack(Material.TNT))
            }
        }

        items.forEach {
            inventory.setItem(it.position, it.getShopItemstack(player))
        }

    }

    override fun handleClick(player: Player, itemStack: ItemStack, view: InventoryView): GUI? {
        if(!isInventory(view)) return null
        if(!itemStack.hasItemMeta()) return null
        if(!itemStack.itemMeta.hasLore()) return null
        if(isItemToBuy(itemStack)) return null

        val shopItem: ShopItem = getShopItemByItemstack(itemStack, player) ?: return null
        val price: Int = shopItem.price

        val currencyMaterial: Material = shopItem.currencyMaterial

        if(player.inventory.contains(currencyMaterial, price)){
            player.inventory.addItem(shopItem.item)
            player.inventory.removeItem(ItemStack(currencyMaterial, price))
            player.sendMessage(Colorize.c("&aВы приобрели &6${shopItem.item.itemMeta.localizedName}"))
        } else {
            player.sendMessage(Colorize.c("&cУ вас недостаточно денег для покупки этого"))
        }
        return null
    }

    override fun isInventory(view: InventoryView): Boolean {
        return view.title == name
    }

    private fun isItemToBuy(itemStack: ItemStack): Boolean{
        items.forEach {
            return it.item == itemStack
        }
        return false
    }

    private fun getShopItemByItemstack(itemStack: ItemStack, player: Player): ShopItem? {
        items.forEach {
            if(itemStack == it.getShopItemstack(player)){
                return it
            }
        }
        return null
    }
}