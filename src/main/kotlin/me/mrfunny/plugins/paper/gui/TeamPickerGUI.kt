package me.mrfunny.plugins.paper.gui

import dev.jcsoftware.jscoreboards.JScoreboardTeam
import dev.jcsoftware.jscoreboards.exception.JScoreboardException
import me.mrfunny.plugins.paper.gamemanager.GameManager
import me.mrfunny.plugins.paper.util.Colorize
import me.mrfunny.plugins.paper.util.ItemBuilder
import me.mrfunny.plugins.paper.worlds.Island
import me.mrfunny.plugins.paper.worlds.IslandColor
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack
import java.util.*

class TeamPickerGUI(private val gameManager: GameManager, private val player: Player) : GUI {

    override val name: String = "Select island"

    companion object Name {
        const val name: String = "Select island"
    }

    override val inventory: Inventory = Bukkit.createInventory(null, 27, name)

    init {
        gameManager.world.islands.forEach {
            val itemBuilder = ItemBuilder(it.color.woolMaterial())
                .setName("${it.color.getChatColor()}${it.color.formattedName()}")
                .addLoreLine(if(it.isMember(player))"&aSelected" else "&cNot selected")
                .addLoreLine("&a${it.players.size}/${gameManager.world.maxTeamSize} players")

            if(it.isMember(player)){
                itemBuilder.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1).hideEnchantments()
            }
            inventory.addItem(itemBuilder.toItemStack())
        }

        inventory.addItem(ItemBuilder(Material.BARRIER).setName("&cExit").toItemStack())
    }

    override fun handleClick(player: Player, itemStack: ItemStack, view: InventoryView): GUI? {
        lateinit var clickedColor: IslandColor

        val itemName: String = ChatColor.stripColor(itemStack.itemMeta.displayName)!!
        for (color: IslandColor in IslandColor.values()) {
            if (itemName.equals(color.formattedName(), true)) {
                println(color)
                clickedColor = color
                break
            }
        }

        val playerIsland: Optional<Island> = gameManager.world.islands.stream().filter { island -> island.isMember(player) }.findFirst()

        playerIsland.ifPresent{ island ->
            island.players.remove(player)
            gameManager.scoreboard.findTeam(island.color.formattedName()).get().addPlayer(player)
            gameManager.updateScoreboard()
        }

        val selectedIsland: Optional<Island> = gameManager.world.islands.stream().filter { island -> island.color == clickedColor }.findFirst()

        if(selectedIsland.isPresent){
            val island: Island = selectedIsland.get()
            if(island.players.size == gameManager.world.maxTeamSize){
                player.sendMessage(Colorize.c("&cThat team is full"))
            } else {
                try{
                    gameManager.scoreboard.findTeam(island.color.formattedName()).get().addPlayer(player)
                } catch (ignore: JScoreboardException) {}
                island.players.add(player)
                gameManager.playerManager.giveTeamArmor(player, island)
            }
        }

        view.close()
        player.closeInventory()

        return null
    }

    override fun isInventory(view: InventoryView): Boolean {
        return view.title == name
    }
}