package com.example.testapp.api

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MlbStatsApi {
    @GET("v1/teams")
    suspend fun getTeams(@Query("sportId") sportId: Int = 11): TeamResponse

    @GET("v1/people/{playerId}/stats")
    suspend fun getPlayerStats(
        @Path("playerId") playerId: Int,
        @Query("stats") stats: String = "season",
        @Query("sportId") sportId: Int = 11,
        @Query("season") season: Int? = null,
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null
    ): PlayerStatsResponse
    
    @GET("v1/teams/{teamId}/roster")
    suspend fun getTeamRoster(
        @Path("teamId") teamId: Int,
        @Query("season") season: Int? = null
    ): RosterResponse
}

data class TeamResponse(val teams: List<Team>)
data class Team(val id: Int, val name: String, val teamName: String)

data class RosterResponse(val roster: List<RosterPlayer>)
data class RosterPlayer(val person: Person, val position: Position)
data class Person(val id: Int, val fullName: String)
data class Position(val abbreviation: String)

data class PlayerStatsResponse(val stats: List<StatContainer>)
data class StatContainer(val splits: List<StatSplit>)
data class StatSplit(val stat: PlayerStats)
data class PlayerStats(
    val avg: String?,
    val homeRuns: Int?,
    val rbi: Int?
)
