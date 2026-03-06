package com.example.testapp.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Player(
    val id: String = "",
    val name: String = "",
    val team: String = "",
    val position: String = "",
    // Batting
    val average: Double = 0.0,
    val homeRuns: Int = 0,
    val rbi: Int = 0,
    // Pitching
    val era: String = "-.--",
    val wins: Int = 0,
    val losses: Int = 0,
    val strikeOuts: Int = 0,
    val inningsPitched: String = "0.0"
)
