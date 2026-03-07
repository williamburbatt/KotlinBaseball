package com.example.testapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.testapp.ui.navigation.Screen
import com.example.testapp.ui.screens.BoxScoreScreen
import com.example.testapp.ui.screens.GameListScreen
import com.example.testapp.ui.screens.LogoGalleryScreen
import com.example.testapp.ui.screens.MainHubScreen
import com.example.testapp.ui.screens.PlayerListScreen
import com.example.testapp.ui.screens.SportSelectionScreen
import com.example.testapp.ui.screens.TeamListScreen
import com.example.testapp.ui.theme.TestAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TestAppTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = Screen.MainHub) {
                    composable<Screen.MainHub> {
                        MainHubScreen(
                            onTeamsClick = { navController.navigate(Screen.SportSelection) },
                            onGamesClick = { navController.navigate(Screen.GameList) },
                            onLogosClick = { navController.navigate(Screen.LogoGallery) }
                        )
                    }
                    composable<Screen.SportSelection> {
                        SportSelectionScreen(onSportClick = { sportId ->
                            navController.navigate(Screen.TeamList(sportId))
                        })
                    }
                    composable<Screen.TeamList> { 
                        TeamListScreen(onTeamClick = { teamId ->
                            navController.navigate(Screen.PlayerList(teamId))
                        })
                    }
                    composable<Screen.PlayerList> { 
                        PlayerListScreen()
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
                    composable<Screen.LogoGallery> {
                        LogoGalleryScreen(onBack = { navController.popBackStack() })
                    }
                }
            }
        }
    }
}
