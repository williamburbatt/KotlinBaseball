package com.example.testapp.model

import kotlinx.serialization.Serializable

@Serializable
data class Game(
    val id: Int,
    val awayTeam: String,
    val homeTeam: String,
    val awayScore: Int?,
    val homeScore: Int?,
    val status: String,
    val awayHits: Int? = null,
    val homeHits: Int? = null,
    val awayErrors: Int? = null,
    val homeErrors: Int? = null
)

@Serializable
data class BoxScore(
    val gameId: Int,
    val awayPlayers: List<BoxScorePlayer>,
    val homePlayers: List<BoxScorePlayer>
)

@Serializable
data class BoxScorePlayer(
    val id: Int,
    val name: String,
    val position: String,
    val battingStats: BattingStats? = null,
    val pitchingStats: PitchingStats? = null
)

@Serializable
data class BattingStats(
    val ab: Int = 0,
    val r: Int = 0,
    val h: Int = 0,
    val rbi: Int = 0,
    val hr: Int = 0,
    val lob: Int = 0
)

@Serializable
data class PitchingStats(
    val ip: String = "0.0",
    val h: Int = 0,
    val r: Int = 0,
    val er: Int = 0,
    val bb: Int = 0,
    val k: Int = 0
)
