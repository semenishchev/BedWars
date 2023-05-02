package me.mrfunny.bedwars.players

import me.mrfunny.bedwars.game.GameManager
import me.mrfunny.bedwars.worlds.islands.Island
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.lang.ref.WeakReference
import java.util.*

class PlayerData(player: Player, val gameManager: GameManager) {
    companion object {
        private val PLAYERS = hashMapOf<UUID, PlayerData>()
        private lateinit var lastManager: GameManager

        fun init(gameManager: GameManager) {
            if(this::lastManager.isInitialized) return
            lastManager = gameManager
        }

        fun new(player: Player, gameManager: GameManager): PlayerData {
            val data = PlayerData(player, gameManager)
            PLAYERS[player.uniqueId] = data
            return data
        }

        fun disable() {
            PLAYERS.clear()
        }

        fun get(uuid: UUID): PlayerData? {
            val data = PLAYERS[uuid]
            if(data == null) {
                val player = Bukkit.getPlayer(uuid) ?: return null
                return new(player, lastManager)
            }
            return data
        }

        fun get(player: Player): PlayerData {
            return PLAYERS[player.uniqueId] ?: return new(player, lastManager)
        }

        fun values() = PLAYERS.values

        fun remove(uuid: UUID) = PLAYERS.remove(uuid)
        fun remove(player: Player) = PLAYERS.remove(player.uniqueId)

        fun all() = PLAYERS.values
    }
    private val player: WeakReference<Player> = WeakReference(player)

    var totalKills: Int = 0
    var totalDeaths: Int = 0
    var totalCoins: Int = 0
        private set
    var lastAttacker: UUID? = null
    var canBuyForSale: Boolean = false
    var isGeneratorMultiplier: Boolean = false
    var isStartPowerSelected: Boolean = false
    var isCompassUnlocked: Boolean = false
    var lastCombat: Long = 0
    var lastShieldUse: Long = 0
    var lastRespawn: Long = 0
    var lastPlatformUse: Long = 0
    lateinit var assignedIsland: Island
    val locale: String = gameManager.storage.getLocale(getPlayer()?.uniqueId)?.lowercase() ?: "en_us"

//    fun isRussian(): Boolean = false

    fun addCoinsAndSave(amount: Int) {
        totalCoins += amount
        gameManager.storage.incrementCoins(getPlayer()!!.uniqueId, amount)
    }

    fun addCoinsAndSave(amount: Int, `for`: String) {
        totalCoins += amount
        val player = getPlayer()!!
        gameManager.storage.incrementCoins(player.uniqueId, amount)
        player.sendMessage(Component.text("+$amount for $`for`"))
    }

    fun getPlayer() = player.get();

}