package com.example.testapp.ui.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.testapp.model.Player
import com.example.testapp.repository.PlayerRepository
import com.example.testapp.ui.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import java.util.Calendar
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

    private val route: Screen.PlayerList? = try {
        savedStateHandle.toRoute<Screen.PlayerList>()
    } catch (e: Exception) {
        null
    }
    
    val teamId = route?.teamId ?: 0
    
    private val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    
    private val _selectedYear = MutableStateFlow(currentYear)
    val selectedYear: StateFlow<Int> = _selectedYear

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _selectedPlayer = MutableStateFlow<Player?>(null)
    val selectedPlayer: StateFlow<Player?> = _selectedPlayer

    private val positionOrder = listOf("C", "1B", "2B", "SS", "3B", "LF", "CF", "RF", "DH")

    fun updateYear(year: Int) {
        _selectedYear.value = year
    }

    fun selectPlayer(playerId: Int) {
        _isLoading.value = true
        repository.getPlayerDetail(playerId).onEach {
            _selectedPlayer.value = it
            _isLoading.value = false
        }.launchIn(viewModelScope)
    }

    fun clearSelectedPlayer() {
        _selectedPlayer.value = null
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val groupedPlayers: StateFlow<List<PlayerGroup>> = if (route == null) {
        flowOf(emptyList<PlayerGroup>()).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    } else {
        combine(
            MutableStateFlow(teamId),
            _selectedYear
        ) { id, year ->
            id to year
        }.flatMapLatest { (id, year) ->
            repository.getPlayers(id, year)
                .onStart { _isLoading.value = true }
                .onEach { _isLoading.value = false }
        }.map { players ->
            val batters = players.filter { it.position != "P" && it.position != "SP" && it.position != "RP" && it.position != "TWP" }
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
    }

    val players: StateFlow<List<Player>> = groupedPlayers.map { groups -> groups.flatMap { it.players } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
