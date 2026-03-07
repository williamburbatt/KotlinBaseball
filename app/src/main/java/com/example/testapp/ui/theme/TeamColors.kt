package com.example.testapp.ui.theme

import androidx.compose.ui.graphics.Color

object TeamColors {
    private val colors = mapOf(
        108 to Color(0xFFBA0021), // LAA
        109 to Color(0xFFA71930), // ARI
        110 to Color(0xFFDF4601), // BAL
        111 to Color(0xFFBD3039), // BOS
        112 to Color(0xFF0E3386), // CHC
        113 to Color(0xFFC6011F), // CIN
        114 to Color(0xFF0C2340), // CLE
        115 to Color(0xFF333366), // COL
        116 to Color(0xFF0C2340), // DET
        117 to Color(0xFF002D62), // HOU
        118 to Color(0xFF004687), // KC
        119 to Color(0xFF005A9C), // LAD
        120 to Color(0xFFAB0003), // WSH
        121 to Color(0xFF002D72), // NYM
        133 to Color(0xFF003831), // OAK
        134 to Color(0xFF27251F), // PIT
        135 to Color(0xFF2F241D), // SD
        136 to Color(0xFF005C5C), // SEA
        137 to Color(0xFFFD5A1E), // SF
        138 to Color(0xFFC41E3A), // STL
        139 to Color(0xFF092C5C), // TB
        140 to Color(0xFF003278), // TEX
        141 to Color(0xFF134A8E), // TOR
        142 to Color(0xFF002B5C), // MIN
        143 to Color(0xFFE81828), // PHI
        144 to Color(0xFF13274F), // ATL
        145 to Color(0xFF27251F), // CWS
        146 to Color(0xFF00A3E0), // MIA
        147 to Color(0xFF003087), // NYY
        158 to Color(0xFF12284B), // MIL
    )

    private val abbreviations = mapOf(
        108 to "LAA",
        109 to "ARI",
        110 to "BAL",
        111 to "BOS",
        112 to "CHC",
        113 to "CIN",
        114 to "CLE",
        115 to "COL",
        116 to "DET",
        117 to "HOU",
        118 to "KC",
        119 to "LAD",
        120 to "WSH",
        121 to "NYM",
        133 to "OAK",
        134 to "PIT",
        135 to "SD",
        136 to "SEA",
        137 to "SF",
        138 to "STL",
        139 to "TB",
        140 to "TEX",
        141 to "TOR",
        142 to "MIN",
        143 to "PHI",
        144 to "ATL",
        145 to "CWS",
        146 to "MIA",
        147 to "NYY",
        158 to "MIL"
    )

    fun getTeamColor(teamId: Int?): Color {
        return colors[teamId] ?: Color.LightGray
    }

    fun getTeamAbbreviation(teamId: Int?): String? {
        return abbreviations[teamId]
    }
}
