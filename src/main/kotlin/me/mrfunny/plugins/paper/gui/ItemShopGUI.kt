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
import org.bukkit.enchantments.Enchantment.ARROW_KNOCKBACK
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.potion.PotionData
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.potion.PotionType

class ItemShopGUI(private val gameManager: GameManager, private val player: Player) : GUI {
    override val inventory: Inventory = Bukkit.createInventory(null, 54, "Shop")
    override val name: String = "Shop"
    private val items: Array<ShopItem>

    val invisibilityPotion: ItemStack = ItemStack(Material.POTION)
    val speedPotion: ItemStack = ItemStack(Material.POTION)
    val jumpPotion: ItemStack = ItemStack(Material.POTION)

    init {
        // invisibility
        val invisibilityPotionMeta: PotionMeta = invisibilityPotion.itemMeta as PotionMeta
        invisibilityPotionMeta.addCustomEffect(PotionEffect(PotionEffectType.INVISIBILITY, 600, 2), true)
        invisibilityPotion.itemMeta = invisibilityPotionMeta
        val island: Island = gameManager.world.getIslandForPlayer(player)!!

        // todo:
        items = arrayOf(
            ShopItem(20, Material.GHAST_TEAR, ShopCategory.UTILITY, ItemBuilder(Material.SNOWBALL).setName("&aSwapping snowball").toItemStack(), 1),
            ShopItem(4, Material.GHAST_TEAR, ShopCategory.BLOCKS, ItemBuilder(island.color.woolMaterial(), 16).toItemStack(), 19, "&aНеплохие блоки, чтобы строится"),
            ShopItem(6, Material.GOLD_NUGGET, ShopCategory.BLOCKS, ItemBuilder(Material.OAK_PLANKS, 12).toItemStack(), 28, "Неплох для застройки кровати"),
            ShopItem(24, Material.GHAST_TEAR, ShopCategory.BLOCKS, ItemBuilder(Material.END_STONE, 16).toItemStack(), 37, "Защитит кровать от почти всех взрывов"),

            ShopItem(10, Material.GHAST_TEAR, ShopCategory.MELEE, ItemBuilder(Material.IRON_SWORD).toItemStack(), 20),
            ShopItem(7, Material.GOLD_NUGGET, ShopCategory.MELEE, ItemBuilder(Material.DIAMOND_SWORD).toItemStack(), 29),
            ShopItem(3, Material.FERMENTED_SPIDER_EYE, ShopCategory.MELEE, ItemBuilder(Material.NETHERITE_SWORD).toItemStack(), 38),
            ShopItem(15, Material.GHAST_TEAR, ShopCategory.MELEE, ItemBuilder(Material.SHIELD).toItemStack(), 47),

            ShopItem(10, Material.GHAST_TEAR, ShopCategory.ARMOR, ItemBuilder(Material.IRON_BOOTS).toItemStack(), 21),
            ShopItem(12, Material.GOLD_NUGGET, ShopCategory.ARMOR, ItemBuilder(Material.DIAMOND_BOOTS).toItemStack(), 30),
            ShopItem(6, Material.FERMENTED_SPIDER_EYE, ShopCategory.ARMOR, ItemBuilder(Material.NETHERITE_BOOTS).toItemStack(), 39),
            ShopItem(20, Material.GOLD_NUGGET, ShopCategory.ARMOR, ItemBuilder(Material.TOTEM_OF_UNDYING).toItemStack(), 48),

            ShopItem(20, Material.GHAST_TEAR, ShopCategory.TOOLS, ItemBuilder(Material.SHEARS).toItemStack(), 22),
            ShopItem(10, Material.GHAST_TEAR, ShopCategory.TOOLS, ItemBuilder(Material.WOODEN_PICKAXE).setUnbreakable(true).addEnchant(Enchantment.DIG_SPEED, 2).toItemStack(), 31),
            ShopItem(10, Material.GHAST_TEAR, ShopCategory.TOOLS, ItemBuilder(Material.WOODEN_AXE).addEnchant(Enchantment.DIG_SPEED, 2).toItemStack(), 40),
            ShopItem(10, Material.GOLD_NUGGET, ShopCategory.TOOLS, ItemBuilder(Material.FISHING_ROD).setUnbreakable(false).setName("&aGrappling Hook").toItemStack(), 49),

            ShopItem(10, Material.GOLD_NUGGET, ShopCategory.RANGED, ItemBuilder(Material.BOW).setUnbreakable(true).toItemStack(), 23),
            ShopItem(10, Material.GOLD_NUGGET, ShopCategory.RANGED, ItemBuilder(Material.BOW).setUnbreakable(true).addEnchant(
                ARROW_KNOCKBACK, 1).addEnchant(Enchantment.ARROW_DAMAGE, 2).toItemStack(), 32),
            ShopItem(8, Material.GHAST_TEAR, ShopCategory.RANGED, ItemBuilder(Material.ARROW, 8).toItemStack(), 41),

            ShopItem(2, Material.FERMENTED_SPIDER_EYE, ShopCategory.POTIONS, invisibilityPotion, 24),
//            ShopItem(1, Material.FERMENTED_SPIDER_EYE, ShopCategory.POTIONS, ItemBuilder(Material.POTION).setPotion(PotionEffectType.SPEED, 900, 3).toItemStack(), 33),
//            ShopItem(2, Material.FERMENTED_SPIDER_EYE, ShopCategory.POTIONS, ItemBuilder(Material.POTION).setPotion(PotionEffectType.JUMP, 900, 5).toItemStack(), 42),

            ShopItem(40, Material.GHAST_TEAR, ShopCategory.UTILITY, ItemBuilder(Material.FIRE_CHARGE).toItemStack(), 25),
            ShopItem(8, Material.GOLD_NUGGET, ShopCategory.UTILITY, ItemBuilder(Material.TNT).toItemStack(), 34),
            ShopItem(3, Material.GOLD_NUGGET, ShopCategory.UTILITY, ItemBuilder(Material.GOLDEN_APPLE).toItemStack(), 43),
            ShopItem(3, Material.FERMENTED_SPIDER_EYE, ShopCategory.UTILITY, ItemBuilder(Material.ENDER_PEARL).toItemStack(), 51),
        )

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
            if(shopItem.item.type.name.contains("BOOTS")){
                when {
                    shopItem.item.type.name.contains("IRON") -> {
                        player.inventory.boots = ItemStack(Material.IRON_BOOTS)
                        player.inventory.leggings = ItemStack(Material.IRON_LEGGINGS)
                    }
                    shopItem.item.type.name.contains("DIAMOND") -> {
                        player.inventory.boots = ItemStack(Material.DIAMOND_BOOTS)
                        player.inventory.leggings = ItemStack(Material.DIAMOND_LEGGINGS)
                    }
                    shopItem.item.type.name.contains("NETHERITE") -> {
                        player.inventory.boots = ItemStack(Material.NETHERITE_BOOTS)
                        player.inventory.leggings = ItemStack(Material.NETHERITE_LEGGINGS)
                    }
                }
            } else {
                player.inventory.addItem(shopItem.item)
            }

            val itemStackToRemove: ItemStack = when(currencyMaterial){
                Material.GHAST_TEAR -> ItemBuilder(currencyMaterial, price).setName("&fIron").toItemStack()
                Material.GOLD_NUGGET -> ItemBuilder(currencyMaterial, price).setName("&6Gold").toItemStack()
                Material.FERMENTED_SPIDER_EYE -> ItemBuilder(currencyMaterial, price).setName("&4Ruby").toItemStack()
                else -> ItemBuilder(currencyMaterial, price).setName("&4Ruby").toItemStack()
            }

            player.inventory.removeItem(itemStackToRemove)
            player.sendMessage(Colorize.c("&aYou purchased &6${shopItem.item.i18NDisplayName}"))
        } else {
            player.sendMessage(Colorize.c("&cYou didn't have enough ${shopItem.currencyMaterial}"))
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