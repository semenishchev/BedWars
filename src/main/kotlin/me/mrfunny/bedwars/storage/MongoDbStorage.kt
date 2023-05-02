package me.mrfunny.bedwars.storage

import java.util.*

class MongoDbStorage(plugin: me.mrfunny.bedwars.BedWars) : BedwarsStorage {
    override fun getLocale(player: UUID?): String? {
        return "en_us"
    }

    override fun addKill(player: UUID) {
    }

    override fun addDeath(player: UUID) {
    }

    override fun addWin(player: UUID) {
    }

    override fun incrementCoins(player: UUID, amount: Int) {
    }

    override fun addWinAndCoins(player: UUID, coins: Int) {
    }

}
