package me.mrfunny.plugins.paper.gui

import me.mrfunny.plugins.paper.gamemanager.GameManager
import me.mrfunny.plugins.paper.gui.shops.shop.ShopCategory
import me.mrfunny.plugins.paper.gui.shops.shop.categories.*
import me.mrfunny.plugins.paper.gui.shops.teamupgrades.MaxLevel
import me.mrfunny.plugins.paper.players.PlayerData
import me.mrfunny.plugins.paper.util.ItemBuilder
import me.mrfunny.plugins.paper.worlds.Island
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class ItemShopGUI(private val gameManager: GameManager, private val clickPlayer: Player): GUI {
    override val name: String = "Shop"
    override val inventory: Inventory = Bukkit.createInventory(null, 27, name)
    private val data: PlayerData = PlayerData.PLAYERS[clickPlayer.uniqueId]!!
    private val invisibilityPotion: ItemStack = ItemBuilder(Material.POTION).setName(if(data.isRussian()) "&7Зелье невидимости" else "&7Invisibility Potion").toItemStack()
    private val speedPotion: ItemStack = ItemBuilder(Material.POTION).setName(if(!data.isRussian()) "&bSpeed Potion" else "&bЗелье скорости").toItemStack()
    private val jumpPotion: ItemStack = ItemBuilder(Material.POTION).setName(if(!data.isRussian()) "&2Jump Potion" else "&bЗелье прыгучести").toItemStack()
    private val strengthPotion: ItemStack = ItemBuilder(Material.POTION).setName(if(!data.isRussian()) "&4Strength Potion" else "&4Зелье силы").toItemStack()

    private val ironSword: ItemBuilder = ItemBuilder(Material.IRON_SWORD)
    private val diamondSword: ItemBuilder = ItemBuilder(Material.DIAMOND_SWORD)
    private val netheriteSword: ItemBuilder = ItemBuilder(Material.NETHERITE_SWORD)

    private val woodenPickaxe: ItemBuilder = ItemBuilder(Material.WOODEN_PICKAXE).addEnchant(Enchantment.DIG_SPEED, 2)
    private val ironPickaxe: ItemBuilder = ItemBuilder(Material.IRON_PICKAXE).addEnchant(Enchantment.DIG_SPEED, 3)
    private val diamondPickaxe: ItemBuilder = ItemBuilder(Material.DIAMOND_PICKAXE).addEnchant(Enchantment.DIG_SPEED, 1)
    private val netheritePickaxe: ItemBuilder = ItemBuilder(Material.NETHERITE_PICKAXE).addEnchant(Enchantment.DIG_SPEED, 5)

    init {
        var currentCategory = 10
        ShopCategory.values().forEach {
            inventory.setItem(currentCategory, it.item)
            currentCategory++
        }
        // invisibility
        val invisibilityPotionMeta: PotionMeta = invisibilityPotion.itemMeta as PotionMeta
        invisibilityPotionMeta.addCustomEffect(PotionEffect(PotionEffectType.INVISIBILITY, 600, 2), true)
        invisibilityPotionMeta.color = Color.fromRGB(127, 131, 146)
        invisibilityPotion.itemMeta = invisibilityPotionMeta

        // speed
        val speedPotionMeta: PotionMeta = speedPotion.itemMeta as PotionMeta
        speedPotionMeta.color = Color.fromRGB(124, 175, 198)
        speedPotionMeta.addCustomEffect(PotionEffect(PotionEffectType.SPEED, 60 * 20, 2), true)
        speedPotion.itemMeta = speedPotionMeta

        // jump
        val jumpPotionMeta: PotionMeta = jumpPotion.itemMeta as PotionMeta
        jumpPotionMeta.color = Color.fromRGB(786297)
        jumpPotionMeta.addCustomEffect(PotionEffect(PotionEffectType.JUMP, 60 * 20, 4), true)
        jumpPotion.itemMeta = jumpPotionMeta

        val strengthPotionMeta: PotionMeta = strengthPotion.itemMeta as PotionMeta
        strengthPotionMeta.color = Color.fromRGB(147, 36, 35)
        strengthPotionMeta.addCustomEffect(PotionEffect(PotionEffectType.INCREASE_DAMAGE, 30 * 20, 0), true)
        strengthPotion.itemMeta = strengthPotionMeta
    }

    override fun handleClick(player: Player, itemStack: ItemStack, view: InventoryView): GUI? {
        if(!isInventory(view)) return null
        if(player != clickPlayer) return null
        return when(ChatColor.stripColor(itemStack.itemMeta.displayName)!!.toLowerCase()){
            "blocks" -> BlockCategoryGUI(gameManager, player, gameManager.world.getIslandForPlayer(player)!!)
            "armor" -> ArmorCategoryGUI(gameManager, player)
            "melee" -> MeleeCategoryGUI(gameManager, player, ironSword, diamondSword, netheriteSword)
            "tools" -> ToolsCategoryGUI(gameManager, player, woodenPickaxe, ironPickaxe, diamondPickaxe, netheritePickaxe)
            "ranged" -> RandedCategoryGUI(gameManager, player)
            "potions" -> PotionCategoryGUI(gameManager, player, invisibilityPotion, speedPotion, jumpPotion, strengthPotion)
            "utils" -> UtilsCategoryGUI(gameManager, PlayerData.PLAYERS[player.uniqueId]!!)
            else -> ItemShopGUI(gameManager, player)
        }
    }

}