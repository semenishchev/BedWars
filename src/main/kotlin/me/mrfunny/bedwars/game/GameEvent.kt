package me.mrfunny.bedwars.game

enum class GameEvent(val displayName: String) {
    RUBY_TWO("Ruby II: {time}"),
    RUBY_THREE("Ruby III: {time}"),
    ALL_BEDS_DESTRUCTION("Bed destruction: {time}"),
    GAME_END("Game end: {time}");
}