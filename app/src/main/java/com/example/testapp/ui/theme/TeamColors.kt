package com.example.testapp.ui.theme

import androidx.compose.ui.graphics.Color

object TeamColors {
    private val colors = mapOf(
        108 to Color(0xFFBA0021), // LAA - Angels Red
        109 to Color(0xFFA71930), // ARI - Sedona Red
        110 to Color(0xFFDF4601), // BAL - Orioles Orange
        111 to Color(0xFFBD3039), // BOS - Red Sox Red
        112 to Color(0xFF0E3386), // CHC - Cubs Blue
        113 to Color(0xFFC6011F), // CIN - Reds Red
        114 to Color(0xFF0C2340), // CLE - Guardians Navy
        115 to Color(0xFF333366), // COL - Rockies Purple
        116 to Color(0xFF0C2340), // DET - Tigers Navy
        117 to Color(0xFF002D62), // HOU - Astros Navy
        118 to Color(0xFF004687), // KC - Royals Blue
        119 to Color(0xFF005A9C), // LAD - Dodgers Blue
        120 to Color(0xFFAB0003), // WSH - Nationals Red
        121 to Color(0xFF002D72), // NYM - Mets Blue
        133 to Color(0xFF003831), // OAK - Athletics Green
        134 to Color(0xFF27251F), // PIT - Pirates Black
        135 to Color(0xFF2F241D), // SD - Padres Brown
        136 to Color(0xFF005C5C), // SEA - Mariners Northwest Green
        137 to Color(0xFFFD5A1E), // SF - Giants Orange
        138 to Color(0xFFC41E3A), // STL - Cardinals Red
        139 to Color(0xFF092C5C), // TB - Rays Navy
        140 to Color(0xFF003278), // TEX - Rangers Blue
        141 to Color(0xFF134A8E), // TOR - Blue Jays Blue
        142 to Color(0xFF002B5C), // MIN - Twins Navy
        143 to Color(0xFFE81828), // PHI - Phillies Red
        144 to Color(0xFF13274F), // ATL - Braves Navy
        145 to Color(0xFF27251F), // CWS - White Sox Black
        146 to Color(0xFF00A3E0), // MIA - Marlins Blue
        147 to Color(0xFF003087), // NYY - Yankees Navy
        158 to Color(0xFF12284B), // MIL - Brewers Navy
    )

    fun getTeamColor(teamId: Int?): Color {
        return colors[teamId] ?: Color.LightGray
    }
}
