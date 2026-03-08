package com.example.testapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testapp.api.MlbStatsApi
import com.example.testapp.api.PersonDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerSearchViewModel @Inject constructor(
    private val api: MlbStatsApi
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<List<PersonDetails>>(emptyList())
    val searchResults: StateFlow<List<PersonDetails>> = _searchResults.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    init {
        setupSearchDebounce()
    }

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    private fun setupSearchDebounce() {
        _searchQuery
            .debounce(300)
            .distinctUntilChanged()
            .flatMapLatest { query ->
                    flow {
                        if (query.length >= 3) {
                            _isSearching.value = true
                            val listOfPlayers = performSearch(query)
                            emit(listOfPlayers)
                        }
                        else {
                            emit(emptyList())
                        }
                    }
                        .onCompletion { _isSearching.value = false }
                }
            .onEach { searchResults ->
                _searchResults.value = searchResults

            }
            .launchIn(viewModelScope)
            }

    fun onQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    private suspend fun performSearch(query: String): List<PersonDetails> {
            try {
                val response = api.searchPlayers(query)
                return response.people
            } catch (e: Exception) {
                e.printStackTrace()
                return emptyList()
            }
    }
}
