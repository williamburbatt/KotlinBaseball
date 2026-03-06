package com.example.testapp.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Team(
    val id: Int,
    val name: String,
    val teamName: String,
    val location: String = ""
)
