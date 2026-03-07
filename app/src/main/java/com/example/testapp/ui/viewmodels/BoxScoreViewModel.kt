package com.example.testapp.ui.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.testapp.api.BoxscoreResponse
import com.example.testapp.api.LinescoreResponse
import com.example.testapp.repository.GameRepository
import com.example.testapp.ui.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class BoxScoreUiState(
    val boxscore: BoxscoreResponse? = null,
    val linescore: LinescoreResponse? = null,
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

    init {
        observeGameData()
    }

    private fun observeGameData() {
        viewModelScope.launch {
            repository.getLiveGameData(gameId).collect { data ->
                _uiState.value = _uiState.value.copy(
                    boxscore = data.boxscore,
                    linescore = data.linescore,
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
