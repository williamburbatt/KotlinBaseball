package com.example.testapp.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.testapp.ui.screens.PlayerSearchScreen
import com.example.testapp.ui.viewmodels.PlayerViewModel

fun NavGraphBuilder.searchGraph(
    navController: NavController,
    globalPlayerViewModel: PlayerViewModel
) {
    composable<Screen.PlayerSearch> {
        PlayerSearchScreen(
            onPlayerClick = { playerId ->
                globalPlayerViewModel.selectPlayer(playerId)
            },
            onBack = { navController.popBackStack() }
        )
    }
}
