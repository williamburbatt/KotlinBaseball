package com.example.testapp.ui.screens

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.testapp.api.BoxscorePlayer
import com.example.testapp.api.BoxscorePlayerStats
import com.example.testapp.api.BoxscoreResponse
import com.example.testapp.api.BoxscoreTeam
import com.example.testapp.api.BoxscoreTeamStats
import com.example.testapp.api.BoxscoreTeams
import com.example.testapp.api.Inning
import com.example.testapp.api.InningScore
import com.example.testapp.api.LinescoreResponse
import com.example.testapp.api.LinescoreTeam
import com.example.testapp.api.LinescoreTeams
import com.example.testapp.api.Person
import com.example.testapp.api.Position
import com.example.testapp.api.Team
import com.example.testapp.ui.viewmodels.BoxScoreViewModel
import com.example.testapp.ui.components.TeamLogo
import com.example.testapp.ui.components.PlayerHeadshot
import com.example.testapp.ui.theme.TestAppTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun BoxScoreScreen(
    onBack: () -> Unit,
    viewModel: BoxScoreViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    BoxScoreContent(
        uiState = uiState,
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoxScoreContent(
    uiState: com.example.testapp.ui.viewmodels.BoxScoreUiState,
    onBack: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                ),
                title = { Text("Box Score", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            val boxscore = uiState.boxscore
            if (uiState.isLoading && boxscore == null) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (boxscore != null) {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    item {
                        ScoreboardHeader(
                            awayTeam = boxscore.teams.away,
                            homeTeam = boxscore.teams.home,
                            linescore = uiState.linescore
                        )
                    }
                    
                    item {
                        LinescoreTable(
                            awayTeam = boxscore.teams.away,
                            homeTeam = boxscore.teams.home,
                            linescore = uiState.linescore
                        )
                    }

                    item {
                        TeamStatsTabs(
                            selectedTab = selectedTab,
                            onTabSelected = { selectedTab = it },
                            awayName = boxscore.teams.away.team.teamName ?: "Away",
                            homeName = boxscore.teams.home.team.teamName ?: "Home",
                            awayId = boxscore.teams.away.team.id,
                            homeId = boxscore.teams.home.team.id
                        )
                    }

                    val team = if (selectedTab == 0) boxscore.teams.away else boxscore.teams.home
                    
                    item {
                        Text(
                            "BATTING",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }

                    val batters = team.players.values
                        .filter { it.stats.batting != null && (it.stats.batting?.atBats ?: 0) > 0 }
                        .sortedByDescending { it.stats.batting?.atBats }
                        
                    items(batters) { player ->
                        BoxscorePlayerRow(
                            name = player.person.fullName,
                            pos = player.position.abbreviation ?: "",
                            ab = player.stats.batting?.atBats ?: 0,
                            r = player.stats.batting?.runs ?: 0,
                            h = player.stats.batting?.hits ?: 0,
                            rbi = player.stats.batting?.rbi ?: 0,
                            playerId = player.person.id
                        )
                    }

                    item {
                        Text(
                            "PITCHING",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }

                    val pitchers = team.players.values
                        .filter { it.stats.pitching != null && it.stats.pitching?.inningsPitched != null && it.stats.pitching?.inningsPitched != "0.0" && it.stats.pitching?.inningsPitched != "0" }

                    items(pitchers) { player ->
                        BoxscorePitcherRow(
                            name = player.person.fullName,
                            ip = player.stats.pitching?.inningsPitched ?: "0.0",
                            h = player.stats.pitching?.hits ?: 0,
                            er = player.stats.pitching?.earnedRuns ?: 0,
                            k = player.stats.pitching?.strikeOuts ?: 0,
                            playerId = player.person.id
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ScoreboardHeader(awayTeam: BoxscoreTeam, homeTeam: BoxscoreTeam, linescore: LinescoreResponse?) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Away
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                TeamLogo(teamId = awayTeam.team.id, modifier = Modifier.size(64.dp))
                Text(
                    text = awayTeam.team.name ?: "",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // Score
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "${linescore?.teams?.away?.runs ?: 0}",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "vs",
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                Text(
                    text = "${linescore?.teams?.home?.runs ?: 0}",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Black
                )
            }

            // Home
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                TeamLogo(teamId = homeTeam.team.id, modifier = Modifier.size(64.dp))
                Text(
                    text = homeTeam.team.name ?: "",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun LinescoreTable(awayTeam: BoxscoreTeam, homeTeam: BoxscoreTeam, linescore: LinescoreResponse?) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(modifier = Modifier.fillMaxWidth()) {
                Spacer(modifier = Modifier.width(100.dp))
                Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.SpaceEvenly) {
                    (1..9).forEach { i ->
                        Text("$i", color = MaterialTheme.colorScheme.outline, fontSize = 12.sp, modifier = Modifier.width(20.dp), textAlign = TextAlign.Center)
                    }
                }
                Row(modifier = Modifier.width(75.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                    Text("R", color = MaterialTheme.colorScheme.outline, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(25.dp), textAlign = TextAlign.Center)
                    Text("H", color = MaterialTheme.colorScheme.outline, fontSize = 12.sp, modifier = Modifier.width(25.dp), textAlign = TextAlign.Center)
                    Text("E", color = MaterialTheme.colorScheme.outline, fontSize = 12.sp, modifier = Modifier.width(25.dp), textAlign = TextAlign.Center)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Away Row
            InningRow(
                logoId = awayTeam.team.id,
                name = awayTeam.team.abbreviation ?: "AWY",
                innings = linescore?.innings,
                isAway = true,
                r = linescore?.teams?.away?.runs ?: 0,
                h = linescore?.teams?.away?.hits ?: 0,
                e = linescore?.teams?.away?.errors ?: 0
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Home Row
            InningRow(
                logoId = homeTeam.team.id,
                name = homeTeam.team.abbreviation ?: "HOM",
                innings = linescore?.innings,
                isAway = false,
                r = linescore?.teams?.home?.runs ?: 0,
                h = linescore?.teams?.home?.hits ?: 0,
                e = linescore?.teams?.home?.errors ?: 0
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun InningRow(logoId: Int, name: String, innings: List<Inning>?, isAway: Boolean, r: Int, h: Int, e: Int) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Row(modifier = Modifier.width(100.dp), verticalAlignment = Alignment.CenterVertically) {
            TeamLogo(teamId = logoId, modifier = Modifier.size(20.dp), padding = 0)
            Spacer(modifier = Modifier.width(8.dp))
            Text(name, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
        
        Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.SpaceEvenly) {
            (1..9).forEach { i ->
                val inn = innings?.find { it.num == i }
                val score = if (isAway) inn?.away?.runs else inn?.home?.runs
                Text(
                    text = score?.toString() ?: "-",
                    color = if (score != null) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    fontSize = 14.sp,
                    modifier = Modifier.width(20.dp),
                    textAlign = TextAlign.Center
                )
            }
        }

        Row(modifier = Modifier.width(75.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
            Text("$r", color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.width(25.dp), textAlign = TextAlign.Center)
            Text("$h", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp, modifier = Modifier.width(25.dp), textAlign = TextAlign.Center)
            Text("$e", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp, modifier = Modifier.width(25.dp), textAlign = TextAlign.Center)
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun TeamStatsTabs(selectedTab: Int, onTabSelected: (Int) -> Unit, awayName: String, homeName: String, awayId: Int, homeId: Int) {
    TabRow(
        selectedTabIndex = selectedTab,
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.primary,
        divider = {}
    ) {
        Tab(selected = selectedTab == 0, onClick = { onTabSelected(0) }) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                TeamLogo(teamId = awayId, modifier = Modifier.size(24.dp), padding = 0)
                Spacer(modifier = Modifier.width(8.dp))
                Text(awayName, color = if (selectedTab == 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline)
            }
        }
        Tab(selected = selectedTab == 1, onClick = { onTabSelected(1) }) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                TeamLogo(teamId = homeId, modifier = Modifier.size(24.dp), padding = 0)
                Spacer(modifier = Modifier.width(8.dp))
                Text(homeName, color = if (selectedTab == 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline)
            }
        }
    }
}

@Composable
fun BoxscorePlayerRow(name: String, pos: String, ab: Int, r: Int, h: Int, rbi: Int, playerId: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
            PlayerHeadshot(playerId = playerId.toString(), modifier = Modifier.size(40.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(name, color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(pos, color = MaterialTheme.colorScheme.outline, style = MaterialTheme.typography.labelSmall)
            }
        }
        Row(modifier = Modifier.width(160.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            StatCol("AB", ab.toString())
            StatCol("R", r.toString())
            StatCol("H", h.toString())
            StatCol("RBI", rbi.toString())
        }
    }
    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
}

@Composable
fun BoxscorePitcherRow(name: String, ip: String, h: Int, er: Int, k: Int, playerId: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
            PlayerHeadshot(playerId = playerId.toString(), modifier = Modifier.size(40.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(name, color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        Row(modifier = Modifier.width(160.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            StatCol("IP", ip)
            StatCol("H", h.toString())
            StatCol("ER", er.toString())
            StatCol("K", k.toString())
        }
    }
    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
}

@Composable
fun StatCol(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(35.dp)) {
        Text(value, color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }
}

@Preview(showBackground = true)
@Composable
fun BoxScorePreview() {
    val mockTeam = Team(id = 146, name = "Miami Marlins", teamName = "Marlins", abbreviation = "MIA")
    val mockTeam2 = Team(id = 117, name = "Houston Astros", teamName = "Astros", abbreviation = "HOU")
    
    val mockBoxscore = BoxscoreResponse(
        teams = BoxscoreTeams(
            away = BoxscoreTeam(
                team = mockTeam,
                teamStats = BoxscoreTeamStats(
                    batting = com.example.testapp.api.BoxscoreBattingStats(3, 10),
                    pitching = com.example.testapp.api.BoxscorePitchingStats(4, 9),
                    fielding = com.example.testapp.api.BoxscoreFieldingStats(0)
                ),
                players = mapOf(
                    "p1" to BoxscorePlayer(
                        person = Person(672333, "Luis Arraez"),
                        stats = BoxscorePlayerStats(batting = com.example.testapp.api.BattingStats(4, 1, 2, 1)),
                        position = Position("2B", "Second Base", "Infielder", "4")
                    )
                )
            ),
            home = BoxscoreTeam(
                team = mockTeam2,
                teamStats = BoxscoreTeamStats(
                    batting = com.example.testapp.api.BoxscoreBattingStats(4, 9),
                    pitching = com.example.testapp.api.BoxscorePitchingStats(3, 10),
                    fielding = com.example.testapp.api.BoxscoreFieldingStats(1)
                ),
                players = mapOf(
                    "p2" to BoxscorePlayer(
                        person = Person(514888, "Jose Altuve"),
                        stats = BoxscorePlayerStats(batting = com.example.testapp.api.BattingStats(4, 1, 1, 0)),
                        position = Position("2B", "Second Base", "Infielder", "4")
                    )
                )
            )
        )
    )
    
    val mockLinescore = LinescoreResponse(
        innings = listOf(
            Inning(1, "1st", InningScore(0), InningScore(0)),
            Inning(2, "2nd", InningScore(0), InningScore(0)),
            Inning(3, "3rd", InningScore(0), InningScore(0)),
            Inning(4, "4th", InningScore(0), InningScore(0)),
            Inning(5, "5th", InningScore(2), InningScore(1)),
            Inning(6, "6th", InningScore(0), InningScore(3)),
            Inning(7, "7th", InningScore(1), InningScore(0)),
            Inning(8, "8th", InningScore(0), InningScore(0))
        ),
        teams = LinescoreTeams(
            away = LinescoreTeam(3, 10, 0),
            home = LinescoreTeam(4, 9, 1)
        )
    )

    TestAppTheme {
        BoxScoreContent(
            uiState = com.example.testapp.ui.viewmodels.BoxScoreUiState(
                boxscore = mockBoxscore,
                linescore = mockLinescore,
                isLoading = false
            ),
            onBack = {}
        )
    }
}
