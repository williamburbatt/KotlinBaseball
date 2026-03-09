package com.example.testapp.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

@Singleton
class UserPreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object PreferencesKeys {
        val FAVORITE_TEAM_ID = intPreferencesKey("favorite_team_id")
    }

    val favoriteTeamIdFlow: Flow<Int?> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferencesKeys.FAVORITE_TEAM_ID]
        }

    suspend fun updateFavoriteTeam(teamId: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.FAVORITE_TEAM_ID] = teamId
        }
    }

    suspend fun clearFavoriteTeam() {
        context.dataStore.edit { preferences ->
            preferences.remove(PreferencesKeys.FAVORITE_TEAM_ID)
        }
    }
}
