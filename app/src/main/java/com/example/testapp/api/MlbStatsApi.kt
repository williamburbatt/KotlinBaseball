package com.example.testapp.api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.serialization.Serializable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MlbStatsApi @Inject constructor(
    private val client: HttpClient
) {
    private val baseUrl = "https://statsapi.mlb.com/api"

    suspend fun getTeams(sportId: Int = 1): TeamResponse {
        return client.get("$baseUrl/v1/teams") {
            parameter("sportId", sportId)
        }.body()
    }

    suspend fun getPlayerDetails(
        playerId: Int,
        hydrate: String = "currentTeam,stats(group=[hitting,pitching,batting],type=[yearByYear,season])"
    ): PeopleResponse {
        return client.get("$baseUrl/v1/people/$playerId") {
            parameter("hydrate", hydrate)
        }.body()
    }

    suspend fun getPlayerStats(
        playerId: Int,
        stats: String = "season",
        sportId: Int = 1,
        season: Int? = null,
        gameType: String = "R",
        group: String? = null
    ): PlayerStatsResponse {
        return client.get("$baseUrl/v1/people/$playerId/stats") {
            parameter("stats", stats)
            parameter("sportId", sportId)
            parameter("season", season)
            parameter("gameType", gameType)
            if (group != null) {
                parameter("group", group)
            }
        }.body()
    }

    suspend fun getTeamRoster(
        teamId: Int,
        season: Int? = null
    ): RosterResponse {
        return client.get("$baseUrl/v1/teams/$teamId/roster") {
            parameter("season", season)
        }.body()
    }

    suspend fun getSchedule(
        sportId: Int = 1,
        date: String,
        hydrate: String = "team,linescore"
    ): ScheduleResponse {
        return client.get("$baseUrl/v1/schedule") {
            parameter("sportId", sportId)
            parameter("date", date)
            parameter("hydrate", hydrate)
        }.body()
    }

    suspend fun getBoxscore(gamePk: Int): BoxscoreResponse {
        return client.get("$baseUrl/v1/game/$gamePk/boxscore").body()
    }
}

@Serializable
data class TeamResponse(val teams: List<Team>)
@Serializable
data class Team(val id: Int, val name: String? = null, val teamName: String? = null)

@Serializable
data class PeopleResponse(val people: List<PersonDetails>)

@Serializable
data class PersonDetails(
    val id: Int,
    val fullName: String,
    val primaryNumber: String? = null,
    val birthDate: String? = null,
    val currentAge: Int? = null,
    val height: String? = null,
    val weight: Int? = null,
    val bats: Side? = null,
    val throws: Side? = null,
    val birthCity: String? = null,
    val birthStateProvince: String? = null,
    val birthCountry: String? = null,
    val mlbDebutDate: String? = null,
    val currentTeam: Team? = null,
    val stats: List<StatContainer>? = null
)

@Serializable
data class Side(val code: String, val description: String)

@Serializable
data class RosterResponse(val roster: List<RosterPlayer>)
@Serializable
data class RosterPlayer(val person: Person, val position: Position)
@Serializable
data class Person(val id: Int, val fullName: String)
@Serializable
data class Position(val abbreviation: String, val name: String? = null)

@Serializable
data class PlayerStatsResponse(val stats: List<StatContainer>)
@Serializable
data class StatContainer(
    val type: StatType? = null,
    val group: StatGroup? = null,
    val splits: List<StatSplit>
)

@Serializable
data class StatType(val displayName: String)
@Serializable
data class StatGroup(val displayName: String)

@Serializable
data class StatSplit(
    val season: String? = null,
    val stat: PlayerStats,
    val team: Team? = null,
    val league: League? = null
)

@Serializable
data class League(val name: String? = null)

@Serializable
data class PlayerStats(
    val avg: String? = null,
    val obp: String? = null,
    val slg: String? = null,
    val ops: String? = null,
    val homeRuns: Int? = null,
    val rbi: Int? = null,
    val runs: Int? = null,
    val gamesPlayed: Int? = null,
    val plateAppearances: Int? = null,
    val atBats: Int? = null,
    val hits: Int? = null,
    val stolenBases: Int? = null,
    val strikeOuts: Int? = null,
    val strikeouts: Int? = null,
    val baseOnBalls: Int? = null,
    // Pitching
    val era: String? = null,
    val wins: Int? = null,
    val losses: Int? = null,
    val inningsPitched: String? = null,
    val whip: String? = null,
    val gamesStarted: Int? = null,
    val saves: Int? = null
)

@Serializable
data class ScheduleResponse(val dates: List<ScheduleDate>)
@Serializable
data class ScheduleDate(val date: String, val games: List<Game>)
@Serializable
data class Game(
    val gamePk: Int,
    val gameDate: String,
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
