package me.mrfunny.plugins.paper.gui.shops.teamupgrades

import me.mrfunny.plugins.paper.BedWars.Companion.colorize
import me.mrfunny.plugins.paper.gamemanager.GameManager
import me.mrfunny.plugins.paper.players.PlayerData
import me.mrfunny.plugins.paper.worlds.Island
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

class UpgradeItem(val position: Int, val id: String, val description: String, val displayItem: ItemStack, val maxLevel: MaxLevel, vararg val prices: Int){
    var currentLevel = MaxLevel.ZERO
    val iterator = prices.iterator()
    var currentPrice = iterator.next()
    init {
        val meta: ItemMeta = displayItem.itemMeta
        meta.lore?.add("")
        meta.lore?.add(description.colorize())
        meta.lore?.add("")
        meta.lore?.add(currentLevel.name)
        displayItem.itemMeta = meta
    }
    fun upgrade(upgrader: Player, gameManager: GameManager, whatUpgradeDoes: Runnable){
//        println("${maxLevel == currentLevel} ${!iterator.hasNext()} ${maxLevel.name} ${currentLevel.name}")
        if(maxLevel == currentLevel) {
            upgrader.sendMessage("&aUpgrade is max level".colorize())
            return
        }
        val island: Island = gameManager.world.getIslandForPlayer(upgrader)!!
        if(island.totalSouls >= currentPrice){
            island.totalSouls -= currentPrice
            currentLevel = MaxLevel.fromInt(currentLevel.toInt() + 1)
            if(iterator.hasNext()){
                currentPrice = iterator.next()
            }
            whatUpgradeDoes.run()
//            upgrader.sendMessage("&aYou purchased the &6${ChatColor.stripColor(displayItem.itemMeta.displayName)}".colorize())
            island.players.forEach {
                if (PlayerData.PLAYERS[it.uniqueId]!!.isRussian()) {
                    it.sendMessage("&b${upgrader.name}&f купил &b${ChatColor.stripColor(displayItem.itemMeta.displayName)}&f улучшение! Теперь оно &b${currentLevel.toInt()} уровня".colorize())
                } else {
                    it.sendMessage("&b${upgrader.name}&f purchased &b${ChatColor.stripColor(displayItem.itemMeta.displayName)}&f upgrade! Now it is tier &b${currentLevel.toInt()}".colorize())
                }
            }
        } else {
            upgrader.sendMessage("${ChatColor.RED}You didn't have enough souls to buy this upgrade. You have ${island.totalSouls}, but need $currentPrice")
        }
    }
}