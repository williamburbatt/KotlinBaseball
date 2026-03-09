package com.example.testapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.testapp.ui.navigation.NavTransitions
import com.example.testapp.ui.navigation.Screen
import com.example.testapp.ui.navigation.gamesGraph
import com.example.testapp.ui.navigation.leadersGraph
import com.example.testapp.ui.navigation.searchGraph
import com.example.testapp.ui.navigation.teamsGraph
import com.example.testapp.ui.screens.DetailedPlayerCard
import com.example.testapp.ui.screens.MainHubScreen
import com.example.testapp.ui.theme.TestAppTheme
import com.example.testapp.ui.viewmodels.PlayerViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalSharedTransitionApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TestAppTheme {
                val navController = rememberNavController()
                
                // Shared PlayerViewModel to handle direct detail viewing across screens
                val globalPlayerViewModel: PlayerViewModel = hiltViewModel()
                val selectedPlayer by globalPlayerViewModel.selectedPlayer.collectAsStateWithLifecycle()
                val isPlayerLoading by globalPlayerViewModel.isLoading.collectAsStateWithLifecycle()

                // Intercept back button if player card is open
                BackHandler(enabled = selectedPlayer != null) {
                    globalPlayerViewModel.clearSelectedPlayer()
                }

                Box(modifier = Modifier.fillMaxSize()) {
                    SharedTransitionLayout {
                        NavHost(
                            navController = navController,
                            startDestination = Screen.MainHub,
                            enterTransition = { NavTransitions.enter() },
                            exitTransition = { NavTransitions.exit() },
                            popEnterTransition = { NavTransitions.popEnter() },
                            popExitTransition = { NavTransitions.popExit() }
                        ) {
                            composable<Screen.MainHub> {
                                MainHubScreen(
                                    onTeamsClick = { navController.navigate(Screen.SportSelection) },
                                    onGamesClick = { navController.navigate(Screen.GameList) },
                                    onSearchClick = { navController.navigate(Screen.PlayerSearch) },
                                    onLeadersClick = { navController.navigate(Screen.Leaders) }
                                )
                            }
                            
                            // Modular Features
                            teamsGraph(navController, this@SharedTransitionLayout)
                            gamesGraph(navController, globalPlayerViewModel)
                            searchGraph(navController, globalPlayerViewModel)
                            leadersGraph(navController, globalPlayerViewModel)
                        }
                    }

                    // Global Player Detail Overlay
                    AnimatedVisibility(
                        visible = selectedPlayer != null,
                        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
                    ) {
                        selectedPlayer?.let { player ->
                            DetailedPlayerCard(
                                player = player,
                                onClose = { globalPlayerViewModel.clearSelectedPlayer() }
                            )
                        }
                    }
                    
                    if (isPlayerLoading && selectedPlayer != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.3f))
                                .clickable(enabled = false) {}
                        ) {
                            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                        }
                    }
                }
            }
        }
    }
}
