package me.mrfunny.plugins.paper.gui

import me.mrfunny.plugins.paper.BedWars.Companion.colorize
import me.mrfunny.plugins.paper.gamemanager.GameManager
import me.mrfunny.plugins.paper.gui.shops.shop.ShopCategory
import me.mrfunny.plugins.paper.util.Colorize
import me.mrfunny.plugins.paper.util.ItemBuilder
import me.mrfunny.plugins.paper.worlds.Island
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack
import me.mrfunny.plugins.paper.gui.shops.shop.ShopItem
import me.mrfunny.plugins.paper.gui.shops.teamupgrades.MaxLevel
import me.mrfunny.plugins.paper.gui.shops.teamupgrades.UpgradeItem
import me.mrfunny.plugins.paper.players.PlayerData
import me.mrfunny.plugins.paper.worlds.generators.Generator
import org.bukkit.*
import org.bukkit.block.Banner
import org.bukkit.block.banner.Pattern
import org.bukkit.enchantments.Enchantment.*
import org.bukkit.inventory.meta.BlockStateMeta
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

open class PrimaryShopGUI(private val gameManager: GameManager, private val player: Player, val items: Array<ShopItem>) : GUI{
    private val data: PlayerData = PlayerData.PLAYERS[player.uniqueId]!!
    final override val name: String = if(data.isRussian()) "Магазин" else "Shop"
    final override val inventory: Inventory = Bukkit.createInventory(null, if(items.size > 7) 36 else 27, name)

    private val island: Island = gameManager.world.getIslandForPlayer(player)!!

    init {
        var totalItems = 10
        items.forEach {
            inventory.setItem(totalItems, it.getShopItemstack(player))
            if((totalItems % 8) == 0){
                totalItems += 2
            }
            totalItems++
        }
        inventory.setItem(if(items.size > 7) 35 else 26, ItemBuilder(Material.IRON_DOOR).setName("&cBack").toItemStack())
    }

    override fun handleClick(player: Player, itemStack: ItemStack, view: InventoryView): GUI? {
        if(!isInventory(view)) return null
        if(!itemStack.hasItemMeta()) return null
        if(itemStack.itemMeta.displayName.contains("Back")) return ItemShopGUI(gameManager, player)
        if(isItemToBuy(itemStack)) return null

        val shopItem: ShopItem = getShopItemByItemstack(itemStack, player) ?: return PrimaryShopGUI(gameManager, player, items)
        val price: Int = shopItem.price

        val currencyMaterial: Material = shopItem.currencyMaterial

        val itemStackToRemove: ItemStack = when(currencyMaterial){
            Material.GHAST_TEAR -> ItemBuilder(currencyMaterial, price).setName("&fIron").toItemStack()
            Material.GOLD_NUGGET -> ItemBuilder(currencyMaterial, price).setName("&6Gold").toItemStack()
            Material.FERMENTED_SPIDER_EYE -> ItemBuilder(currencyMaterial, price).setName("&4Ruby").toItemStack()
            else -> ItemBuilder(currencyMaterial, price).setName("&4Ruby").toItemStack()
        }

        if(player.inventory.contains(currencyMaterial, price)){
            if(!player.canPickupItems){
                player.sendMessage("&cYou inventory is full".colorize())
                return PrimaryShopGUI(gameManager, player, items)
            }
            var isArmor = false
            if(shopItem.item.type.name.contains("BOOTS")){
                isArmor = true
                when {
                    shopItem.item.type.name.contains("IRON") -> {
                        if(player.inventory.boots != null && player.inventory.leggings != null){
                            if(player.inventory.boots!!.type.name.contains("IRON") || player.inventory.boots!!.type.name.contains("DIAMOND") || player.inventory.boots!!.type.name.contains("NETHERITE")){
                                player.sendMessage("&cYou already have this armor".colorize())
                                return PrimaryShopGUI(gameManager, player, items)
                            }
                        }

                        val bootsBuilder = ItemBuilder(Material.IRON_BOOTS)
                        val leggingsBuilder = ItemBuilder(Material.IRON_LEGGINGS)

                        if(island.getUpgrade("armor")!!.currentLevel != MaxLevel.ZERO){
                            val armUpgrade: UpgradeItem = island.getUpgrade("armor")!!
                            bootsBuilder.addEnchant(PROTECTION_ENVIRONMENTAL, armUpgrade.currentLevel.toInt())
                            leggingsBuilder.addEnchant(PROTECTION_ENVIRONMENTAL, armUpgrade.currentLevel.toInt())
                        }

                        player.inventory.boots = bootsBuilder.toItemStack()
                        player.inventory.leggings = leggingsBuilder.toItemStack()
                    }
                    shopItem.item.type.name.contains("DIAMOND") -> {
                        if(player.inventory.boots != null && player.inventory.leggings != null){
                            if(player.inventory.boots!!.type.name.contains("NETHERITE") || player.inventory.boots!!.type.name.contains("DIAMOND")){
                                player.sendMessage("&cYou already have this armor".colorize())
                                return PrimaryShopGUI(gameManager, player, items)
                            }
                        }
                        val bootsBuilder = ItemBuilder(Material.DIAMOND_BOOTS)
                        val leggingsBuilder = ItemBuilder(Material.DIAMOND_LEGGINGS)

                        if(island.getUpgrade("armor")!!.currentLevel != MaxLevel.ZERO){
                            val armUpgrade: UpgradeItem = island.getUpgrade("armor")!!
                            bootsBuilder.addEnchant(PROTECTION_ENVIRONMENTAL, armUpgrade.currentLevel.toInt())
                            leggingsBuilder.addEnchant(PROTECTION_ENVIRONMENTAL, armUpgrade.currentLevel.toInt())
                        }

                        player.inventory.boots = bootsBuilder.toItemStack()
                        player.inventory.leggings = leggingsBuilder.toItemStack()
                    }
                    shopItem.item.type.name.contains("NETHERITE") -> {
                        if(player.inventory.boots != null && player.inventory.leggings != null) {
                            if (player.inventory.boots!!.type.name.contains("NETHERITE")) {
                                player.sendMessage("&cYou already have this armor".colorize())
                                return PrimaryShopGUI(gameManager, player, items)
                            }
                        }
                        val bootsBuilder = ItemBuilder(Material.NETHERITE_BOOTS)
                        val leggingsBuilder = ItemBuilder(Material.NETHERITE_LEGGINGS)

                        if(island.getUpgrade("armor")!!.currentLevel != MaxLevel.ZERO){
                            val armUpgrade: UpgradeItem = island.getUpgrade("armor")!!
                            bootsBuilder.addEnchant(PROTECTION_ENVIRONMENTAL, armUpgrade.currentLevel.toInt())
                            leggingsBuilder.addEnchant(PROTECTION_ENVIRONMENTAL, armUpgrade.currentLevel.toInt())
                        }

                        player.inventory.boots = bootsBuilder.toItemStack()
                        player.inventory.leggings = leggingsBuilder.toItemStack()
                    }
                }
            } else if(shopItem.item.type == Material.ELYTRA){
                if(player.inventory.chestplate!!.type == Material.ELYTRA){
                    player.inventory.addItem(shopItem.item)
                } else {
                    player.inventory.chestplate = ItemStack(Material.ELYTRA)
                }
            }
            else {
                if(PlayerData.PLAYERS[player.uniqueId]!!.canBuyForSale && shopItem.category == ShopCategory.BLOCKS){
                    for(i in 0..5){
                        player.inventory.addItem(shopItem.item)
                    }
                }

                if(shopItem.item.type == Material.SHIELD){
                    val item = ItemStack(Material.SHIELD)
                    val shieldMeta: ItemMeta = item.itemMeta
                    val banner: Banner = (shieldMeta as BlockStateMeta).blockState as Banner
                    banner.baseColor = gameManager.world.getIslandForPlayer(player)!!.color.dyeColor()
                    shieldMeta.blockState = banner
                    item.itemMeta = shieldMeta
                    player.inventory.addItem(item)
                } else {
                    player.inventory.addItem(shopItem.item)
                }
            }

            player.inventory.removeItem(itemStackToRemove)
            if(isArmor){
                player.sendMessage("&a${if(data.isRussian())"Вы купили" else "You purchased"} ${ChatColor.stripColor(shopItem.item.i18NDisplayName)!!.split(" ")[0]} броню".colorize())
            } else {
                player.sendMessage(Colorize.c("&a${if(data.isRussian())"Вы купили" else "You purchased"} &6${if(shopItem.item.itemMeta.displayName == "") ChatColor.stripColor(shopItem.item.i18NDisplayName) else ChatColor.stripColor(shopItem.item.itemMeta.displayName) }"))
            }
            player.playNote(player.location, Instrument.PLING, Note(12))
        } else {
            player.sendMessage(Colorize.c("&c${if(data.isRussian())"У вас недостаточно" else "You don't have enough "} ${Generator.getGeneratorTypeByMaterial(shopItem.currencyMaterial)}"))
            player.playSound(player.location, Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f)
        }
        return PrimaryShopGUI(gameManager, player, items)
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