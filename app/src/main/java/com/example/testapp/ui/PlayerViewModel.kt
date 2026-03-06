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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class PlayerGroup(
    val title: String,
    val players: List<Player>
)

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

    private val positionOrder = listOf("C", "1B", "2B", "SS", "3B", "LF", "CF", "RF", "DH")

    fun updateYear(year: Int) {
        _selectedYear.value = year
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val groupedPlayers: StateFlow<List<PlayerGroup>> = combine(
        teamIdFlow,
        _selectedYear
    ) { id, year ->
        id to year
    }.flatMapLatest { (id, year) ->
        repository.getPlayers(id, year)
            .onStart { _isLoading.value = true }
            .onEach { _isLoading.value = false }
    }.map { players ->
        val batters = players.filter { it.position != "P" && it.position != "SP" && it.position != "RP" && it.position != "TWP"}
            .sortedWith(compareBy({ positionOrder.indexOf(it.position).takeIf { it != -1 } ?: 99 }, { it.name }))
        
        val pitchers = players.filter { it.position == "P" || it.position == "SP" || it.position == "RP" || it.position == "TWP" }
            .sortedBy { it.name }

        listOf(
            PlayerGroup("Batters", batters),
            PlayerGroup("Pitchers", pitchers)
        ).filter { it.players.isNotEmpty() }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Keep the original players flow for compatibility if needed, though we'll use groupedPlayers
    val players: StateFlow<List<Player>> = groupedPlayers.map { groups -> groups.flatMap { it.players } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
