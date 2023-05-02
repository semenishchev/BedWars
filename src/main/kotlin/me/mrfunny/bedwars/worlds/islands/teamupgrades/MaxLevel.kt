package me.mrfunny.bedwars.worlds.islands.teamupgrades

enum class MaxLevel {
    ZERO, ONE, TWO, THREE, FOUR;

    fun toInt(): Int{
        return when(this){
            ZERO -> 0
            ONE -> 1
            TWO -> 2
            THREE -> 3
            FOUR -> 4
        }
    }

    companion object{
        fun fromInt(int: Int): MaxLevel {
            return when(int){
                1 -> ONE
                2 -> TWO
                3 -> THREE
                4 -> FOUR
                else -> ZERO
            }
        }
    }
}