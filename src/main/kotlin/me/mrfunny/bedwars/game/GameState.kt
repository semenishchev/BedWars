package me.mrfunny.bedwars.game

enum class GameState {
    PRELOBBY, LOBBY, STARTING, ACTIVE, WON, RESET;

    fun isPreGame() = this == LOBBY || this == STARTING
}