package com.example.testapp.model

import kotlinx.serialization.Serializable

@Serializable
data class PlayerSearchResult(
    val id: Int,
    val fullName: String,
    val teamName: String = "",
    val position: String = ""
)

@Serializable
data class PlayerSearchResponse(
    val people: List<PlayerSearchResult> = emptyList()
)
