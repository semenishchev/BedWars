package me.mrfunny.plugins.paper.gui.shops.teamupgrades

enum class MaxLevel {
    ONE, TWO, THREE, FOUR;

    fun toInt(): Int{
        return when(this){
            ONE -> 1
            TWO -> 2
            THREE -> 3
            FOUR -> 4
        }
    }

    companion object{
        fun fromInt(int: Int): MaxLevel {
            when(int){
                1 -> ONE
                2 -> TWO
                3 -> THREE
                4 -> FOUR
            }
            return ONE
        }
    }
}