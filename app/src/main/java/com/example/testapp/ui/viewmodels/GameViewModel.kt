package com.example.testapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testapp.model.Game
import com.example.testapp.model.BoxScore
import com.example.testapp.repository.GameRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    private val repository: GameRepository
) : ViewModel() {

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    private val _games = MutableStateFlow<List<Game>>(emptyList())
    val games: StateFlow<List<Game>> = _games.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadGamesForSelectedDate()
    }

    fun updateDate(date: LocalDate) {
        _selectedDate.value = date
        loadGamesForSelectedDate()
    }

    private fun loadGamesForSelectedDate() {
        viewModelScope.launch {
            _isLoading.value = true
            val dateString = _selectedDate.value.format(DateTimeFormatter.ISO_LOCAL_DATE)
            repository.getGamesForDate(dateString).collect {
                _games.value = it
                _isLoading.value = false
            }
        }
    }
}
