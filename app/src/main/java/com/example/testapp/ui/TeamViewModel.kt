package com.example.testapp.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testapp.model.Team
import com.example.testapp.repository.TeamRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class TeamViewModel @Inject constructor(
    private val repository: TeamRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    val teams: StateFlow<List<Team>> = savedStateHandle.getStateFlow("sportId", 1)
        .flatMapLatest { sportId ->
            repository.getTeams(sportId)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}
