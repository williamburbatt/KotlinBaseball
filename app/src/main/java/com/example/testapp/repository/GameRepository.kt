package com.example.testapp.repository

import com.example.testapp.api.BoxscoreResponse
import com.example.testapp.api.LinescoreResponse
import com.example.testapp.api.MlbStatsApi
import com.example.testapp.model.Game
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

data class GameData(
    val boxscore: BoxscoreResponse,
    val linescore: LinescoreResponse
)

@Singleton
class GameRepository @Inject constructor(
    private val api: MlbStatsApi
) {
    fun getGamesForDate(date: String, autoRefresh: Boolean = false): Flow<List<Game>> = flow {
        while (true) {
            try {
                val response = api.getSchedule(date = date)
                val games = response.dates.flatMap { dateEntry ->
                    dateEntry.games.map { apiGame ->
                        Game(
                            id = apiGame.gamePk,
                            awayTeam = apiGame.teams.away.team.name ?: "",
                            homeTeam = apiGame.teams.home.team.name ?: "",
                            awayScore = apiGame.teams.away.score,
                            homeScore = apiGame.teams.home.score,
                            status = apiGame.status.detailedState,
                            startTime = apiGame.gameDate,
                            awayHits = apiGame.linescore?.teams?.away?.hits,
                            homeHits = apiGame.linescore?.teams?.home?.hits,
                            awayErrors = apiGame.linescore?.teams?.away?.errors,
                            homeErrors = apiGame.linescore?.teams?.home?.errors
                        )
                    }
                }
                emit(games)
            } catch (e: Exception) {
                e.printStackTrace()
                if (!autoRefresh) {
                    emit(emptyList())
                }
            }
            
            if (autoRefresh) {
                delay(5000)
            } else {
                break
            }
        }
    }

    fun getLiveGameData(gamePk: Int): Flow<GameData> = flow {
        while (true) {
            try {
                val boxscore = api.getBoxscore(gamePk)
                val linescore = api.getLinescore(gamePk)
                emit(GameData(boxscore, linescore))
            } catch (e: Exception) {
                e.printStackTrace()
            }
            delay(5000) // Update every 5 seconds as requested
        }
    }
}
