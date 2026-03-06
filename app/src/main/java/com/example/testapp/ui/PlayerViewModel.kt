package com.example.testapp.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testapp.model.Player
import com.example.testapp.repository.PlayerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val repository: PlayerRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val teamIdFlow = savedStateHandle.getStateFlow("teamId", 117)
    
    private val _selectedYear = MutableStateFlow(2026)
    val selectedYear: StateFlow<Int> = _selectedYear

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun updateYear(year: Int) {
        _selectedYear.value = year
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val players: StateFlow<List<Player>> = combine(
        teamIdFlow,
        _selectedYear
    ) { id, year ->
        id to year
    }.flatMapLatest { (id, year) ->
        repository.getPlayers(id, year)
            .onStart { _isLoading.value = true }
            .onEach { _isLoading.value = false }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
}
