package com.example.testapp.ui.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.testapp.model.BoxScore
import com.example.testapp.repository.GameRepository
import com.example.testapp.ui.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class BoxScoreViewModel @Inject constructor(
    private val repository: GameRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val route = savedStateHandle.toRoute<Screen.BoxScore>()
    val gameId = route.gameId
    val awayTeamName = route.awayTeam
    val homeTeamName = route.homeTeam

    private val _boxScore = MutableStateFlow<BoxScore?>(null)
    val boxScore: StateFlow<BoxScore?> = _boxScore.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _lastUpdated = MutableStateFlow<String?>(null)
    val lastUpdated: StateFlow<String?> = _lastUpdated.asStateFlow()

    private var observationJob: Job? = null

    init {
        observeLiveBoxScore()
    }

    private fun observeLiveBoxScore() {
        observationJob?.cancel()
        observationJob = viewModelScope.launch {
            repository.getLiveBoxScore(gameId)
                .onStart { _isLoading.value = true }
                .collect { newBoxScore ->
                    _boxScore.value = newBoxScore
                    _isLoading.value = false
                    _lastUpdated.value = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
                }
        }
    }

    fun refresh() {
        observeLiveBoxScore()
    }
}
