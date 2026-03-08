package com.example.testapp.ui.viewmodels
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testapp.api.Leader
import com.example.testapp.repository.LeadersRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

    data class LeadersUiState(
        val leaders: List<Leader> = emptyList(),
        val isLoading: Boolean = false,
        val selectedCategory: String = "homeRuns",
        val error: String? = null
    )
    @HiltViewModel
    class LeadersViewModel @Inject constructor(
        private val repository: LeadersRepository,
        savedStateHandle: SavedStateHandle
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(LeadersUiState())
        val uiState: StateFlow<LeadersUiState> = _uiState.asStateFlow()

        init {
            loadLeaders()
        }

        private fun loadLeaders() {
            viewModelScope.launch {
                _uiState.value = _uiState.value.copy(isLoading = true)
                repository.getLeaders(_uiState.value.selectedCategory, "hitting")
                    .collect { leaderList ->
                        _uiState.value = _uiState.value.copy(
                            leaders = leaderList,
                            isLoading = false
                        )
                    }
            }
        }
        fun updateCategory(newCategory: String) {
            _uiState.value = _uiState.value.copy(selectedCategory = newCategory)
            loadLeaders()
        }
    }
