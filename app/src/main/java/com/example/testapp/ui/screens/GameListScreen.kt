package com.example.testapp.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.testapp.model.Game
import com.example.testapp.ui.components.TeamLogo
import com.example.testapp.ui.theme.TestAppTheme
import com.example.testapp.ui.viewmodels.GameViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun GameListScreen(
    onGameClick: (Game) -> Unit, viewModel: GameViewModel = hiltViewModel()
) {
    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()
    val games by viewModel.games.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    GameListContent(
        selectedDate = selectedDate,
        games = games,
        isLoading = isLoading,
        onDateSelected = { viewModel.updateDate(it) },
        onGameClick = onGameClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameListContent(
    selectedDate: LocalDate,
    games: List<Game>,
    isLoading: Boolean,
    onDateSelected: (LocalDate) -> Unit,
    onGameClick: (Game) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Surface(shadowElevation = 8.dp) {
                Column(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 48.dp, start = 16.dp, end = 8.dp, bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = selectedDate.month.getDisplayName(
                                    TextStyle.FULL, Locale.getDefault()
                                ),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Live Games",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(
                                Icons.Default.DateRange,
                                contentDescription = "Select Date",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }

                    DateScroller(
                        selectedDate = selectedDate, onDateSelected = onDateSelected
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            if (isLoading && games.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (games.isEmpty()) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "No games scheduled",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Text(
                        selectedDate.format(DateTimeFormatter.ofPattern("EEEE, MMMM d")),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 12.dp)
                ) {
                    items(
                        items = games, key = { game -> game.id }) { game ->
                        if (game.isLive) {
                            LiveGameCard(game = game, onClick = { onGameClick(game) })
                        } else {
                            GameScoreCard(game = game, onClick = { onGameClick(game) })
                        }
                    }
                }
            }
        }

        if (showDatePicker) {
            M3DatePickerDialog(initialDate = selectedDate, onDateSelected = {
                it?.let { onDateSelected(it) }
                showDatePicker = false
            }, onDismiss = { showDatePicker = false })
        }
    }
}

@Composable
fun DateScroller(
    selectedDate: LocalDate, onDateSelected: (LocalDate) -> Unit
) {
    val listState = rememberLazyListState()
    val dates = remember(selectedDate) {
        (-15..15).map { selectedDate.plusDays(it.toLong()) }
    }

    LaunchedEffect(Unit) {
        listState.scrollToItem(15)
    }

    LazyRow(
        state = listState,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp)
    ) {
        items(
            items = dates, key = { date -> date.toString() }) { date ->
            val isSelected = date == selectedDate
            DateItem(
                date = date, isSelected = isSelected, onClick = { onDateSelected(date) })
        }
    }
}

@Composable
fun DateItem(date: LocalDate, isSelected: Boolean, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .width(50.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()).uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.outline,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = date.dayOfMonth.toString(),
            style = MaterialTheme.typography.titleMedium,
            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun M3DatePickerDialog(
    initialDate: LocalDate, onDateSelected: (LocalDate?) -> Unit, onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDate.atStartOfDay(ZoneId.systemDefault()).toInstant()
            .toEpochMilli()
    )

    DatePickerDialog(onDismissRequest = onDismiss, confirmButton = {
        TextButton(onClick = {
            val selectedMillis = datePickerState.selectedDateMillis
            if (selectedMillis != null) {
                val date = Instant.ofEpochMilli(selectedMillis).atZone(ZoneId.systemDefault())
                    .toLocalDate()
                onDateSelected(date)
            } else {
                onDismiss()
            }
        }) {
            Text("Confirm")
        }
    }, dismissButton = {
        TextButton(onClick = onDismiss) {
            Text("Cancel")
        }
    }) {
        DatePicker(state = datePickerState)
    }
}

@Composable
fun LiveGameCard(game: Game, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Team Scores & Info
                Column(modifier = Modifier.weight(1f).padding(end = 16.dp)) {
                    LiveTeamRow(
                        teamId = game.awayTeamId,
                        name = game.awayTeam,
                        score = game.awayScore ?: 0
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LiveTeamRow(
                        teamId = game.homeTeamId,
                        name = game.homeTeam,
                        score = game.homeScore ?: 0
                    )
                }

                // Vertical Divider
                Box(
                    modifier = Modifier
                        .height(60.dp)
                        .width(1.dp)
                        .background(MaterialTheme.colorScheme.outlineVariant)
                )

                // Inning, Outs & Diamond
                Row(
                    modifier = Modifier.padding(start = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${game.inningHalf} ${game.currentInningOrdinal}",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.Red
                        )
                        Text(
                            text = "${game.outs} Outs",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    DiamondView(
                        first = game.runnerOnFirst,
                        second = game.runnerOnSecond,
                        third = game.runnerOnThird
                    )
                }
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview
@Composable
fun LiveGameCardPreview() {
    val mockGame = Game(
        id = 1,
        awayTeam = "Nicaragua",
        homeTeam = "Netherlands",
        awayScore = 3,
        homeScore = 1,
        status = "In Progress",
        startTime = "2024-10-25T23:05:00Z",
        isLive = true,
        currentInningOrdinal = "8th",
        inningHalf = "Top",
        outs = 2,
        runnerOnFirst = true,
        runnerOnSecond = false,
        runnerOnThird = true,
        awayTeamId = 146,
        homeTeamId = 117
    )
    TestAppTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            LiveGameCard(game = mockGame, onClick = {})
        }
    }
}

@OptIn(androidx.compose.animation.ExperimentalSharedTransitionApi::class)
@Composable
fun LiveTeamRow(teamId: Int, name: String, score: Int) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        TeamLogo(teamId = teamId, modifier = Modifier.size(24.dp), padding = 0)
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = name,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = score.toString(),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Black
        )
    }
}

@Composable
fun DiamondView(first: Boolean, second: Boolean, third: Boolean) {
    val activeColor = MaterialTheme.colorScheme.primary
    val inactiveColor = Color.LightGray.copy(alpha = 0.5f)

    Box(modifier = Modifier.size(40.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val diamondSize = 12.dp.toPx()

            // Second Base (Top)
            drawDiamond(
                center = Offset(size.width / 2, diamondSize / 2),
                size = diamondSize,
                color = if (second) activeColor else inactiveColor
            )

            // Third Base (Left)
            drawDiamond(
                center = Offset(diamondSize / 2, size.height / 2),
                size = diamondSize,
                color = if (third) activeColor else inactiveColor
            )

            // First Base (Right)
            drawDiamond(
                center = Offset(size.width - (diamondSize / 2), size.height / 2),
                size = diamondSize,
                color = if (first) activeColor else inactiveColor
            )
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawDiamond(
    center: Offset, size: Float, color: Color
) {
    val path = Path().apply {
        moveTo(center.x, center.y - size / 2) // Top
        lineTo(center.x + size / 2, center.y) // Right
        lineTo(center.x, center.y + size / 2) // Bottom
        lineTo(center.x - size / 2, center.y) // Left
        close()
    }
    drawPath(path = path, color = color)
}

@Composable
fun GameScoreCard(game: Game, onClick: () -> Unit) {
    val formattedStartTime = remember(game.startTime) {
        try {
            val instant = Instant.parse(game.startTime)
            val zonedDateTime = instant.atZone(ZoneId.systemDefault())
            zonedDateTime.format(DateTimeFormatter.ofPattern("h:mm a"))
        } catch (ignored: Exception) {
            game.startTime ?: ""
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = if (game.status == "Final") Color.LightGray.copy(alpha = 0.2f) else MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = game.status.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            color = if (game.status == "Final") Color.DarkGray else MaterialTheme.colorScheme.primary
                        )
                    }

                    if (game.status != "Final" && formattedStartTime.isNotEmpty()) {
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = formattedStartTime,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Text(
                    text = "BOX SCORE >",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            TeamScoreRow(
                teamName = game.awayTeam,
                score = game.awayScore,
                isWinner = (game.awayScore ?: 0) > (game.homeScore ?: 0) && game.status == "Final"
            )
            Spacer(modifier = Modifier.height(12.dp))
            TeamScoreRow(
                teamName = game.homeTeam,
                score = game.homeScore,
                isWinner = (game.homeScore ?: 0) > (game.awayScore ?: 0) && game.status == "Final"
            )
        }
    }
}

@Composable
fun TeamScoreRow(teamName: String, score: Int?, isWinner: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (isWinner) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                )
                Spacer(Modifier.width(8.dp))
            }
            Text(
                text = teamName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = if (isWinner) FontWeight.ExtraBold else FontWeight.Medium,
                color = if (isWinner) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(
                    alpha = 0.7f
                )
            )
        }
        Text(
            text = score?.toString() ?: "-",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Black,
            color = if (isWinner) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GameListPreview() {
    val mockGames = listOf(
        Game(
            id = 1,
            awayTeam = "Nicaragua",
            homeTeam = "Netherlands",
            awayScore = 3,
            homeScore = 1,
            status = "In Progress",
            startTime = "2024-10-25T23:05:00Z",
            isLive = true,
            currentInningOrdinal = "8th",
            inningHalf = "Top",
            outs = 2,
            runnerOnFirst = true,
            runnerOnSecond = false,
            runnerOnThird = true,
            awayTeamId = 146,
            homeTeamId = 117
        ), Game(
            id = 2,
            awayTeam = "New York Yankees",
            homeTeam = "Boston Red Sox",
            awayScore = 5,
            homeScore = 2,
            status = "Final",
            startTime = "2024-10-25T23:05:00Z"
        )
    )
    TestAppTheme {
        GameListContent(
            selectedDate = LocalDate.now(),
            games = mockGames,
            isLoading = false,
            onDateSelected = {},
            onGameClick = {})
    }
}
