package com.example.testapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.testapp.R
import com.example.testapp.ui.theme.TestAppTheme
import com.example.testapp.ui.viewmodels.UserPreferencesViewModel

@Composable
fun MainHubScreen(
    onGamesClick: () -> Unit,
    onTeamsClick: () -> Unit,
    onSearchClick: () -> Unit,
    onLeadersClick: () -> Unit,
    userPrefsViewModel: UserPreferencesViewModel = hiltViewModel()
) {
    val favoriteTeamId by userPrefsViewModel.favoriteTeamId.collectAsStateWithLifecycle()
    val teams by userPrefsViewModel.allTeams.collectAsStateWithLifecycle()
    var showPicker by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Favorite Team Logo in Top Right
            favoriteTeamId?.let { teamId ->
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 48.dp, end = 24.dp)
                        .size(48.dp)
                        .clip(CircleShape)
                        .clickable { showPicker = true }
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data("https://www.mlbstatic.com/team-logos/$teamId.svg")
                            .crossfade(true)
                            .build(),
                        contentDescription = "Favorite Team",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }
            } ?: run {
                // Show a placeholder or button to pick if none selected
                IconButton(
                    onClick = { showPicker = true },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 48.dp, end = 24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Pick Favorite Team",
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_logo_classic_ball),
                    contentDescription = "App Logo",
                    modifier = Modifier.size(120.dp)
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Text(
                    text = "Kotlin Baseball",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(48.dp))
                
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    HubCard(
                        title = "Live Games",
                        description = "Check real-time scores and box scores",
                        icon = Icons.Default.DateRange,
                        onClick = onGamesClick
                    )
                    
                    HubCard(
                        title = "Teams & Rosters",
                        description = "Browse teams and player stats",
                        icon = Icons.AutoMirrored.Filled.List,
                        onClick = onTeamsClick
                    )

                    HubCard(
                        title = "Player Lookup",
                        description = "Search for any player directly",
                        icon = Icons.Default.Search,
                        onClick = onSearchClick
                    )
                    HubCard(
                        title = "Stat Leaders",
                        description = "View the best of the best",
                        icon = Icons.Default.Star,
                        onClick = onLeadersClick
                    )
                }
            }
        }
    }

    if (showPicker) {
        FavoriteTeamPicker(
            teams = teams,
            onTeamSelected = { team ->
                userPrefsViewModel.setFavoriteTeam(team.id)
            },
            onDismiss = { showPicker = false }
        )
    }
}

@Composable
fun HubCard(title: String, description: String, icon: ImageVector, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.width(20.dp))
            
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainHubPreview() {
    TestAppTheme {
        MainHubScreen(onGamesClick = {}, onTeamsClick = {}, onSearchClick = {}, onLeadersClick = {})
    }
}
