package com.example.testapp.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.testapp.ui.screens.LeadersScreen
import com.example.testapp.ui.viewmodels.PlayerViewModel

fun NavGraphBuilder.leadersGraph(
    navController: NavController,
    globalPlayerViewModel: PlayerViewModel
) {
    composable<Screen.Leaders> {
        LeadersScreen(
            onBack = { navController.popBackStack() },
            onPlayerClick = { playerId ->
                globalPlayerViewModel.selectPlayer(playerId)
            }
        )
    }
}
