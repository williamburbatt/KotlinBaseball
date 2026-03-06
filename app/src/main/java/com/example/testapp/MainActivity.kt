package com.example.testapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.testapp.model.Player
import com.example.testapp.ui.PlayerViewModel
import com.example.testapp.ui.theme.TestAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TestAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PlayerListScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun PlayerListScreen(
    modifier: Modifier = Modifier,
    viewModel: PlayerViewModel = viewModel()
) {
    val players by viewModel.players.collectAsState()

    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(players) { player ->
            PlayerCard(player = player)
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
            Text(text = "Team: ${player.team}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Pos: ${player.position}", style = MaterialTheme.typography.bodySmall)
            Text(text = "AVG: ${player.average} | HR: ${player.homeRuns} | RBI: ${player.rbi}", 
                 style = MaterialTheme.typography.bodyLarge)
        }
    }
}
