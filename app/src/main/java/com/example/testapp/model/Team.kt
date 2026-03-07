package com.example.testapp.model

import kotlinx.serialization.Serializable

@Serializable
data class Team(
    val id: Int,
    val name: String,
    val teamName: String,
    val location: String = ""
)
