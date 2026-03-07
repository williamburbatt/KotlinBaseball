package com.example.testapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.testapp.ui.screens.BoxScoreScreen
import com.example.testapp.ui.screens.GameListScreen
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
                NavHost(navController = navController, startDestination = "main") {
                    composable("main") {
                        MainHubScreen(
                            onTeamsClick = { navController.navigate("sports") },
                            onGamesClick = { navController.navigate("games") }
                        )
                    }
                    composable("sports") {
                        SportSelectionScreen(onSportClick = { sportId ->
                            navController.navigate("teams/$sportId")
                        })
                    }
                    composable(
                        "teams/{sportId}",
                        arguments = listOf(navArgument("sportId") { type = NavType.IntType })
                    ) { 
                        TeamListScreen(onTeamClick = { teamId ->
                            navController.navigate("players/$teamId")
                        })
                    }
                    composable(
                        "players/{teamId}",
                        arguments = listOf(navArgument("teamId") { type = NavType.IntType })
                    ) { backStackEntry ->
                        val teamId = backStackEntry.arguments?.getInt("teamId") ?: 0
                        PlayerListScreen(teamId = teamId)
                    }
                    composable("games") {
                        GameListScreen(onGameClick = { gameId ->
                            navController.navigate("boxscore/$gameId")
                        })
                    }
                    composable(
                        "boxscore/{gameId}",
                        arguments = listOf(navArgument("gameId") { type = NavType.IntType })
                    ) { backStackEntry ->
                        val gameId = backStackEntry.arguments?.getInt("gameId") ?: 0
                        BoxScoreScreen(gameId = gameId, onBack = { navController.popBackStack() })
                    }
                }
            }
        }
    }
}
