package com.example.testapp.repository

import com.example.testapp.api.MlbStatsApi
import com.example.testapp.model.Player
import com.example.testapp.model.YearlyStats
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
                        val position = rosterPlayer.position.abbreviation
                        
                        val statsResponse = try {
                            api.getPlayerStats(
                                playerId = playerId,
                                stats = "season",
                                season = year,
                                sportId = if (teamId < 200) 1 else 11
                            )
                        } catch (e: Exception) {
                            null
                        }

                        val playerStats = statsResponse?.stats?.firstOrNull()?.splits?.firstOrNull()?.stat
                        
                        Player(
                            id = playerId.toString(),
                            name = rosterPlayer.person.fullName,
                            team = "Team $teamId",
                            position = position,
                            currentStats = if (playerStats != null) {
                                YearlyStats(
                                    year = year.toString(),
                                    team = "Team $teamId",
                                    avg = playerStats.avg ?: ".000",
                                    hr = playerStats.homeRuns ?: 0,
                                    rbi = playerStats.rbi ?: 0,
                                    era = playerStats.era ?: "-.--",
                                    w = playerStats.wins ?: 0,
                                    l = playerStats.losses ?: 0,
                                    k = playerStats.strikeOuts ?: 0,
                                    ip = playerStats.inningsPitched ?: "0.0"
                                )
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
            val response = api.getPlayerDetails(playerId)
            val person = response.people.firstOrNull()
            
            if (person != null) {
                val careerBatting = person.stats?.find { it.group?.displayName == "batting" && it.type?.displayName == "yearByYear" }
                val careerPitching = person.stats?.find { it.group?.displayName == "pitching" && it.type?.displayName == "yearByYear" }
                
                val currentBatting = person.stats?.find { it.group?.displayName == "batting" && it.type?.displayName == "season" }?.splits?.firstOrNull()?.stat
                val currentPitching = person.stats?.find { it.group?.displayName == "pitching" && it.type?.displayName == "season" }?.splits?.firstOrNull()?.stat

                val history = mutableListOf<YearlyStats>()
                
                val battingSplits = careerBatting?.splits ?: emptyList()
                val pitchingSplits = careerPitching?.splits ?: emptyList()
                
                val allYears = (battingSplits.mapNotNull { it.season } + pitchingSplits.mapNotNull { it.season }).distinct().sortedDescending()
                
                for (year in allYears) {
                    val b = battingSplits.find { it.season == year }
                    val p = pitchingSplits.find { it.season == year }
                    
                    history.add(YearlyStats(
                        year = year,
                        team = b?.team?.name ?: p?.team?.name ?: "Unknown",
                        league = b?.league?.name ?: p?.league?.name ?: "MLB",
                        // Batting
                        games = b?.stat?.gamesPlayed ?: 0,
                        pa = b?.stat?.plateAppearances ?: 0,
                        ab = b?.stat?.atBats ?: 0,
                        h = b?.stat?.hits ?: 0,
                        hr = b?.stat?.homeRuns ?: 0,
                        r = b?.stat?.runs ?: 0,
                        rbi = b?.stat?.rbi ?: 0,
                        sb = b?.stat?.stolenBases ?: 0,
                        bb = b?.stat?.baseOnBalls ?: 0,
                        so = b?.stat?.strikeOuts ?: 0,
                        avg = b?.stat?.avg ?: ".000",
                        obp = b?.stat?.obp ?: ".000",
                        slg = b?.stat?.slg ?: ".000",
                        ops = b?.stat?.ops ?: ".000",
                        // Pitching
                        era = p?.stat?.era ?: "-.--",
                        w = p?.stat?.wins ?: 0,
                        l = p?.stat?.losses ?: 0,
                        k = p?.stat?.strikeouts ?: p?.stat?.strikeOuts ?: 0,
                        p_bb = p?.stat?.baseOnBalls ?: 0,
                        ip = p?.stat?.inningsPitched ?: "0.0",
                        whip = p?.stat?.whip ?: "-.--",
                        gs = p?.stat?.gamesStarted ?: 0,
                        sv = p?.stat?.saves ?: 0
                    ))
                }

                val birthPlace = listOfNotNull(person.birthCity, person.birthStateProvince, person.birthCountry).joinToString(", ")

                emit(Player(
                    id = person.id.toString(),
                    name = person.fullName,
                    number = person.primaryNumber ?: "",
                    age = person.currentAge ?: 0,
                    birthDate = person.birthDate ?: "",
                    birthLocation = birthPlace,
                    height = person.height ?: "",
                    weight = person.weight ?: 0,
                    bats = person.bats?.description ?: "",
                    throws = person.throws?.description ?: "",
                    debutDate = person.mlbDebutDate ?: "",
                    headshotUrl = "https://img.mlbstatic.com/mlb-photos/image/upload/d_people:generic:headshot:67:current.png/w_426,q_auto:best/v1/people/${person.id}/headshot/67/current",
                    currentStats = if (currentBatting != null || currentPitching != null) {
                        YearlyStats(
                            year = "2024",
                            team = "", 
                            avg = currentBatting?.avg ?: ".000",
                            obp = currentBatting?.obp ?: ".000",
                            slg = currentBatting?.slg ?: ".000",
                            ops = currentBatting?.ops ?: ".000",
                            hr = currentBatting?.homeRuns ?: 0,
                            rbi = currentBatting?.rbi ?: 0,
                            games = currentBatting?.gamesPlayed ?: 0,
                            pa = currentBatting?.plateAppearances ?: 0,
                            era = currentPitching?.era ?: "-.--",
                            w = currentPitching?.wins ?: 0,
                            l = currentPitching?.losses ?: 0,
                            k = currentPitching?.strikeouts ?: currentPitching?.strikeOuts ?: 0,
                            ip = currentPitching?.inningsPitched ?: "0.0",
                            whip = currentPitching?.whip ?: "-.--"
                        )
                    } else null,
                    careerStats = history
                ))
            } else {
                emit(null)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(null)
        }
    }
}
