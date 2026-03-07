package com.example.testapp.repository

import com.example.testapp.api.MlbStatsApi
import com.example.testapp.model.Player
import com.example.testapp.model.YearlyStats
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.Calendar
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
                        val position = rosterPlayer.position.abbreviation
                        
                        val isPitcher = position == "P" || position == "SP" || position == "RP" || position == "TWP"
                        val group = if (isPitcher) "pitching" else "hitting"

                        val statsResponse = try {
                            api.getPlayerStats(
                                playerId = playerId,
                                stats = "season",
                                season = year,
                                sportId = if (teamId < 200) 1 else 11,
                                group = group
                            )
                        } catch (e: Exception) {
                            null
                        }

                        val playerStats = statsResponse?.stats?.firstOrNull()?.splits?.firstOrNull()?.stat
                        
                        Player(
                            id = playerId.toString(),
                            name = rosterPlayer.person.fullName,
                            team = "Team $teamId",
                            teamId = teamId,
                            position = position,
                            currentStats = if (playerStats != null) {
                                mapApiStatsToYearly(playerStats, year.toString(), "Team $teamId", isPitcher)
                            } else null
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

    fun getPlayerDetail(playerId: Int): Flow<Player?> = flow {
        try {
            val currentYearInt = Calendar.getInstance().get(Calendar.YEAR)
            val currentYearStr = currentYearInt.toString()
            
            val personResponse = api.getPlayerDetails(playerId, hydrate = "currentTeam")
            val person = personResponse.people.firstOrNull()
            
            if (person != null) {
                val historyMap = mutableMapOf<String, YearlyStats>()
                val milbHistoryMap = mutableMapOf<String, YearlyStats>()

                // Fetch MLB Stats
                val mlbHitting = try { api.getPlayerStats(playerId, stats = "yearByYear", group = "hitting", sportId = 1) } catch (e: Exception) { null }
                val mlbPitching = try { api.getPlayerStats(playerId, stats = "yearByYear", group = "pitching", sportId = 1) } catch (e: Exception) { null }

                // Fetch MiLB Stats
                val milbHitting = try { api.getPlayerStats(playerId, stats = "yearByYear", group = "hitting", sportId = 11) } catch (e: Exception) { null }
                val milbPitching = try { api.getPlayerStats(playerId, stats = "yearByYear", group = "pitching", sportId = 11) } catch (e: Exception) { null }

                // Process MLB
                mlbHitting?.stats?.forEach { container ->
                    container.splits.forEach { split ->
                        val year = split.season ?: return@forEach
                        historyMap[year] = mapApiStatsToYearly(split.stat, year, split.team?.abbreviation ?: split.team?.name ?: "MLB", false)
                    }
                }
                mlbPitching?.stats?.forEach { container ->
                    container.splits.forEach { split ->
                        val year = split.season ?: return@forEach
                        val existing = historyMap[year]
                        val pitching = mapApiStatsToYearly(split.stat, year, split.team?.abbreviation ?: split.team?.name ?: "MLB", true)
                        historyMap[year] = if (existing != null) mergeStats(existing, pitching) else pitching
                    }
                }

                // Process MiLB
                milbHitting?.stats?.forEach { container ->
                    container.splits.forEach { split ->
                        val year = split.season ?: return@forEach
                        milbHistoryMap[year] = mapApiStatsToYearly(split.stat, year, split.team?.abbreviation ?: split.team?.name ?: "MiLB", false)
                    }
                }
                milbPitching?.stats?.forEach { container ->
                    container.splits.forEach { split ->
                        val year = split.season ?: return@forEach
                        val existing = milbHistoryMap[year]
                        val pitching = mapApiStatsToYearly(split.stat, year, split.team?.abbreviation ?: split.team?.name ?: "MiLB", true)
                        milbHistoryMap[year] = if (existing != null) mergeStats(existing, pitching) else pitching
                    }
                }

                val history = historyMap.values.sortedByDescending { it.year }
                val milbHistory = milbHistoryMap.values.sortedByDescending { it.year }
                val currentStats = history.find { it.year == currentYearStr } ?: milbHistory.find { it.year == currentYearStr } ?: history.firstOrNull() ?: milbHistory.firstOrNull()

                val birthPlace = listOfNotNull(person.birthCity, person.birthStateProvince, person.birthCountry).joinToString(", ")

                val currentTeamId = person.currentTeam?.id
                    ?: mlbHitting?.stats?.flatMap { it.splits }?.find { it.season == currentYearStr }?.team?.id
                    ?: mlbPitching?.stats?.flatMap { it.splits }?.find { it.season == currentYearStr }?.team?.id
                    ?: milbHitting?.stats?.flatMap { it.splits }?.find { it.season == currentYearStr }?.team?.id
                    ?: milbPitching?.stats?.flatMap { it.splits }?.find { it.season == currentYearStr }?.team?.id

                emit(Player(
                    id = person.id.toString(),
                    name = person.fullName,
                    teamId = currentTeamId,
                    team = person.currentTeam?.name ?: "",
                    number = person.primaryNumber ?: "",
                    age = person.currentAge ?: 0,
                    birthDate = person.birthDate ?: "",
                    birthLocation = birthPlace,
                    height = person.height ?: "",
                    weight = person.weight ?: 0,
                    bats = person.bats?.code ?: "",
                    throws = person.throws?.code ?: "",
                    debutDate = person.mlbDebutDate ?: "",
                    headshotUrl = "https://img.mlbstatic.com/mlb-photos/image/upload/d_people:generic:headshot:67:current.png/w_426,q_auto:best/v1/people/${person.id}/headshot/67/current",
                    currentStats = currentStats,
                    careerStats = history,
                    milbStats = milbHistory
                ))
            } else {
                emit(null)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(null)
        }
    }

    private fun mapApiStatsToYearly(apiStats: com.example.testapp.api.PlayerStats, year: String, team: String, isPitching: Boolean): YearlyStats {
        return if (isPitching) {
            YearlyStats(
                year = year, team = team,
                era = apiStats.era ?: "-.--",
                w = apiStats.wins ?: 0,
                l = apiStats.losses ?: 0,
                k = apiStats.strikeouts ?: apiStats.strikeOuts ?: 0,
                p_bb = apiStats.baseOnBalls ?: 0,
                ip = apiStats.inningsPitched ?: "0.0",
                whip = apiStats.whip ?: "-.--",
                gs = apiStats.gamesStarted ?: 0,
                sv = apiStats.saves ?: 0,
                games = apiStats.gamesPlayed ?: 0
            )
        } else {
            YearlyStats(
                year = year, team = team,
                avg = apiStats.avg ?: ".000",
                obp = apiStats.obp ?: ".000",
                slg = apiStats.slg ?: ".000",
                ops = apiStats.ops ?: ".000",
                hr = apiStats.homeRuns ?: 0,
                rbi = apiStats.rbi ?: 0,
                r = apiStats.runs ?: 0,
                games = apiStats.gamesPlayed ?: 0,
                pa = apiStats.plateAppearances ?: 0,
                ab = apiStats.atBats ?: 0,
                h = apiStats.hits ?: 0,
                sb = apiStats.stolenBases ?: 0,
                bb = apiStats.baseOnBalls ?: 0,
                so = apiStats.strikeOuts ?: 0
            )
        }
    }

    private fun mergeStats(h: YearlyStats, p: YearlyStats): YearlyStats {
        return h.copy(
            era = p.era, w = p.w, l = p.l, k = p.k, p_bb = p.p_bb, ip = p.ip, whip = p.whip, gs = p.gs, sv = p.sv
        )
    }
}
