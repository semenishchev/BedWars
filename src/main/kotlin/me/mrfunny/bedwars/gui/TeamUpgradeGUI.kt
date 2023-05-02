package me.mrfunny.bedwars.gui

import me.mrfunny.bedwars.game.GameManager
import me.mrfunny.bedwars.players.PlayerData
import me.mrfunny.bedwars.worlds.generators.GeneratorTier
import me.mrfunny.bedwars.worlds.islands.Island
import me.mrfunny.bedwars.worlds.islands.teamupgrades.MaxLevel
import me.mrfunny.bedwars.worlds.islands.teamupgrades.UpgradeItem
import org.bukkit.Bukkit
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack

class TeamUpgradeGUI(private val gameManager: GameManager): GUI {
    override val inventory: Inventory = Bukkit.createInventory(null, 27, "Team upgrades")
    override val name: String = "Team upgrades"

    init {
        gameManager.upgrades.forEach {
            inventory.setItem(it.position, it.displayItem)
        }
    }

    override fun handleClick(player: Player, itemStack: ItemStack, view: InventoryView): GUI? {
        if(!isInventory(view)) return null
        val island: Island = gameManager.world.getIslandForPlayer(player)!!
        if(getUpgradeByItemStack(itemStack, island) == null) return null

        val upgrade: UpgradeItem = getUpgradeByItemStack(itemStack, island)!!

        upgrade.upgrade(player, gameManager){
            if(upgrade.id == "armor") {
                island.players.forEach {
                    for(item in it.inventory){
                        if(item == null) continue
                        if(!isArmor(item)) continue
                        item.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, upgrade.currentLevel.toInt())
                    }
                }
            } else if(upgrade.id == "generator"){
                when(upgrade.currentLevel){
                    MaxLevel.ONE -> {
                        for (generator in island.islandGenerators){
                            generator.currentTier = GeneratorTier.TWO
                        }
                    }
                    MaxLevel.TWO -> {
                        for (generator in island.islandGenerators){
                            generator.currentTier = GeneratorTier.THREE
                        }
                    }
                    MaxLevel.THREE ->{
                        island.activateEmeraldGenerators()
                    }
                    else -> println("generator bug lol")
                }
            } else if(upgrade.id == "compass"){
                for (it in island.players) {
                    PlayerData.get(it).isCompassUnlocked = true
                }
            }
        }

        return null
    }

    private fun isUpgrade(`is`: ItemStack): Boolean {
        gameManager.upgrades.forEach {
            return it.displayItem == `is`
        }
        return false
    }

    private fun getUpgradeByItemStack(item: ItemStack, island: Island): UpgradeItem? {
        island.upgrades.forEach {
            if(it.displayItem == item){
                return it
            }
        }
        return null
    }

//    private fun getIslandUpgrade(item: ItemStack, island: Island): UpgradeItem?{
//        return if(island.upgrades.contains(getUpgradeByItemStack(item, island))){
//            getUpgradeByItemStack(item)
//        } else {
//            island.upgrades.add(getUpgradeByItemStack(item, island)!!)
//            getUpgradeByItemStack(item, island)
//        }
//    }

    private fun isArmor(itemStack: ItemStack?): Boolean {
        if (itemStack == null) return false
        val typeNameString = itemStack.type.name
        return (typeNameString.endsWith("_HELMET")
                || typeNameString.endsWith("_CHESTPLATE")
                || typeNameString.endsWith("_LEGGINGS")
                || typeNameString.endsWith("_BOOTS"))
    }
}