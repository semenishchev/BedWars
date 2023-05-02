package me.mrfunny.bedwars.gui.shops.shop

import me.mrfunny.bedwars.BedWars.Companion.colorize
import me.mrfunny.bedwars.util.Colorize
import me.mrfunny.bedwars.util.ItemBuilder
import me.mrfunny.bedwars.worlds.generators.Generator
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class ShopItem(var price: Int, val currencyMaterial: Material, val category: ShopCategory, val item: ItemStack, private vararg val description: String) {

    private fun getLore(player: Player): ArrayList<String>{
        val output: ArrayList<String> = arrayListOf()

        output.add("")
        // FIXME: message localisation
        val material = Generator.getGeneratorTypeByMaterial(currencyMaterial)
        output.add(Colorize.c("&7Price:${Generator.getColorByGenerator(currencyMaterial)} $price $material"))
        output.add("")

        for(i in description.indices){
            if(i != 0){
                output.add(description[i].colorize())
            }
        }

        output.add("")
        output.add(if(canBuy(player)) "&aClick to purchase".colorize() else "&cYou don't have enough of $material&c for purchasing".colorize())
        output.add("")

        return output
    }

    fun getShopItemstack(player: Player): ItemStack {
        val itemBuilder: ItemBuilder = ItemBuilder(item.clone()).setLore(getLore(player))
        return itemBuilder.toItemStack()
    }

    private fun canBuy(player: Player): Boolean{
        for(inventoryItem in player.inventory){
            if(inventoryItem == null) continue
            if(inventoryItem.type == currencyMaterial){
                return inventoryItem.amount >= price
            }
        }
        return false
    }
}