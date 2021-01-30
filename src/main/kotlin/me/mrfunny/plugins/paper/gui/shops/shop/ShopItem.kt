package me.mrfunny.plugins.paper.gui.shops.shop

import me.mrfunny.plugins.paper.BedWars.Companion.colorize
import me.mrfunny.plugins.paper.players.PlayerData
import me.mrfunny.plugins.paper.util.Colorize
import me.mrfunny.plugins.paper.util.ItemBuilder
import me.mrfunny.plugins.paper.worlds.generators.Generator
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class ShopItem(var price: Int, val currencyMaterial: Material, val category: ShopCategory, val item: ItemStack, private vararg val description: String) {

    private fun getLore(player: Player): ArrayList<String>{
        val output: ArrayList<String> = arrayListOf()

        output.add("")
        if(PlayerData.PLAYERS[player.uniqueId]!!.isRussian()){
            output.add(Colorize.c("&7Цена:${Generator.getColorByGenerator(currencyMaterial)} $price ${Generator.getGeneratorTypeByMaterial(currencyMaterial)}"))
        } else {
            output.add(Colorize.c("&7Price:${Generator.getColorByGenerator(currencyMaterial)} $price ${Generator.getGeneratorTypeByMaterial(currencyMaterial)}"))
        }
        output.add("")

        for(i in description.indices){
            if(i != 0){
                output.add(description[i].colorize())
            }
        }

        output.add("")
        if(PlayerData.PLAYERS[player.uniqueId]!!.isRussian()){
            output.add(if(canBuy(player)) "&aНажмите, чтобы купить".colorize() else "&cУ вас недостаточно ${Generator.getGeneratorTypeByMaterial(currencyMaterial)}&c для покупки".colorize())
        } else {
            output.add(if(canBuy(player)) "&aClick to purchase".colorize() else "&cYou don't have enough of ${Generator.getGeneratorTypeByMaterial(currencyMaterial)}&c for purchasing".colorize())
        }
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