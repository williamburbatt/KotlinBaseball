package com.example.testapp.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.testapp.model.Player
import com.example.testapp.model.YearlyStats
import com.example.testapp.ui.components.PlayerHeadshot
import com.example.testapp.ui.components.PositionBadge
import com.example.testapp.ui.components.SectionHeader
import com.example.testapp.ui.components.StatItem
import com.example.testapp.ui.components.TeamLogo
import com.example.testapp.ui.theme.TeamColors
import com.example.testapp.ui.viewmodels.PlayerViewModel
import java.util.Calendar

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
    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    val years = (2020..currentYear).reversed().toList()

    // Handle back button to close player detail overlay instead of navigating back to team list
    BackHandler(enabled = selectedPlayer != null) {
        viewModel.clearSelectedPlayer()
    }

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
            
            if (isLoading && selectedPlayer != null) {
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

@Composable
fun PlayerCard(player: Player, onClick: () -> Unit) {
    val teamColor = TeamColors.getTeamColor(player.teamId)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = teamColor.copy(alpha = 0.1f))
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
    val currentYear = Calendar.getInstance().get(Calendar.YEAR).toString()
    val teamColor = TeamColors.getTeamColor(player.teamId)
    var selectedTabIndex by remember { mutableStateOf(if (player.careerStats.isNotEmpty()) 0 else 1) }
    
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(teamColor)
                    .padding(top = 48.dp, bottom = 16.dp, start = 16.dp, end = 16.dp)
            ) {
                IconButton(
                    onClick = onClose,
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    PlayerHeadshot(
                        playerId = player.id,
                        modifier = Modifier.size(100.dp)
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
                            text = "Age: ${player.age} | B/T: ${player.bats}/${player.throws} | ${player.height}/${player.weight}",
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

            val accentColor = if (teamColor == Color.LightGray) Color(0xFF002D72) else teamColor

            Column(modifier = Modifier.padding(16.dp)) {
                val displayYear = player.currentStats?.year ?: currentYear
                Text(
                    "$displayYear SEASON HIGHLIGHTS",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Black,
                    color = accentColor
                )
                Spacer(modifier = Modifier.height(12.dp))
                CurrentSeasonCard(player, displayYear, accentColor)
            }

            Spacer(modifier = Modifier.height(8.dp))

            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = accentColor,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = accentColor
                    )
                }
            ) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    text = { Text("MLB CAREER", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    text = { Text("MiLB CAREER", fontWeight = FontWeight.Bold) }
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(16.dp)
            ) {
                val statsToShow = if (selectedTabIndex == 0) player.careerStats else player.milbStats
                
                if (statsToShow.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(top = 48.dp), contentAlignment = Alignment.Center) {
                            Text(
                                "No ${if (selectedTabIndex == 0) "MLB" else "Minor League"} stats available",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                } else {
                    item {
                        StatsTable(player, statsToShow)
                    }
                }
                
                item { Spacer(modifier = Modifier.height(32.dp)) }
            }
        }
    }
}

@Composable
fun CurrentSeasonCard(player: Player, year: String, accentColor: Color) {
    val stats = player.currentStats
    val isPitcher = player.position.contains("P")
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = accentColor.copy(alpha = 0.05f)),
        shape = RoundedCornerShape(8.dp)
    ) {
        if (stats == null) {
            Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                Text("No $year stats available yet", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.outline)
            }
        } else {
            Row(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                if (!isPitcher) {
                    HighlightStat("AVG", stats.avg, accentColor)
                    HighlightStat("HR", stats.hr.toString(), accentColor)
                    HighlightStat("RBI", stats.rbi.toString(), accentColor)
                    HighlightStat("OPS", stats.ops, accentColor)
                } else {
                    HighlightStat("ERA", stats.era, accentColor)
                    HighlightStat("W-L", "${stats.w}-${stats.l}", accentColor)
                    HighlightStat("SO", stats.k.toString(), accentColor)
                    HighlightStat("WHIP", stats.whip, accentColor)
                }
            }
        }
    }
}

@Composable
fun HighlightStat(label: String, value: String, accentColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label, 
            style = MaterialTheme.typography.labelSmall, 
            color = accentColor,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = value, 
            style = MaterialTheme.typography.headlineSmall, 
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun StatsTable(player: Player, stats: List<YearlyStats>) {
    val isPitcher = player.position.contains("P")
    val headers = if (!isPitcher) {
        listOf("Year", "Team", "G", "HR", "RBI", "AVG", "OPS")
    } else {
        listOf("Year", "Team", "W-L", "ERA", "IP", "SO", "WHIP")
    }

    Column(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface, RoundedCornerShape(4.dp))) {
        Surface(color = Color(0xFF455A64)) {
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                headers.forEachIndexed { index, header ->
                    Text(
                        text = header,
                        modifier = Modifier.weight(if (index < 2) 1.2f else 1f),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        stats.forEach { yearly ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!isPitcher) {
                    StatCell(yearly.year, 1.2f, isYear = true)
                    StatCell(yearly.team, 1.2f)
                    StatCell(yearly.games.toString())
                    StatCell(yearly.hr.toString())
                    StatCell(yearly.rbi.toString())
                    StatCell(yearly.avg)
                    StatCell(yearly.ops)
                } else {
                    StatCell(yearly.year, 1.2f, isYear = true)
                    StatCell(yearly.team, 1.2f)
                    StatCell("${yearly.w}-${yearly.l}")
                    StatCell(yearly.era)
                    StatCell(yearly.ip)
                    StatCell(yearly.k.toString())
                    StatCell(yearly.whip)
                }
            }
            HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
        }
    }
}

@Composable
fun RowScope.StatCell(text: String, weight: Float = 1f, isYear: Boolean = false) {
    Text(
        text = text,
        modifier = Modifier.weight(weight),
        style = MaterialTheme.typography.bodySmall,
        textAlign = TextAlign.Center,
        fontWeight = if (isYear) FontWeight.Bold else FontWeight.Normal,
        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
        maxLines = 1
    )
}
