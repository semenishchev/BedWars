package me.mrfunny.bedwars.gui

import me.mrfunny.bedwars.BedWars.Companion.colorize
import me.mrfunny.bedwars.game.GameManager
import me.mrfunny.bedwars.gameutils.StartingPower
import me.mrfunny.bedwars.players.PlayerData
import me.mrfunny.bedwars.util.ItemBuilder
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Sound
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack
import java.util.*

class StartPowerGUI(private val gameManager: GameManager, val clickedPlayer: Player): GUI {
    override val name: String = "Select start power"
    override val inventory: Inventory = Bukkit.createInventory(null, 9, name)
    private val playerData: PlayerData = PlayerData.get(clickedPlayer)
    init {
        gameManager.startingPowers.forEach {
            val builder = ItemBuilder(it.item.type)
            if(it.players.contains(playerData)){
                builder.addEnchant(Enchantment.DAMAGE_ALL, 1).hideEnchantments()
            }
            builder.setName(it.item.itemMeta.displayName)
            inventory.addItem(builder.toItemStack())
        }
    }



    override fun handleClick(player: Player, itemStack: ItemStack, view: InventoryView): GUI? {
        if(!isInventory(view)) return null
        if(gameManager.getStartPowerByItem(itemStack) == null) return StartPowerGUI(gameManager, clickedPlayer)
        if(clickedPlayer != player) return null
        val power: StartingPower = gameManager.getStartPowerByItem(itemStack)!!
        if(power.players.contains(playerData)) { view.close() }

        val selectedPower = Arrays.stream(gameManager.startingPowers)
            .filter {pwd -> pwd.players.contains(playerData) }
            .findFirst()
            .orElse(null)
        selectedPower?.let {
            it.players.remove(playerData)
            inventory.clear()
        }

        player.sendMessage("&aYou selected &6${ChatColor.stripColor(power.item.itemMeta.displayName)}&a as you start power".colorize())
        player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1f)
        playerData.isStartPowerSelected = true
        power.players.add(playerData)

        return null
    }
}