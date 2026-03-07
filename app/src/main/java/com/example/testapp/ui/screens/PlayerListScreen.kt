package com.example.testapp.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.testapp.model.Player
import com.example.testapp.ui.components.PositionBadge
import com.example.testapp.ui.components.SectionHeader
import com.example.testapp.ui.components.StatItem
import com.example.testapp.ui.components.TeamLogo
import com.example.testapp.ui.viewmodels.PlayerViewModel

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PlayerListScreen(
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    viewModel: PlayerViewModel = hiltViewModel()
) {
    val groupedPlayers by viewModel.groupedPlayers.collectAsStateWithLifecycle()
    val selectedYear by viewModel.selectedYear.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val selectedPlayer by viewModel.selectedPlayer.collectAsStateWithLifecycle()
    var showYearDropdown by remember { mutableStateOf(false) }
    
    val teamId = viewModel.teamId
    val years = (2020..2024).reversed().toList()

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
                    TeamLogo(
                        teamId = teamId, 
                        modifier = Modifier.size(48.dp),
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = animatedVisibilityScope
                    )
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
            if (isLoading && selectedPlayer == null) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    groupedPlayers.forEach { group ->
                        item(key = "header_${group.title}") {
                            SectionHeader(title = group.title)
                        }
                        items(
                            items = group.players,
                            key = { player -> player.id }
                        ) { player ->
                            PlayerCard(player = player, onClick = { viewModel.selectPlayer(player.id.toInt()) })
                        }
                        item { Spacer(modifier = Modifier.height(16.dp)) }
                    }
                }
            }

            AnimatedVisibility(
                visible = selectedPlayer != null,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                selectedPlayer?.let { player ->
                    DetailedPlayerCard(
                        player = player,
                        onClose = { viewModel.clearSelectedPlayer() }
                    )
                }
            }
        }
    }
}

@Composable
fun PlayerCard(player: Player, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable(onClick = onClick),
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
                player.currentStats?.let { stats ->
                    if (player.position != "P" && player.position != "SP" && player.position != "RP") {
                        Row(modifier = Modifier.padding(top = 2.dp)) {
                            StatItem(label = "AVG", value = stats.avg)
                            StatItem(label = "HR", value = stats.hr.toString())
                            StatItem(label = "RBI", value = stats.rbi.toString())
                        }
                    } else {
                        Row(modifier = Modifier.padding(top = 2.dp)) {
                            StatItem(label = "ERA", value = stats.era)
                            StatItem(label = "W-L", value = "${stats.w}-${stats.l}")
                            StatItem(label = "SO", value = stats.k.toString())
                            StatItem(label = "IP", value = stats.ip)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DetailedPlayerCard(player: Player, onClose: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFBA0C2F)) // FanGraphs red-ish
                    .padding(top = 48.dp, bottom = 16.dp, start = 16.dp, end = 16.dp)
            ) {
                IconButton(
                    onClick = onClose,
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = player.headshotUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = player.name,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "${player.team} | #${player.number}",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        Text(
                            text = "Age: ${player.age} | Bats/Throws: ${player.bats}/${player.throws} | ${player.height}/${player.weight}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Surface(
                        color = Color.White.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = player.position,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(16.dp)
            ) {
                // Quick Look Section
                item {
                    Text(
                        "QUICK LOOK",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF558B2F)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    QuickLookCard(player)
                    Spacer(modifier = Modifier.height(24.dp))
                }

                // Stats Table Section
                item {
                    Text(
                        "SEASON STATS",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF558B2F)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                item {
                    StatsTable(player)
                }
            }
        }
    }
}

@Composable
fun QuickLookCard(player: Player) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
        shape = RoundedCornerShape(4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            val stats = player.currentStats ?: return@Column
            val isPitcher = player.position.contains("P")
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                if (!isPitcher) {
                    QuickStat("G", stats.games.toString())
                    QuickStat("PA", stats.pa.toString())
                    QuickStat("HR", stats.hr.toString())
                    QuickStat("AVG", stats.avg)
                    QuickStat("OBP", stats.obp)
                    QuickStat("OPS", stats.ops)
                } else {
                    QuickStat("W", stats.w.toString())
                    QuickStat("L", stats.l.toString())
                    QuickStat("ERA", stats.era)
                    QuickStat("SO", stats.k.toString())
                    QuickStat("IP", stats.ip)
                    QuickStat("WHIP", stats.whip)
                }
            }
        }
    }
}

@Composable
fun QuickStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
        Text(value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace)
    }
}

@Composable
fun StatsTable(player: Player) {
    val isPitcher = player.position.contains("P")
    val headers = if (!isPitcher) {
        listOf("Season", "Team", "G", "PA", "HR", "AVG", "OBP", "SLG", "OPS")
    } else {
        listOf("Season", "Team", "W", "L", "ERA", "G", "GS", "IP", "SO", "WHIP")
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        // Table Header
        Surface(color = Color(0xFF455A64)) {
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                headers.forEachIndexed { index, header ->
                    Text(
                        text = header,
                        modifier = Modifier.weight(if (index < 2) 1.5f else 1f),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        player.careerStats.forEach { stats ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!isPitcher) {
                    StatCell(stats.year, 1.5f)
                    StatCell(stats.team, 1.5f)
                    StatCell(stats.games.toString())
                    StatCell(stats.pa.toString())
                    StatCell(stats.hr.toString())
                    StatCell(stats.avg)
                    StatCell(stats.obp)
                    StatCell(stats.slg)
                    StatCell(stats.ops)
                } else {
                    StatCell(stats.year, 1.5f)
                    StatCell(stats.team, 1.5f)
                    StatCell(stats.w.toString())
                    StatCell(stats.l.toString())
                    StatCell(stats.era)
                    StatCell(stats.games.toString())
                    StatCell("0") // GS missing in YearlyStats for now
                    StatCell(stats.ip)
                    StatCell(stats.k.toString())
                    StatCell(stats.whip)
                }
            }
            HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
        }
    }
}

@Composable
fun RowScope.StatCell(text: String, weight: Float = 1f) {
    Text(
        text = text,
        modifier = Modifier.weight(weight),
        style = MaterialTheme.typography.bodySmall,
        textAlign = TextAlign.Center,
        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
        maxLines = 1
    )
}
