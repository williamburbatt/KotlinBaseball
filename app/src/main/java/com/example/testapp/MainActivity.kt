package com.example.testapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.testapp.model.Player
import com.example.testapp.model.Team
import com.example.testapp.ui.PlayerViewModel
import com.example.testapp.ui.TeamViewModel
import com.example.testapp.ui.theme.TestAppTheme
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TestAppTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "sports") {
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
                }
            }
        }
    }
}

@Composable
fun SportSelectionScreen(onSportClick: (Int) -> Unit) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Select Sport",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            SportCard(name = "Major League Baseball", id = 1, onClick = { onSportClick(1) })
            SportCard(name = "Minor League Baseball", id = 11, onClick = { onSportClick(11) })
        }
    }
}

@Composable
fun SportCard(name: String, id: Int, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = name, style = MaterialTheme.typography.headlineSmall)
        }
    }
}

@Composable
fun TeamListScreen(
    onTeamClick: (Int) -> Unit,
    viewModel: TeamViewModel = hiltViewModel()
) {
    val teams by viewModel.teams.collectAsState()

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        LazyColumn(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            items(teams) { team ->
                TeamCard(team = team, onClick = { onTeamClick(team.id) })
            }
        }
    }
}

@Composable
fun TeamCard(team: Team, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = team.name, style = MaterialTheme.typography.headlineSmall)
            Text(text = team.teamName, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerListScreen(
    teamId: Int,
    viewModel: PlayerViewModel = hiltViewModel()
) {
    val players by viewModel.players.collectAsState()
    val selectedYear by viewModel.selectedYear.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var showYearDropdown by remember { mutableStateOf(false) }
    
    val years = (2020..2026).reversed().toList()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp, start = 16.dp, end = 16.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$selectedYear Season Stats",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                Box {
                    Button(onClick = { showYearDropdown = true }) {
                        Text("Year: $selectedYear")
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    }
                    DropdownMenu(
                        expanded = showYearDropdown,
                        onDismissRequest = { showYearDropdown = false }
                    ) {
                        years.forEach { year ->
                            DropdownMenuItem(
                                text = { Text(year.toString()) },
                                onClick = {
                                    viewModel.updateYear(year)
                                    showYearDropdown = false
                                }
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(players) { player ->
                        PlayerCard(player = player)
                    }
                }
            }
        }
    }
}

@Composable
fun PlayerCard(player: Player) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = player.name, style = MaterialTheme.typography.headlineSmall)
            Row {
                Text(text = "Pos: ${player.position}", style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = String.format(Locale.US, "AVG: %.3f | HR: %d | RBI: %d", 
                        player.average, player.homeRuns, player.rbi),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}
