package me.mrfunny.plugins.paper.gui

import me.mrfunny.plugins.paper.BedWars.Companion.colorize
import me.mrfunny.plugins.paper.gamemanager.GameManager
import me.mrfunny.plugins.paper.gameutils.StartingPower
import me.mrfunny.plugins.paper.players.PlayerData
import me.mrfunny.plugins.paper.util.ItemBuilder
import org.bukkit.*
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack
import java.util.*

class StartPowerGUI(private val gameManager: GameManager, val clickedPlayer: Player): GUI {
    override val name: String = "Select start power"
    override val inventory: Inventory = Bukkit.createInventory(null, 9, name)

    init {
        gameManager.startingPowers.forEach {
            val builder = ItemBuilder(it.item.type)
            if(it.players.contains(PlayerData.PLAYERS[clickedPlayer.uniqueId])){
                builder.addEnchant(Enchantment.DAMAGE_ALL, 1).hideEnchantments()
            }
            builder.setName(it.item.itemMeta.displayName)
            inventory.addItem(builder.toItemStack())
        }
    }

    private val playerData: PlayerData = PlayerData.PLAYERS[clickedPlayer.uniqueId]!!

    override fun handleClick(player: Player, itemStack: ItemStack, view: InventoryView): GUI? {
        if(!isInventory(view)) return null
        if(gameManager.getStartPowerByItem(itemStack) == null) return StartPowerGUI(gameManager, clickedPlayer)
        if(clickedPlayer != player) return null
        val power: StartingPower = gameManager.getStartPowerByItem(itemStack)!!
        if(power.players.contains(playerData)) { view.close() }

        val selectedPower: Optional<StartingPower> = Arrays.stream(gameManager.startingPowers).filter {pwd -> pwd.players.contains(playerData) }.findFirst()
        selectedPower.ifPresent {
            it.players.remove(playerData)
            inventory.clear()
        }

        player.sendMessage("&aYou selected &6${ChatColor.stripColor(power.item.itemMeta.displayName)}&a as you start power".colorize())
        player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1f)
        playerData.isStartPowerSelected = true
        power.players.add(PlayerData.PLAYERS[clickedPlayer.uniqueId]!!)

        return null
    }
}