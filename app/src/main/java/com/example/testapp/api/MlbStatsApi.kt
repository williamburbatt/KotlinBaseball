package com.example.testapp.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.serialization.Serializable
import javax.inject.Inject
import javax.inject.Singleton

@Serializable
data class TeamResponse(val teams: List<Team>)

@Serializable
data class Team(
    val id: Int,
    val name: String? = null,
    val teamName: String? = null,
    val abbreviation: String? = null,
    val location: String? = null
)

@Serializable
data class PeopleResponse(val people: List<PersonDetails>)

@Serializable
data class PersonDetails(
    val id: Int,
    val fullName: String,
    val primaryNumber: String? = null,
    val primaryPosition: Position? = null,
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
data class Side(val code: String? = null, val description: String? = null)

@Serializable
data class RosterResponse(val roster: List<RosterPlayer>)

@Serializable
data class RosterPlayer(val person: Person, val position: Position)

@Serializable
data class Person(val id: Int, val fullName: String)

@Serializable
data class Position(
    val abbreviation: String? = null,
    val name: String? = null,
    val type: String? = null,
    val battingOrder: String? = null
)

@Serializable
data class StatContainer(
    val type: StatType? = null,
    val group: StatGroup? = null,
    val splits: List<StatSplit> = emptyList()
)

@Serializable
data class StatSplit(
    val season: String? = null,
    val stat: PlayerStats,
    val team: Team? = null
)

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
    val baseOnBalls: Int? = null,
    val strikeOuts: Int? = null,
    val era: String? = null,
    val wins: Int? = null,
    val losses: Int? = null,
    val strikeouts: Int? = null,
    val inningsPitched: String? = null,
    val whip: String? = null,
    val gamesStarted: Int? = null,
    val saves: Int? = null,
    val earnedRuns: Int? = null
)

@Serializable
data class StatType(val displayName: String? = null)

@Serializable
data class StatGroup(val displayName: String? = null)

@Serializable
data class PlayerStatsResponse(val stats: List<StatContainer>)

@Serializable
data class ScheduleResponse(val dates: List<ScheduleDate>)

@Serializable
data class ScheduleDate(val date: String, val games: List<Game>)

@Serializable
data class Game(
    val gamePk: Int,
    val gameDate: String,
    val status: GameStatus,
    val teams: GameTeams,
    val linescore: LinescoreResponse? = null
)

@Serializable
data class GameStatus(val abstractGameState: String, val detailedState: String)

@Serializable
data class GameTeams(val away: GameTeam, val home: GameTeam)

@Serializable
data class GameTeam(val team: Team, val score: Int? = null, val seriesRecord: SeriesRecord? = null)

@Serializable
data class SeriesRecord(val wins: Int, val losses: Int)

@Serializable
data class LinescoreResponse(
    val currentInning: Int? = null,
    val currentInningOrdinal: String? = null,
    val inningHalf: String? = null,
    val isTopInning: Boolean? = null,
    val scheduledInnings: Int? = null,
    val teams: LinescoreTeams? = null,
    val innings: List<Inning> = emptyList(),
    val outs: Int? = null,
    val offense: Offense? = null
)

@Serializable
data class Offense(
    val first: Runner? = null,
    val second: Runner? = null,
    val third: Runner? = null,
    val batter: Person? = null,
    val pitcher: Person? = null,
    val onFirst: Person? = null,
    val onSecond: Person? = null,
    val onThird: Person? = null
)

@Serializable
data class Runner(val id: Int? = null, val fullName: String? = null)

@Serializable
data class Inning(
    val num: Int,
    val ordinalNum: String? = null,
    val away: InningScore,
    val home: InningScore
)

@Serializable
data class InningScore(val runs: Int? = null, val hits: Int? = null, val errors: Int? = null)

@Serializable
data class LinescoreTeams(val away: LinescoreTeam, val home: LinescoreTeam)

@Serializable
data class LinescoreTeam(val runs: Int? = null, val hits: Int? = null, val errors: Int? = null)

@Serializable
data class BoxscoreResponse(val teams: BoxscoreTeams)

@Serializable
data class BoxscoreTeams(val away: BoxscoreTeam, val home: BoxscoreTeam)

@Serializable
data class BoxscoreTeam(
    val team: Team,
    val players: Map<String, BoxscorePlayer>,
    val teamStats: BoxscoreTeamStats? = null
)

@Serializable
data class BoxscoreTeamStats(
    val batting: BoxscoreBattingStats? = null,
    val pitching: BoxscorePitchingStats? = null,
    val fielding: BoxscoreFieldingStats? = null
)

@Serializable
data class BoxscoreBattingStats(
    val runs: Int? = null,
    val hits: Int? = null,
    val rbi: Int? = null,
    val homeRuns: Int? = null,
    val atBats: Int? = null,
    val stolenBases: Int? = null
)

@Serializable
data class BoxscorePitchingStats(
    val runs: Int? = null,
    val hits: Int? = null,
    val strikeouts: Int? = null,
    val earnedRuns: Int? = null,
    val inningsPitched: String? = null
)

@Serializable
data class BoxscoreFieldingStats(val errors: Int? = null)

@Serializable
data class BoxscorePlayer(
    val person: Person,
    val stats: BoxscorePlayerStats,
    val position: Position
)

@Serializable
data class BoxscorePlayerStats(val batting: BattingStats? = null, val pitching: PitchingStats? = null)

@Serializable
data class BattingStats(
    val runs: Int? = null,
    val hits: Int? = null,
    val rbi: Int? = null,
    val homeRuns: Int? = null,
    val strikeouts: Int? = null,
    val walks: Int? = null,
    val atBats: Int? = null,
    val stolenBases: Int? = null
)

@Serializable
data class PitchingStats(
    val inningsPitched: String? = null,
    val hits: Int? = null,
    val runs: Int? = null,
    val earnedRuns: Int? = null,
    val strikeOuts: Int? = null,
    val walks: Int? = null,
    val era: String? = null,
    val whip: String? = null
)

@Serializable
data class PlayByPlayResponse(val allPlays: List<Play>)

@Serializable
data class Play(
    val result: PlayResult,
    val about: PlayAbout,
    val count: PlayCount,
    val matchDetails: MatchDetails? = null
)

@Serializable
data class PlayResult(
    val type: String? = null,
    val event: String? = null,
    val description: String?,
    val rbi: Int? = null
)

@Serializable
data class PlayAbout(
    val inning: Int,
    val isTopInning: Boolean,
    val halfInning: String? = null
)

@Serializable
data class PlayCount(val balls: Int, val strikes: Int, val outs: Int)

@Serializable
data class MatchDetails(val pitcher: Person, val batter: Person)

@Serializable
data class LeagueLeadersResponse(
    val leagueLeaders: List<LeagueLeaderCategory>
)

@Serializable
data class LeagueLeaderCategory(
    val leaderCategory: String,
    val statGroup: String,
    val leaders: List<Leader>
)

@Serializable
data class Leader(
    val rank: Int,
    val value: String,
    val team: Team,
    val person: Person
)

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
        hydrate: String = "currentTeam,stats(group=[hitting,pitching],type=[yearByYear,season])"
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
        teamId: Int, season: Int? = null
    ): RosterResponse {
        return client.get("$baseUrl/v1/teams/$teamId/roster") {
            parameter("season", season)
        }.body()
    }

    suspend fun getSchedule(
        sportId: Int = 1, date: String, hydrate: String = "team,linescore"
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

    suspend fun getLinescore(gamePk: Int): LinescoreResponse {
        return client.get("$baseUrl/v1/game/$gamePk/linescore").body()
    }

    suspend fun searchPlayers(query: String): PeopleResponse {
        return client.get("$baseUrl/v1/people/search") {
            parameter("names", query)
            parameter("activeStatus", "BOTH")
            parameter("hydrate", "currentTeam")
        }.body()
    }

    suspend fun getPlayByPlay(gamePk: Int): PlayByPlayResponse {
        return client.get("$baseUrl/v1/game/$gamePk/playByPlay").body()
    }

    suspend fun getLeagueLeaders(
        category: String,
        group: String,
        season: Int = 2025,
        limit: Int = 25
    ): LeagueLeadersResponse {
        return client.get("$baseUrl/v1/stats/leaders") {
            parameter("leaderCategories", category)
            parameter("statGroup", group)
            parameter("season", season)
            parameter("limit", limit)
        }.body()
    }
}
