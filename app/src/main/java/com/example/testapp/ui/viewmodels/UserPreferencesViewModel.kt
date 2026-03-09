package com.example.testapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testapp.model.Team
import com.example.testapp.repository.TeamRepository
import com.example.testapp.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserPreferencesViewModel @Inject constructor(
    private val repository: UserPreferencesRepository,
    private val teamRepository: TeamRepository
) : ViewModel() {

    val favoriteTeamId: StateFlow<Int?> = repository.favoriteTeamIdFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val allTeams: StateFlow<List<Team>> = teamRepository.getTeams(1) // Default to MLB (1)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun setFavoriteTeam(teamId: Int) {
        viewModelScope.launch {
            repository.updateFavoriteTeam(teamId)
        }
    }

    fun clearFavoriteTeam() {
        viewModelScope.launch {
            repository.clearFavoriteTeam()
        }
    }
}
