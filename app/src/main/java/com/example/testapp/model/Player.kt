package com.example.testapp.model

import kotlinx.serialization.Serializable

@Serializable
data class Player(
    val id: String = "",
    val name: String = "",
    val team: String = "",
    val teamId: Int? = null,
    val position: String = "",
    val number: String = "",
    val age: Int = 0,
    val birthDate: String = "",
    val birthLocation: String = "",
    val height: String = "",
    val weight: Int = 0,
    val bats: String = "",
    val throws: String = "",
    val debutDate: String = "",
    val headshotUrl: String = "",
    // Current Season Stats
    val currentStats: YearlyStats? = null,
    // Historical Stats
    val careerStats: List<YearlyStats> = emptyList(),
    val milbStats: List<YearlyStats> = emptyList()
)

@Serializable
data class YearlyStats(
    val year: String,
    val team: String,
    val league: String = "MLB",
    // Batting
    val games: Int = 0,
    val pa: Int = 0,
    val ab: Int = 0,
    val h: Int = 0,
    val hr: Int = 0,
    val r: Int = 0,
    val rbi: Int = 0,
    val sb: Int = 0,
    val bb: Int = 0,
    val so: Int = 0,
    val avg: String = ".000",
    val obp: String = ".000",
    val slg: String = ".000",
    val ops: String = ".000",
    // Pitching
    val era: String = "-.--",
    val w: Int = 0,
    val l: Int = 0,
    val k: Int = 0,
    val p_bb: Int = 0,
    val ip: String = "0.0",
    val whip: String = "-.--",
    val gs: Int = 0,
    val sv: Int = 0,
    val earnedRuns: Int? = null
)
