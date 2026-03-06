package com.example.testapp.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Player(
    val id: String = "",
    val name: String = "",
    val team: String = "",
    val position: String = "",
    val average: Double = 0.0,
    val homeRuns: Int = 0,
    val rbi: Int = 0
)
