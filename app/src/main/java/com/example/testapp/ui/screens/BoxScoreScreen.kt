package com.example.testapp.ui.screens

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.testapp.R
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
import com.example.testapp.ui.components.DiamondView
import com.example.testapp.ui.theme.TestAppTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun BoxScoreScreen(
    onBack: () -> Unit,
    onPlayerClick: (Int) -> Unit,
    viewModel: BoxScoreViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    BoxScoreContent(
        uiState = uiState,
        onBack = onBack,
        onPlayerClick = onPlayerClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoxScoreContent(
    uiState: com.example.testapp.ui.viewmodels.BoxScoreUiState,
    onBack: () -> Unit,
    onPlayerClick: (Int) -> Unit = {}
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

                    if (selectedTab < 2) {
                        val team = if (selectedTab == 0) boxscore.teams.away else boxscore.teams.home
                        
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "BATTING",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.outline
                                )
                                Row(modifier = Modifier.width(260.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                    StatHeaderCol("AB")
                                    StatHeaderCol("R")
                                    StatHeaderCol("H")
                                    StatHeaderCol("RBI")
                                    StatHeaderCol("HR")
                                    StatHeaderCol("SB")
                                }
                            }
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
                                hr = player.stats.batting?.homeRuns ?: 0,
                                sb = player.stats.batting?.stolenBases ?: 0,
                                playerId = player.person.id,
                                onClick = { onPlayerClick(player.person.id) }
                            )
                        }

                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "PITCHING",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.outline
                                )
                                Row(modifier = Modifier.width(260.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                    StatHeaderCol("IP")
                                    StatHeaderCol("H")
                                    StatHeaderCol("ER")
                                    StatHeaderCol("K")
                                    StatHeaderCol("ERA")
                                    StatHeaderCol("WHIP")
                                }
                            }
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
                                era = player.stats.pitching?.era ?: "-.--",
                                whip = player.stats.pitching?.whip ?: "-.--",
                                playerId = player.person.id,
                                onClick = { onPlayerClick(player.person.id) }
                            )
                        }
                    } else {
                        val plays = uiState.playByPlay?.allPlays ?: emptyList()
                        if (plays.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "No play-by-play data available for this game.",
                                        color = MaterialTheme.colorScheme.outline,
                                        style = MaterialTheme.typography.bodyMedium,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        } else {
                            items(plays.reversed()) { play ->
                                PlayRow(play)
                            }
                        }
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
            modifier = Modifier.padding(24.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Away Team
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

            // Middle Section (Scores + Live Info)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1.2f)
            ) {
                // Scores row aligned with top of logos/text
                Row(verticalAlignment = Alignment.CenterVertically) {
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

                // Scorebug (Live Info) underneath
                if (linescore != null && linescore.currentInning != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${linescore.inningHalf} ${linescore.currentInningOrdinal}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.Red
                            )
                            Text(
                                text = "${linescore.outs} Outs",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        DiamondView(
                            first = linescore.offense?.first != null,
                            second = linescore.offense?.second != null,
                            third = linescore.offense?.third != null,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }

            // Home Team
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
        Tab(selected = selectedTab == 2, onClick = { onTabSelected(2) }) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(24.dp),
                    shape = CircleShape,
                    color = Color.White,
                    shadowElevation = 2.dp
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_logo_classic_ball),
                        contentDescription = "Plays",
                        modifier = Modifier.padding(4.dp),
                        tint = Color.Unspecified
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("Plays", color = if (selectedTab == 2) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline)
            }
        }
    }
}

@Composable
fun BoxscorePlayerRow(
    name: String, 
    pos: String, 
    ab: Int, 
    r: Int, 
    h: Int, 
    rbi: Int, 
    hr: Int, 
    sb: Int, 
    playerId: Int,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
            PlayerHeadshot(playerId = playerId.toString(), modifier = Modifier.size(40.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = name, 
                    color = MaterialTheme.colorScheme.onBackground, 
                    fontWeight = FontWeight.Bold, 
                    maxLines = 2, 
                    overflow = TextOverflow.Ellipsis
                )
                Text(pos, color = MaterialTheme.colorScheme.outline, style = MaterialTheme.typography.labelSmall)
            }
        }
        Row(modifier = Modifier.width(260.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            StatCol(ab.toString())
            StatCol(r.toString())
            StatCol(h.toString())
            StatCol(rbi.toString())
            StatCol(hr.toString())
            StatCol(sb.toString())
        }
    }
    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
}

@Composable
fun BoxscorePitcherRow(
    name: String, 
    ip: String, 
    h: Int, 
    er: Int, 
    k: Int, 
    era: String, 
    whip: String, 
    playerId: Int,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
            PlayerHeadshot(playerId = playerId.toString(), modifier = Modifier.size(40.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = name, 
                color = MaterialTheme.colorScheme.onBackground, 
                fontWeight = FontWeight.Bold, 
                maxLines = 2, 
                overflow = TextOverflow.Ellipsis
            )
        }
        Row(modifier = Modifier.width(260.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            StatCol(ip)
            StatCol(h.toString())
            StatCol(er.toString())
            StatCol(k.toString())
            StatCol(era)
            StatCol(whip)
        }
    }
    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
}

@Composable
fun PlayRow(play: com.example.testapp.api.Play) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape,
                        modifier = Modifier.size(8.dp)
                    ) {}
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${play.about.halfInning} ${play.about.inning}",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                Text(
                    text = "${play.count.outs} Outs",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline,
                    fontWeight = FontWeight.Medium
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = play.result.description ?: "",
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 20.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            if (play.result.event != null) {
                Spacer(modifier = Modifier.height(8.dp))
                SuggestionChip(
                    onClick = { },
                    label = { Text(play.result.event ?: "", fontSize = 10.sp) },
                    modifier = Modifier.height(24.dp)
                )
            }
        }
    }
}

@Composable
fun StatHeaderCol(label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(35.dp)) {
        Text(label, color = MaterialTheme.colorScheme.outline, fontWeight = FontWeight.Bold, fontSize = 12.sp)
    }
}

@Composable
fun StatCol(value: String) {
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
                        stats = BoxscorePlayerStats(batting = com.example.testapp.api.BattingStats(4, 1, 2, 1, 1, 0, 1)),
                        position = Position("2B", "Second Base", "Infielder", "4")
                    ),
                    "p_marlins_pitcher" to BoxscorePlayer(
                        person = Person(664353, "Sandy Alcantara"),
                        stats = BoxscorePlayerStats(pitching = com.example.testapp.api.PitchingStats("7.0", 5, 1, 1, 10, 1, "2.25", "1.05")),
                        position = Position("SP", "Starting Pitcher", "Pitcher", "1")
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
                        stats = BoxscorePlayerStats(batting = com.example.testapp.api.BattingStats(4, 1, 1, 0, 0, 0, 0)),
                        position = Position("2B", "Second Base", "Infielder", "4")
                    ),
                    "p3" to BoxscorePlayer(
                        person = Person(593160, "Gerrit Cole"),
                        stats = BoxscorePlayerStats(pitching = com.example.testapp.api.PitchingStats("6.0", 4, 2, 2, 8, 1, "3.10", "1.12")),
                        position = Position("SP", "Starting Pitcher", "Pitcher", "1")
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
        ),
        currentInning = 8,
        currentInningOrdinal = "8th",
        inningHalf = "Top",
        outs = 2,
        offense = com.example.testapp.api.Offense(
            first = com.example.testapp.api.Runner(1),
            third = com.example.testapp.api.Runner(2)
        )
    )

    val mockPlays = listOf(
        com.example.testapp.api.Play(
            result = com.example.testapp.api.PlayResult("Luis Arraez flies out to right fielder Kyle Tucker.", "Flyout"),
            about = com.example.testapp.api.PlayAbout(8, "Top"),
            count = com.example.testapp.api.PlayCount(0, 0, 1)
        ),
        com.example.testapp.api.Play(
            result = com.example.testapp.api.PlayResult("Josh Bell doubles (20) on a line drive to center fielder Chas McCormick. Luis Arraez scores.", "Double"),
            about = com.example.testapp.api.PlayAbout(8, "Top"),
            count = com.example.testapp.api.PlayCount(1, 1, 2)
        )
    )

    TestAppTheme {
        BoxScoreContent(
            uiState = com.example.testapp.ui.viewmodels.BoxScoreUiState(
                boxscore = mockBoxscore,
                linescore = mockLinescore,
                playByPlay = com.example.testapp.api.PlayByPlayResponse(mockPlays),
                isLoading = false
            ),
            onBack = {}
        )
    }
}
