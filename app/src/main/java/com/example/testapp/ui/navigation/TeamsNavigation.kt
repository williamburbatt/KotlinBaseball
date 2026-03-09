package com.example.testapp.ui.navigation

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.testapp.ui.screens.PlayerListScreen
import com.example.testapp.ui.screens.SportSelectionScreen
import com.example.testapp.ui.screens.TeamListScreen

@OptIn(ExperimentalSharedTransitionApi::class)
fun NavGraphBuilder.teamsGraph(
    navController: NavController,
    sharedTransitionScope: SharedTransitionScope
) {
    composable<Screen.SportSelection> {
        SportSelectionScreen(onSportClick = { sportId ->
            navController.navigate(Screen.TeamList(sportId))
        })
    }
    
    composable<Screen.TeamList> { 
        TeamListScreen(
            onTeamClick = { teamId ->
                navController.navigate(Screen.PlayerList(teamId))
            },
            sharedTransitionScope = sharedTransitionScope,
            animatedVisibilityScope = this as AnimatedVisibilityScope
        )
    }
    
    composable<Screen.PlayerList> { 
        PlayerListScreen(
            sharedTransitionScope = sharedTransitionScope,
            animatedVisibilityScope = this as AnimatedVisibilityScope
        )
    }
}
