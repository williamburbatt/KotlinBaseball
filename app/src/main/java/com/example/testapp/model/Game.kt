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
    val startTime: String? = null,
    val awayHits: Int? = null,
    val homeHits: Int? = null,
    val awayErrors: Int? = null,
    val homeErrors: Int? = null
)

@Serializable
data class BoxScore(
    val gameId: Int,
    val awayTeamName: String = "",
    val homeTeamName: String = "",
    val status: String = "",
    val awayPlayers: List<BoxScorePlayer>,
    val homePlayers: List<BoxScorePlayer>,
    val awayLineScore: LineScore = LineScore(),
    val homeLineScore: LineScore = LineScore()
)

@Serializable
data class LineScore(
    val runs: Int = 0,
    val hits: Int = 0,
    val errors: Int = 0
)

@Serializable
data class BoxScorePlayer(
    val id: Int,
    val name: String,
    val position: String,
    val battingStats: BattingStats? = null,
    val pitchingStats: PitchingStats? = null,
    val isCurrentBatter: Boolean = false,
    val isCurrentPitcher: Boolean = false
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
