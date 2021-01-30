package me.mrfunny.plugins.paper.players

import me.mrfunny.plugins.paper.gamemanager.GameManager
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.lang.System.currentTimeMillis
import java.util.*

class PlayerData(uuid: UUID, val gameManager: GameManager) {
    companion object{
        val PLAYERS = hashMapOf<UUID, PlayerData>()

        fun disable(){
            PLAYERS.clear()
        }
    }
    var player: Player = Bukkit.getPlayer(uuid)!!
        private set

//    var axeIter: ListIterator<ToolLevel> = ToolLevel.values().iterator() as ListIterator<ToolLevel>
//    var pickIter: ListIterator<ToolLevel> = ToolLevel.values().iterator() as ListIterator<ToolLevel>

    var totalKills: Int = 0
    var totalDeaths: Int = 0
    var lastAttacker: UUID? = null
    var canBuyForSale: Boolean = false
    var isGeneratorMultiplier: Boolean = false
    var isStartPowerSelected: Boolean = false
    var isCompassUnlocked: Boolean = false
    var lastCombat: Long = currentTimeMillis()
    var lastShieldUse: Long = currentTimeMillis()
    var lastRespawn: Long = currentTimeMillis()
    var lastPlatformUse: Long = currentTimeMillis()
    var locale: String = gameManager.sql.getLocale(uuid)

    fun isRussian(): Boolean = locale == "ru_ru"

}