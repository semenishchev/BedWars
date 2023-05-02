package me.mrfunny.bedwars.gui

import me.mrfunny.bedwars.BedWars.Companion.addInteractionKey
import me.mrfunny.bedwars.BedWars.Companion.colorize
import me.mrfunny.bedwars.BedWars.Companion.getInteractionKey
import me.mrfunny.bedwars.BedWars.Companion.isBetterThan
import me.mrfunny.bedwars.game.GameManager
import me.mrfunny.bedwars.gui.shops.shop.ShopCategory
import me.mrfunny.bedwars.gui.shops.shop.ShopItem
import me.mrfunny.bedwars.players.PlayerData
import me.mrfunny.bedwars.util.ItemBuilder
import me.mrfunny.bedwars.worlds.generators.Generator
import me.mrfunny.bedwars.worlds.generators.GeneratorType
import me.mrfunny.bedwars.worlds.islands.Island
import me.mrfunny.bedwars.worlds.islands.teamupgrades.ArmourUpgrade
import me.mrfunny.bedwars.worlds.islands.teamupgrades.MaxLevel
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.*
import org.bukkit.block.Banner
import org.bukkit.enchantments.Enchantment.PROTECTION_ENVIRONMENTAL
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.BlockStateMeta
import org.bukkit.inventory.meta.ItemMeta

open class PrimaryShopGUI(private val gameManager: GameManager, private val player: Player, val items: Array<ShopItem>) : GUI{
    private val data: PlayerData = PlayerData.get(player)
    final override val name: String = "Shop"
    final override val inventory: Inventory = Bukkit.createInventory(null, if(items.size > 7) 36 else 27, name)

    private val island: Island = gameManager.world.getIslandForPlayer(player)!!

    init {
        var totalItems = 10
        for (it in items) {
            inventory.setItem(totalItems, it.getShopItemstack(player))
            if((totalItems % 8) == 0){
                totalItems += 2
            }
            totalItems++
        }
        inventory.setItem(if(items.size > 7) 35 else 26, ItemBuilder(Material.IRON_DOOR).setName("&cBack").toItemStack().addInteractionKey("back"))
    }


    // FIXME: localisation messages
    override fun handleClick(player: Player, itemStack: ItemStack, view: InventoryView): GUI? {
        if(!isInventory(view)) return null
        if(!itemStack.hasItemMeta()) return null
        if(itemStack.getInteractionKey() == "back") return ItemShopGUI(gameManager, player)
        if(isItemToBuy(itemStack)) return null

        val shopItem: ShopItem = getShopItemByItemstack(itemStack, player) ?: return PrimaryShopGUI(gameManager, player, items)
        val price: Int = shopItem.price

        val currencyMaterial: Material = shopItem.currencyMaterial

        val itemStackToRemove: ItemStack = when(currencyMaterial){
            GeneratorType.IRON.getMaterial() -> ItemBuilder(currencyMaterial, price).setName(GeneratorType.IRON.getName()).toItemStack()
            GeneratorType.GOLD.getMaterial() -> ItemBuilder(currencyMaterial, price).setName(GeneratorType.GOLD.getName()).toItemStack()
            Material.FERMENTED_SPIDER_EYE -> ItemBuilder(currencyMaterial, price).setName(GeneratorType.EMERALD.getName()).toItemStack()
            else -> ItemBuilder(currencyMaterial, price).setName("&bPepega").toItemStack()
        }

        if(player.inventory.contains(currencyMaterial, price)){
            if(!player.canPickupItems) {
                player.sendMessage("&cYou inventory is full".colorize())
                return this
            }
            var isArmor = false
            val type = shopItem.item.type
            if(type.name.contains("BOOTS")){
                isArmor = true
                if((player.inventory.boots?.type ?: Material.AIR).isBetterThan(type)) {
                    player.sendMessage(Component.text("You already have this or better armor", NamedTextColor.RED))
                    return this
                }

                val builder = ItemBuilder(type)
                island.getUpgrade(ArmourUpgrade::class.java).also {
                    if(it.currentLevel == MaxLevel.ZERO) return@also
                    builder.addEnchant(PROTECTION_ENVIRONMENTAL, it.currentLevel.toInt())
                }
                val boots = builder.toItemStack()
                player.inventory.boots = boots
                player.inventory.leggings = boots.clone().also { it.type = Material.valueOf(type.name.replace("BOOTS", "LEGGINGS")) }
            } else if(shopItem.item.type == Material.ELYTRA){
                if((player.inventory.chestplate?.type ?: Material.AIR) == Material.ELYTRA){
                    player.sendMessage(Component.text("You already own an elytra", NamedTextColor.RED))
                    return this
                }

                player.inventory.chestplate = ItemStack(Material.ELYTRA)
            }
            else {
                if(data.canBuyForSale && shopItem.category == ShopCategory.BLOCKS){
                    for(i in 0..5){
                        player.inventory.addItem(shopItem.item)
                    }
                }

                if(shopItem.item.type == Material.SHIELD){
                    val item = ItemStack(Material.SHIELD)
                    val shieldMeta: ItemMeta = item.itemMeta!!
                    val banner: Banner = (shieldMeta as BlockStateMeta).blockState as Banner
                    banner.baseColor = gameManager.world.getIslandOf(player).color.dyeColor()
                    shieldMeta.blockState = banner
                    item.itemMeta = shieldMeta
                    player.inventory.addItem(item)
                } else {
                    player.inventory.addItem(shopItem.item)
                }
            }

            player.inventory.removeItem(itemStackToRemove)
            var base = Component.text("You've purchased ", NamedTextColor.GREEN)
//            if(isArmor){
//                player.sendMessage
//                player.sendMessage("&aYou've purchased ${ChatColor.stripColor(shopItem.item.itemMeta!!.displayName)!!.split(" ")[0]} броню".colorize())
//            } else {
//                player.sendMessage(Colorize.c("&a${if(data.isRussian())"Вы купили" else "You purchased"} &6${if(shopItem.item.itemMeta!!.displayName == "") ChatColor.stripColor(shopItem.item.i18NDisplayName) else ChatColor.stripColor(shopItem.item.itemMeta.displayName) }"))
//            }
            base = if(isArmor) {
                base.append(Component.text(shopItem.item.type.name.split("_").let { it.getOrNull(0) ?: "IRON" }.lowercase(), NamedTextColor.GOLD))
            } else {
                base.append(shopItem.item.displayName().color(NamedTextColor.GOLD))
            }

            player.sendMessage(base)
            player.playNote(player.location, Instrument.PLING, Note(12))
        } else {
            player.sendMessage("&cYou don't have enough ${Generator.getGeneratorTypeByMaterial(shopItem.currencyMaterial)}".colorize())
            player.playSound(player.location, Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f)
        }
        return this
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