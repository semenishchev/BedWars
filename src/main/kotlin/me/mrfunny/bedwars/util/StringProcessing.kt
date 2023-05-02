package me.mrfunny.bedwars.util

object StringProcessing {
    fun calculatePadsForCenter(string: String, amount: Int): Int {
        val len = string.length
        if(amount <= len) return 0
        return (amount - len) / 2
    }
}