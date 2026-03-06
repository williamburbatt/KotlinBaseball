package com.example.testapp.repository

import com.example.testapp.api.MlbStatsApi
import com.example.testapp.model.Player
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayerRepository @Inject constructor(
    private val api: MlbStatsApi
) {
    fun getPlayers(): Flow<List<Player>> = flow {
        try {
            // Let's get the roster for a MiLB team (e.g., Somerset Patriots ID: 117)
            val rosterResponse = api.getTeamRoster(117)
            val players = rosterResponse.roster.map { rosterPlayer ->
                Player(
                    id = rosterPlayer.person.id.toString(),
                    name = rosterPlayer.person.fullName,
                    team = "Somerset Patriots", 
                    position = rosterPlayer.position.abbreviation,
                    average = 0.0, 
                    homeRuns = 0,
                    rbi = 0
                )
            }
            emit(players)
        } catch (e: Exception) {
            e.printStackTrace()
            emit(emptyList())
        }
    }
}
