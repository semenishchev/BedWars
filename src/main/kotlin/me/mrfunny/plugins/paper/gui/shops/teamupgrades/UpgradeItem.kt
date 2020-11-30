package me.mrfunny.plugins.paper.gui.shops.teamupgrades

import me.mrfunny.plugins.paper.BedWars.Companion.colorize
import me.mrfunny.plugins.paper.gamemanager.GameManager
import me.mrfunny.plugins.paper.worlds.Island
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

// todo: make upgrades with this system
class UpgradeItem(val upgradeName: String, val description: String, val displayItem: ItemStack, private val maxLevel: MaxLevel, vararg prices: Int){
    var currentLevel = MaxLevel.ONE
    val iterator = prices.iterator()
    var currentPrice = prices.first()
    init {
        val meta: ItemMeta = displayItem.itemMeta
        meta.lore?.add("")
        meta.lore?.add(description.colorize())
        displayItem.itemMeta = meta
    }
    fun upgrade(upgrader: Player, gameManager: GameManager, whatUpgradeDoes: Runnable){
        if(maxLevel == currentLevel || !iterator.hasNext()) {
            upgrader.sendMessage("&aUpgrade is max level".colorize())
            return
        } else {
            currentLevel = MaxLevel.fromInt(currentLevel.toInt() + 1)
        }

        val island: Island = gameManager.world.getIslandForPlayer(upgrader)!!
        if(island.totalSouls >= currentPrice){
            island.totalSouls--
            whatUpgradeDoes.run()
            currentPrice = iterator.next()
        } else {
            upgrader.sendMessage("${ChatColor.RED}You didn't have enough souls to buy this upgrade. You have ${island.totalSouls}, but need $currentPrice")
        }
    }

}