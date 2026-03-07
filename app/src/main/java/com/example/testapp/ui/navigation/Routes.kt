package com.example.testapp.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface Screen {
    @Serializable
    data object MainHub : Screen

    @Serializable
    data object SportSelection : Screen

    @Serializable
    data class TeamList(val sportId: Int) : Screen

    @Serializable
    data class PlayerList(val teamId: Int) : Screen

    @Serializable
    data object GameList : Screen

    @Serializable
    data class BoxScore(
        val gameId: Int,
        val awayTeam: String,
        val homeTeam: String
    ) : Screen

    @Serializable
    data object PlayerSearch : Screen
}
