package com.example.testapp.api

import kotlinx.serialization.Serializable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MlbStatsApi {
    @GET("v1/teams")
    suspend fun getTeams(@Query("sportId") sportId: Int = 1): TeamResponse

    @GET("v1/people/{playerId}/stats")
    suspend fun getPlayerStats(
        @Path("playerId") playerId: Int,
        @Query("stats") stats: String = "season",
        @Query("sportId") sportId: Int = 1,
        @Query("season") season: Int? = null,
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null
    ): PlayerStatsResponse
    
    @GET("v1/teams/{teamId}/roster")
    suspend fun getTeamRoster(
        @Path("teamId") teamId: Int,
        @Query("season") season: Int? = null
    ): RosterResponse

    @GET("v1/schedule")
    suspend fun getSchedule(
        @Query("sportId") sportId: Int = 1,
        @Query("date") date: String, // format: YYYY-MM-DD
        @Query("hydrate") hydrate: String = "team,linescore"
    ): ScheduleResponse

    @GET("v1/game/{gamePk}/boxscore")
    suspend fun getBoxscore(
        @Path("gamePk") gamePk: Int
    ): BoxscoreResponse
}

@Serializable
data class TeamResponse(val teams: List<Team>)
@Serializable
data class Team(val id: Int, val name: String, val teamName: String)

@Serializable
data class RosterResponse(val roster: List<RosterPlayer>)
@Serializable
data class RosterPlayer(val person: Person, val position: Position)
@Serializable
data class Person(val id: Int, val fullName: String)
@Serializable
data class Position(val abbreviation: String)

@Serializable
data class PlayerStatsResponse(val stats: List<StatContainer>)
@Serializable
data class StatContainer(val splits: List<StatSplit>)
@Serializable
data class StatSplit(val stat: PlayerStats)
@Serializable
data class PlayerStats(
    // Batting
    val avg: String? = null,
    val homeRuns: Int? = null,
    val rbi: Int? = null,
    // Pitching
    val era: String? = null,
    val wins: Int? = null,
    val losses: Int? = null,
    val strikeOuts: Int? = null,
    val inningsPitched: String? = null
)

@Serializable
data class ScheduleResponse(val dates: List<ScheduleDate>)
@Serializable
data class ScheduleDate(val date: String, val games: List<Game>)
@Serializable
data class Game(
    val gamePk: Int,
    val teams: GameTeams,
    val status: GameStatus,
    val linescore: Linescore? = null
)
@Serializable
data class GameTeams(val away: TeamScore, val home: TeamScore)
@Serializable
data class TeamScore(val team: Team, val score: Int? = null)
@Serializable
data class GameStatus(val abstractGameState: String, val detailedState: String)
@Serializable
data class Linescore(val teams: LinescoreTeams)
@Serializable
data class LinescoreTeams(val away: LinescoreTeam, val home: LinescoreTeam)
@Serializable
data class LinescoreTeam(val runs: Int? = null, val hits: Int? = null, val errors: Int? = null)

@Serializable
data class BoxscoreResponse(val teams: BoxscoreTeams)
@Serializable
data class BoxscoreTeams(val away: BoxscoreTeam, val home: BoxscoreTeam)
@Serializable
data class BoxscoreTeam(val team: Team, val players: Map<String, BoxscorePlayer>)
@Serializable
data class BoxscorePlayer(val person: Person, val stats: BoxscorePlayerStats, val position: Position)
@Serializable
data class BoxscorePlayerStats(val batting: BattingStats? = null, val pitching: PitchingStats? = null)
@Serializable
data class BattingStats(val atBats: Int? = null, val runs: Int? = null, val hits: Int? = null, val rbi: Int? = null, val homeRuns: Int? = null, val leftOnBase: Int? = null)
@Serializable
data class PitchingStats(val inningsPitched: String? = null, val hits: Int? = null, val runs: Int? = null, val earnedRuns: Int? = null, val strikeOuts: Int? = null, val baseOnBalls: Int? = null)
