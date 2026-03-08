package com.example.testapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.testapp.api.Leader
import com.example.testapp.api.Person
import com.example.testapp.api.Team
import com.example.testapp.ui.components.PlayerHeadshot
import com.example.testapp.ui.theme.TestAppTheme
import com.example.testapp.ui.viewmodels.LeadersUiState
import com.example.testapp.ui.viewmodels.LeadersViewModel

@Composable
fun LeadersScreen(
    onPlayerClick: (Int) -> Unit,
    onBack: () -> Unit,
    viewModel: LeadersViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LeadersScreenContent(
        uiState = uiState,
        onPlayerClick = onPlayerClick,
        onBack = onBack,
        onCategoryChange = { viewModel.updateCategory(it) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeadersScreenContent(
    uiState: LeadersUiState,
    onPlayerClick: (Int) -> Unit,
    onBack: () -> Unit,
    onCategoryChange: (String) -> Unit
) {
    val categories = listOf(
        "homeRuns" to "Home Runs",
        "battingAverage" to "Batting Average",
        "runsBattedIn" to "RBIs",
        "stolenBases" to "Stolen Bases",
        "hits" to "Hits"
    )
    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("League Leaders", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Dropdown Menu for Categories
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                OutlinedTextField(
                    value = categories.find { it.first == uiState.selectedCategory }?.second ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Category") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    categories.forEach { (key, label) ->
                        DropdownMenuItem(
                            text = { Text(label) },
                            onClick = {
                                onCategoryChange(key)
                                expanded = false
                            }
                        )
                    }
                }
            }

            Box(modifier = Modifier.weight(1f)) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(uiState.leaders) { leader ->
                            LeaderRow(
                                leader = leader,
                                onClick = { onPlayerClick(leader.person.id) }
                            )
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                thickness = 0.5.dp,
                                color = MaterialTheme.colorScheme.outlineVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LeaderRow(
    leader: Leader,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "${leader.rank}",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(32.dp)
        )
        
        PlayerHeadshot(
            playerId = leader.person.id.toString(),
            modifier = Modifier.size(48.dp)
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = leader.person.fullName,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = leader.team.name ?: "",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
        
        Text(
            text = leader.value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LeadersScreenPreview() {
    TestAppTheme {
        LeadersScreenContent(
            uiState = LeadersUiState(
                leaders = listOf(
                    Leader(
                        rank = 1,
                        value = "44",
                        team = Team(id = 1, name = "New York Yankees"),
                        person = Person(id = 1, fullName = "Aaron Judge")
                    ),
                    Leader(
                        rank = 2,
                        value = "39",
                        team = Team(id = 2, name = "Los Angeles Dodgers"),
                        person = Person(id = 2, fullName = "Shohei Ohtani")
                    ),
                    Leader(
                        rank = 3,
                        value = "35",
                        team = Team(id = 3, name = "Baltimore Orioles"),
                        person = Person(id = 3, fullName = "Gunnar Henderson")
                    )
                )
            ),
            onPlayerClick = {},
            onBack = {},
            onCategoryChange = {}
        )
    }
}
