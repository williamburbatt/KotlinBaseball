package com.example.testapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.testapp.model.BoxScorePlayer
import com.example.testapp.ui.GameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoxScoreScreen(
    gameId: Int,
    onBack: () -> Unit,
    viewModel: GameViewModel = hiltViewModel()
) {
    val boxScore by viewModel.boxScore.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }

    LaunchedEffect(gameId) {
        viewModel.loadBoxScore(gameId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Box Score") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            if (isLoading) {
                androidx.compose.material3.CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (boxScore != null) {
                Column {
                    TabRow(selectedTabIndex = selectedTab) {
                        Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) {
                            Text("Away", modifier = Modifier.padding(16.dp))
                        }
                        Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) {
                            Text("Home", modifier = Modifier.padding(16.dp))
                        }
                    }
                    
                    val players = if (selectedTab == 0) boxScore!!.awayPlayers else boxScore!!.homePlayers
                    
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        item {
                            Text(
                                "BATTING",
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        
                        val batters = players.filter { (it.battingStats != null && it.battingStats.ab > 0) || (it.battingStats?.h ?: 0 > 0) }
                        items(batters) { player ->
                            BoxScorePlayerRow(player = player)
                        }

                        item {
                            Text(
                                "PITCHING",
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        
                        val pitchers = players.filter { it.pitchingStats != null && it.pitchingStats.ip != "0.0" }
                        items(pitchers) { player ->
                            BoxScorePitcherRow(player = player)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BoxScorePlayerRow(player: BoxScorePlayer) {
    val stats = player.battingStats ?: return
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = "${player.name} (${player.position})", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
            Text(text = "AB: ${stats.ab} R: ${stats.r} H: ${stats.h} RBI: ${stats.rbi} HR: ${stats.hr}", style = MaterialTheme.typography.bodySmall)
        }
        HorizontalDivider(modifier = Modifier.padding(top = 4.dp), thickness = 0.5.dp, color = Color.LightGray)
    }
}

@Composable
fun BoxScorePitcherRow(player: BoxScorePlayer) {
    val stats = player.pitchingStats ?: return
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = player.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
            Text(text = "IP: ${stats.ip} H: ${stats.h} ER: ${stats.er} K: ${stats.k} BB: ${stats.bb}", style = MaterialTheme.typography.bodySmall)
        }
        HorizontalDivider(modifier = Modifier.padding(top = 4.dp), thickness = 0.5.dp, color = Color.LightGray)
    }
}
