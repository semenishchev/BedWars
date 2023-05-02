package me.mrfunny.bedwars.worlds.islands.teamupgrades

import me.mrfunny.bedwars.BedWars.Companion.colorize
import me.mrfunny.bedwars.game.GameManager
import me.mrfunny.bedwars.worlds.islands.Island
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

open class UpgradeItem: Cloneable {
    val position: Int
    val id: String
    val description: String
    val displayItem: ItemStack
    val maxLevel: MaxLevel
    val prices: IntArray

//    constructor(
//        position: Int,
//        id: String,
//        description: String,
//        displayItem: ItemStack,
//        maxLevel: MaxLevel,
//        vararg prices: Int
//    ) {
//        this.position = position
//        this.id = id
//        this.description = description
//        this.displayItem = displayItem
//        this.maxLevel = maxLevel
//        this.prices = prices
//        this.iterator = prices.iterator()
//        this.currentPrice = iterator.next()
//        val meta: ItemMeta = displayItem.itemMeta
//        val lore = listOf<Component>(
//            Component.empty(),
//            Component.text(description.colorize()),
//            Component.empty(),
//            Component.text(currentLevel.name)
//        )
//        meta.lore(lore)
//        displayItem.itemMeta = meta
//    }

    constructor(position: Int, id: String, displayMaterial: Material, description: String, maxLevel: MaxLevel, vararg prices: Int) {
        this.position = position
        this.id = id
        this.description = description
        this.maxLevel = maxLevel
        this.prices = prices
        val displayItem = ItemStack(displayMaterial)
        val meta = displayItem.itemMeta
        this.iterator = prices.iterator()
        this.currentPrice = iterator.next()
        meta.displayName(Component.text("$id upgrade", NamedTextColor.GREEN))
        meta.lore(listOf(
            Component.text(description, NamedTextColor.GRAY),
            Component.empty(),
            Component.text("Current level: ${currentLevel.toInt()}")
        ))
        displayItem.itemMeta = meta
        this.displayItem = displayItem
    }

    var currentLevel = MaxLevel.ZERO
    val iterator: IntIterator
    private var currentPrice: Int
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
            for (it in island.players) {
                it.sendMessage(upgrader.name().color(NamedTextColor.AQUA)
                    .append(Component.text(" purchased ", NamedTextColor.WHITE))
                    .append(displayItem.displayName().color(NamedTextColor.AQUA)
                    .append(Component.text(" upgrade! Now it is tier ", NamedTextColor.WHITE))
                    .append(Component.text(currentLevel.toInt())))
                )
            }
        } else {
            upgrader.sendMessage(Component.text("You don't have enough souls to buy this upgrade. " +
                    "You have ${island.totalSouls} but you need $currentPrice souls", NamedTextColor.RED))
        }
    }

    public override fun clone(): UpgradeItem {
        return super.clone() as UpgradeItem
    }

    companion object
}