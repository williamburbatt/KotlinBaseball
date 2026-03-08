package com.example.testapp.repository

import com.example.testapp.api.Leader
import com.example.testapp.api.MlbStatsApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LeadersRepository @Inject constructor(
    private val api: MlbStatsApi
) {
    fun getLeaders(category: String, group: String, season: Int =2025): Flow<List<Leader>> = flow {
        try {
            val leadersResponse = api.getLeagueLeaders(category, group, season)
            val allLeaders = leadersResponse.leagueLeaders.flatMap { it.leaders }
            emit(allLeaders)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }
}
