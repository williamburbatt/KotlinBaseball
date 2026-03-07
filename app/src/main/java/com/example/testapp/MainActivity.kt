package com.example.testapp

import android.os.Bundle
import androidx.activity.ComponentActivity
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.testapp.ui.screens.BoxScoreScreen
import com.example.testapp.ui.screens.DetailedPlayerCard
import com.example.testapp.ui.screens.GameListScreen
import com.example.testapp.ui.screens.MainHubScreen
import com.example.testapp.ui.screens.PlayerListScreen
import com.example.testapp.ui.screens.PlayerSearchScreen
import com.example.testapp.ui.screens.SportSelectionScreen
import com.example.testapp.ui.screens.TeamListScreen
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
                
                // Shared PlayerViewModel to handle direct detail viewing
                val globalPlayerViewModel: PlayerViewModel = hiltViewModel()
                val selectedPlayer by globalPlayerViewModel.selectedPlayer.collectAsStateWithLifecycle()
                val isPlayerLoading by globalPlayerViewModel.isLoading.collectAsStateWithLifecycle()

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
                                    onSearchClick = { navController.navigate(Screen.PlayerSearch) }
                                )
                            }
                            composable<Screen.PlayerSearch> {
                                PlayerSearchScreen(
                                    onPlayerClick = { playerId ->
                                        globalPlayerViewModel.selectPlayer(playerId)
                                    },
                                    onBack = { navController.popBackStack() }
                                )
                            }
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
                                    sharedTransitionScope = this@SharedTransitionLayout,
                                    animatedVisibilityScope = this@composable
                                )
                            }
                            composable<Screen.PlayerList> { 
                                PlayerListScreen(
                                    sharedTransitionScope = this@SharedTransitionLayout,
                                    animatedVisibilityScope = this@composable
                                )
                            }
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
                                BoxScoreScreen(onBack = { navController.popBackStack() })
                            }
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
