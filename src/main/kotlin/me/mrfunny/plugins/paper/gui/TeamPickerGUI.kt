package me.mrfunny.plugins.paper.gui

import dev.jcsoftware.jscoreboards.JScoreboardTeam
import dev.jcsoftware.jscoreboards.exception.JScoreboardException
import me.mrfunny.plugins.paper.gamemanager.GameManager
import me.mrfunny.plugins.paper.players.PlayerData
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

    override val inventory: Inventory = Bukkit.createInventory(null, 9, name)

    init {
        updateTeamSelector(player, this)
    }

    override fun handleClick(player: Player, itemStack: ItemStack, view: InventoryView): GUI? {
        if(!isInventory(view)) return null
        lateinit var clickedColor: IslandColor

        val itemName: String = ChatColor.stripColor(itemStack.itemMeta.displayName)!!
        for (color: IslandColor in IslandColor.values()) {
            if (itemName.equals(color.formattedName(), true)) {
                println("${player.name} was assigned to ${color.formattedName()}")
                clickedColor = color
                break
            }
        }

        val playerIsland: Optional<Island> = gameManager.world.islands.stream().filter { island -> island.isMember(player) }.findFirst()

        playerIsland.ifPresent{ island ->
            island.removeMember(player)
            if(findPlayerTeam(player) != null){
                findPlayerTeam(player)!!.removePlayer(player)
            }
            gameManager.scoreboard.findTeam(island.color.formattedName()).get().addPlayer(player)
            gameManager.updateScoreboard()
        }

        val selectedIsland: Optional<Island> = gameManager.world.islands.stream().filter { island -> island.color == clickedColor }.findFirst()

        if(selectedIsland.isPresent){
            val island: Island = selectedIsland.get()
            if(island.players.size == gameManager.world.maxTeamSize){
                val rus = PlayerData.PLAYERS[player.uniqueId]!!.isRussian();
                player.sendMessage(Colorize.c(if(rus) "Эта команда заполнена" else "&cThat team is full"))
                return Cancelled
            } else {
                try{
                    if(findPlayerTeam(player) != null){
                        findPlayerTeam(player)!!.removePlayer(player)
                    }
                    gameManager.scoreboard.findTeam(island.color.formattedName()).get().addPlayer(player)
                } catch (ignore: JScoreboardException) {}
                island.addMember(player)
                gameManager.guiManager.playerToGUIMap.entries.forEach {
                    if(it.value is TeamPickerGUI){
                        updateTeamSelector(it.key, it.value as TeamPickerGUI);
                    }
                }
            }
        }

        gameManager.playerManager.playerTeamSelector(player)

        view.close()
        player.closeInventory()

        return null
    }

    private fun findPlayerTeam(player: Player): JScoreboardTeam? {
        for(team in gameManager.scoreboard.teams) {
            if(team.isOnTeam(player.uniqueId)){
                return team
            }
        }
        return null
    }

    private fun updateTeamSelector(player: Player, gui: TeamPickerGUI?){
        if(gui == null) return
        gui.inventory.clear()
        gameManager.world.islands.forEach {
            val itemBuilder = ItemBuilder(it.color.woolMaterial())
                .setName("${it.color.getChatColor()}${it.color.formattedName()}")
                .addLoreLine(if(it.isMember(player))"&aSelected" else "&cNot selected")
                .addLoreLine("&a${it.players.size}/${gameManager.world.maxTeamSize} players")

            if(it.isMember(player)){
                itemBuilder.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1).hideEnchantments()
            }
            itemBuilder.addLoreLine("")
            for (teamPlayer in it.players) {
                itemBuilder.addLoreLine("${it.color.getChatColor()}${teamPlayer.name}")
            }
            gui.inventory.addItem(itemBuilder.toItemStack())
        }

        gui.inventory.addItem(ItemBuilder(Material.BARRIER).setName("&cExit").toItemStack())
    }
}