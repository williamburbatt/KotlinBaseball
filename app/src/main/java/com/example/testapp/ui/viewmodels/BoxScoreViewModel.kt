package com.example.testapp.ui.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.testapp.api.BoxscoreResponse
import com.example.testapp.api.LinescoreResponse
import com.example.testapp.api.PlayByPlayResponse
import com.example.testapp.repository.GameRepository
import com.example.testapp.ui.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class BoxScoreUiState(
    val boxscore: BoxscoreResponse? = null,
    val linescore: LinescoreResponse? = null,
    val playByPlay: PlayByPlayResponse? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
    val lastUpdated: String? = null
)

@HiltViewModel
class BoxScoreViewModel @Inject constructor(
    private val repository: GameRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val route = savedStateHandle.toRoute<Screen.BoxScore>()
    val gameId = route.gameId
    val awayTeamName = route.awayTeam
    val homeTeamName = route.homeTeam

    private val _uiState = MutableStateFlow(BoxScoreUiState())
    val uiState: StateFlow<BoxScoreUiState> = _uiState.asStateFlow()
    private var refreshJob: Job? = null

    init {
        observeGameData()
    }

    private fun observeGameData() {
        refreshJob?.cancel()
        refreshJob = viewModelScope.launch {
            repository.getLiveGameData(gameId)
                .combine(repository.getPlaybyPlayData(gameId)){gameData, plays -> Pair(gameData,plays)}
                    .collect { (gameData, plays) ->
                        _uiState.value = _uiState.value.copy(
                    boxscore = gameData.boxscore,
                    linescore = gameData.linescore,
                    playByPlay = plays,
                    isLoading = false,
                    lastUpdated = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
                )
            }
        }
    }

    fun refresh() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        observeGameData()
    }
}
