package me.mrfunny.plugins.paper.gui

import me.mrfunny.plugins.paper.gamemanager.GameManager
import me.mrfunny.plugins.paper.gui.shops.teamupgrades.MaxLevel
import me.mrfunny.plugins.paper.gui.shops.teamupgrades.UpgradeItem
import me.mrfunny.plugins.paper.util.ItemBuilder
import me.mrfunny.plugins.paper.worlds.Island
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack

class TeamUpgradeGUI(private val gameManager: GameManager): GUI {
    override val inventory: Inventory = Bukkit.createInventory(null, 27, "Team upgrades")
    override val name: String = "Team upgrades"

    private val upgrades: Array<UpgradeItem> = arrayOf(
       UpgradeItem("Мечи", "asdsd", ItemBuilder(Material.DIAMOND_SWORD).setLore("", "Lol").toItemStack(), MaxLevel.THREE, 1, 2, 4)
   )

    init {

        upgrades.forEach {
            inventory.addItem(it.displayItem)
        }
    }

    override fun handleClick(player: Player, itemStack: ItemStack, view: InventoryView): GUI? {
        if(!isInventory(view)) return null
        if(!itemStack.hasItemMeta()) return null
        if(!itemStack.itemMeta.hasLore()) return null
        if(!isUpgrade(itemStack)) return null
        if(getUpgradeByItemStack(itemStack) == null) return null
        val upgrade: UpgradeItem = getUpgradeByItemStack(itemStack)!!

        val island: Island = gameManager.world.getIslandForPlayer(player)!!

        upgrade.upgrade(player, gameManager){
            if(upgrade.upgradeName == "Мечи"){
                island.players.forEach {
                    for(item in it.inventory){
                        if(item == null) continue
                        if(item.type.name.contains("SWORD") || item.type.name.contains("AXE")){
                            item.addEnchantment(Enchantment.DAMAGE_ALL, upgrade.currentLevel.toInt())
                        }
                    }
                }
            }
        }

        return null
    }

    override fun isInventory(view: InventoryView): Boolean {
        return view.title == name
    }

    private fun isUpgrade(`is`: ItemStack): Boolean {
        upgrades.forEach {
            return it.displayItem == `is`
        }
        return false
    }

    private fun getUpgradeByItemStack(item: ItemStack): UpgradeItem? {
        upgrades.forEach {
            if(it.displayItem == item){
                return it
            }
        }
        return null
    }

    private fun getIslandUpgrade(item: ItemStack, island: Island): UpgradeItem?{
        return if(island.upgrades.contains(getUpgradeByItemStack(item))){
            getUpgradeByItemStack(item)
        } else {
            island.upgrades.add(getUpgradeByItemStack(item)!!)
            getUpgradeByItemStack(item)
        }
    }
}