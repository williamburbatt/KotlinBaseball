package com.example.testapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.testapp.model.Player
import com.example.testapp.model.Team
import com.example.testapp.ui.PlayerGroup
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
                text = "Baseball Stats",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            
            SportCard(name = "Major League", description = "MLB - The Big Leagues", onClick = { onSportClick(1) })
            SportCard(name = "Minor League", description = "MiLB - The Future Stars", onClick = { onSportClick(11) })
        }
    }
}

@Composable
fun SportCard(name: String, description: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(text = name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(text = description, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TeamLogo(teamId = team.id, modifier = Modifier.size(64.dp))
            
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = team.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text(text = team.teamName, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.outline)
            }
        }
    }
}

@Composable
fun TeamLogo(teamId: Int, modifier: Modifier = Modifier) {
    val logoUrl = "https://www.mlbstatic.com/team-logos/$teamId.svg"
    
    Surface(
        modifier = modifier,
        shape = CircleShape,
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Box(
            modifier = Modifier.padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(logoUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "Team Logo",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerListScreen(
    teamId: Int,
    viewModel: PlayerViewModel = hiltViewModel()
) {
    val groupedPlayers by viewModel.groupedPlayers.collectAsState()
    val selectedYear by viewModel.selectedYear.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var showYearDropdown by remember { mutableStateOf(false) }
    
    val years = (2020..2026).reversed().toList()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Surface(shadowElevation = 4.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 48.dp, start = 16.dp, end = 16.dp, bottom = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TeamLogo(teamId = teamId, modifier = Modifier.size(48.dp))
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Roster", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text(text = "Season $selectedYear", style = MaterialTheme.typography.bodySmall)
                    }
                    Box {
                        Button(
                            onClick = { showYearDropdown = true },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(selectedYear.toString())
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
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    groupedPlayers.forEach { group ->
                        item {
                            SectionHeader(title = group.title)
                        }
                        items(group.players) { player ->
                            PlayerCard(player = player)
                        }
                        item { Spacer(modifier = Modifier.height(16.dp)) }
                    }
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 1.5.sp
        )
        HorizontalDivider(modifier = Modifier.padding(top = 4.dp), thickness = 2.dp, color = MaterialTheme.colorScheme.primaryContainer)
    }
}

@Composable
fun PlayerCard(player: Player) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PositionBadge(position = player.position)
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(text = player.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                if (player.position != "P" && player.position != "SP" && player.position != "RP") {
                    Row(modifier = Modifier.padding(top = 2.dp)) {
                        StatItem(label = "AVG", value = String.format(Locale.US, ".%03d", (player.average * 1000).toInt()))
                        StatItem(label = "HR", value = player.homeRuns.toString())
                        StatItem(label = "RBI", value = player.rbi.toString())
                    }
                } else {
                    Row(modifier = Modifier.padding(top = 2.dp)) {
                        StatItem(label = "ERA", value = player.era)
                        StatItem(label = "W-L", value = "${player.wins}-${player.losses}")
                        StatItem(label = "SO", value = player.strikeOuts.toString())
                        StatItem(label = "IP", value = player.inningsPitched)
                    }
                }
            }
        }
    }
}

@Composable
fun PositionBadge(position: String) {
    Surface(
        color = MaterialTheme.colorScheme.primary,
        shape = RoundedCornerShape(4.dp),
        modifier = Modifier.size(width = 36.dp, height = 24.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = position,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Row(modifier = Modifier.padding(end = 12.dp)) {
        Text(text = "$label: ", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
        Text(text = value, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
    }
}
