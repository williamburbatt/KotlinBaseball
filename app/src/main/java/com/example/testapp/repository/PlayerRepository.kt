package com.example.testapp.repository

import com.example.testapp.api.MlbStatsApi
import com.example.testapp.model.Player
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayerRepository @Inject constructor(
    private val api: MlbStatsApi
) {
    fun getPlayers(teamId: Int, year: Int): Flow<List<Player>> = flow {
        try {
            val rosterResponse = api.getTeamRoster(teamId, season = year)
            
            val playersWithStats = coroutineScope {
                rosterResponse.roster.map { rosterPlayer ->
                    async {
                        val playerId = rosterPlayer.person.id
                        val statsResponse = try {
                            api.getPlayerStats(
                                playerId = playerId,
                                stats = "season",
                                season = year
                            )
                        } catch (e: Exception) {
                            null
                        }

                        val playerStats = statsResponse?.stats?.firstOrNull()?.splits?.firstOrNull()?.stat
                        
                        Player(
                            id = playerId.toString(),
                            name = rosterPlayer.person.fullName,
                            team = "Team $teamId",
                            position = rosterPlayer.position.abbreviation,
                            average = playerStats?.avg?.toDoubleOrNull() ?: 0.0,
                            homeRuns = playerStats?.homeRuns ?: 0,
                            rbi = playerStats?.rbi ?: 0
                        )
                    }
                }.awaitAll()
            }
            
            emit(playersWithStats)
        } catch (e: Exception) {
            e.printStackTrace()
            emit(emptyList())
        }
    }
}
