package com.example.testapp.repository

import com.example.testapp.api.MlbStatsApi
import com.example.testapp.model.Game
import com.example.testapp.model.BoxScore
import com.example.testapp.model.BoxScorePlayer
import com.example.testapp.model.BattingStats
import com.example.testapp.model.PitchingStats
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameRepository @Inject constructor(
    private val api: MlbStatsApi
) {
    fun getGamesForDate(date: String): Flow<List<Game>> = flow {
        try {
            val response = api.getSchedule(date = date)
            val games = response.dates.flatMap { it.games }.map { apiGame ->
                val localStartTime = try {
                    val zonedDateTime = ZonedDateTime.parse(apiGame.gameDate)
                    val localDateTime = zonedDateTime.withZoneSameInstant(ZoneId.systemDefault())
                    localDateTime.format(DateTimeFormatter.ofPattern("h:mm a"))
                } catch (e: Exception) {
                    null
                }

                Game(
                    id = apiGame.gamePk,
                    awayTeam = apiGame.teams.away.team.name ?: "",
                    homeTeam = apiGame.teams.home.team.name ?: "",
                    awayScore = apiGame.teams.away.score,
                    homeScore = apiGame.teams.home.score,
                    status = apiGame.status.detailedState,
                    startTime = localStartTime,
                    awayHits = apiGame.linescore?.teams?.away?.hits,
                    homeHits = apiGame.linescore?.teams?.home?.hits,
                    awayErrors = apiGame.linescore?.teams?.away?.errors,
                    homeErrors = apiGame.linescore?.teams?.home?.errors
                )
            }
            emit(games)
        } catch (e: Exception) {
            e.printStackTrace()
            emit(emptyList())
        }
    }

    fun getBoxScore(gameId: Int): Flow<BoxScore?> = flow {
        try {
            val response = api.getBoxscore(gameId)
            val boxScore = BoxScore(
                gameId = gameId,
                awayTeamName = response.teams.away.team.name ?: "",
                homeTeamName = response.teams.home.team.name ?: "",
                awayPlayers = response.teams.away.players.values.map { it.toModel() },
                homePlayers = response.teams.home.players.values.map { it.toModel() }
            )
            emit(boxScore)
        } catch (e: Exception) {
            e.printStackTrace()
            emit(null)
        }
    }

    private fun com.example.testapp.api.BoxscorePlayer.toModel(): BoxScorePlayer {
        return BoxScorePlayer(
            id = person.id,
            name = person.fullName,
            position = position.abbreviation,
            battingStats = stats.batting?.let {
                BattingStats(
                    ab = it.atBats ?: 0,
                    r = it.runs ?: 0,
                    h = it.hits ?: 0,
                    rbi = it.rbi ?: 0,
                    hr = it.homeRuns ?: 0,
                    lob = it.leftOnBase ?: 0
                )
            },
            pitchingStats = stats.pitching?.let {
                PitchingStats(
                    ip = it.inningsPitched ?: "0.0",
                    h = it.hits ?: 0,
                    r = it.runs ?: 0,
                    er = it.earnedRuns ?: 0,
                    bb = it.baseOnBalls ?: 0,
                    k = it.strikeOuts ?: 0
                )
            }
        )
    }
}
