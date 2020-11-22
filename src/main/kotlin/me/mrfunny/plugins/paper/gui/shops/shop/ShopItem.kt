package me.mrfunny.plugins.paper.gui.shops.shop

import me.mrfunny.plugins.paper.util.Colorize
import me.mrfunny.plugins.paper.util.ItemBuilder
import org.bukkit.Material
import org.bukkit.entity.Damageable
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

// todo: make upgrades with this system
class ShopItem(val price: Int, val currencyMaterial: Material, val category: ShopCategory, val item: ItemStack, val position: Int, private vararg val description: String) {

    fun getLore(player: Player): ArrayList<String>{
        val output: ArrayList<String> = arrayListOf()

        output.add(Colorize.c("&7Стоимость:&f $price"))
        output.add("")
        description.forEach {
            output.add(Colorize.c(it))
        }

        output.add("")

        output.add(Colorize.c(if(canBuy(player)) "&aНажмите, чтобы купить" else "&cУ вас недостаточно $currencyMaterial для покупки"))

        output.add("")

        return output
    }

    fun getShopItemstack(player: Player): ItemStack {
        return ItemBuilder(item.type, item.amount).setLore(getLore(player)).toItemStack()
    }

    fun canBuy(player: Player): Boolean{
        for(inventoryItem in player.inventory){
            if(inventoryItem == null) continue
            if(inventoryItem.type == currencyMaterial){
                return inventoryItem.amount >= price
            }
        }
        return false
    }
}