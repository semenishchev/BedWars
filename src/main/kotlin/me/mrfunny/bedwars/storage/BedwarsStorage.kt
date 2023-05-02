package me.mrfunny.bedwars.storage

import java.util.*

interface BedwarsStorage {
    fun getLocale(player: UUID?): String?;
    fun addKill(player: UUID);
    fun addDeath(player: UUID)
    fun addWin(player: UUID);
    fun incrementCoins(player: UUID, amount: Int)
    fun addWinAndCoins(player: UUID, coins: Int)
}