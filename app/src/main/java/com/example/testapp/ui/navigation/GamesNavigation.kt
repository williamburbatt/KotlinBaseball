package com.example.testapp.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.testapp.ui.screens.BoxScoreScreen
import com.example.testapp.ui.screens.GameListScreen
import com.example.testapp.ui.viewmodels.PlayerViewModel

fun NavGraphBuilder.gamesGraph(
    navController: NavController,
    globalPlayerViewModel: PlayerViewModel
) {
    composable<Screen.GameList> {
        GameListScreen(onGameClick = { game ->
            navController.navigate(
                Screen.BoxScore(
                    gameId = game.id,
                    awayTeam = game.awayTeam,
                    homeTeam = game.homeTeam
                )
            )
        })
    }

    composable<Screen.BoxScore> {
        BoxScoreScreen(
            onBack = { navController.popBackStack() },
            onPlayerClick = { playerId ->
                globalPlayerViewModel.selectPlayer(playerId)
            }
        )
    }
}
