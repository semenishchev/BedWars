package me.mrfunny.plugins.paper.gamemanager

enum class GameEvent(val displayName: String) {
    RUBY_TWO("Ruby II: {time}"),
    RUBY_THREE("Ruby III: {time}"),
    ALL_BEDS_DESTRUCTION("Bed destruction: {time}"),
    GAME_END("Game end: {time}");
}