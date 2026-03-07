package com.example.testapp.repository

import com.example.testapp.api.MlbStatsApi
import com.example.testapp.model.Team
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TeamRepository @Inject constructor(
    private val api: MlbStatsApi
) {
    fun getTeams(sportId: Int = 1): Flow<List<Team>> = flow {
        try {
            val response = api.getTeams(sportId)
            val teams = response.teams.map { 
                Team(
                    id = it.id,
                    name = it.name ?: "",
                    teamName = it.teamName ?: ""
                )
            }
            emit(teams)
        } catch (e: Exception) {
            e.printStackTrace()
            emit(emptyList())
        }
    }
}
