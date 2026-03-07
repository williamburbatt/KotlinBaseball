package com.example.testapp.ui.theme

import androidx.compose.ui.graphics.Color

object TeamColors {
    private val colors = mapOf(
        108 to Color(0xFFE31937), // LAA
        109 to Color(0xFF002D62), // ARI
        110 to Color(0xFFCE1141), // ATL
        111 to Color(0xFFDF4601), // BAL
        112 to Color(0xFFBD3039), // BOS
        113 to Color(0xFF0E3386), // CHC
        114 to Color(0xFF27251F), // CWS
        115 to Color(0xFFC6011F), // CIN
        116 to Color(0xFF002B5C), // CLE
        117 to Color(0xFF333366), // COL
        118 to Color(0xFF0C2340), // DET
        119 to Color(0xFF002D62), // HOU
        120 to Color(0xFF004687), // KC
        121 to Color(0xFF005A9C), // LAD
        133 to Color(0xFF00A3E0), // MIA
        134 to Color(0xFF12284B), // MIL
        135 to Color(0xFF002B5C), // MIN
        136 to Color(0xFF002D72), // NYM
        137 to Color(0xFF003087), // NYY
        138 to Color(0xFF003831), // OAK
        139 to Color(0xFFE81828), // PHI
        140 to Color(0xFF27251F), // PIT
        141 to Color(0xFF002D62), // SD
        142 to Color(0xFFFD5A1E), // SF
        143 to Color(0xFF005C5C), // SEA
        144 to Color(0xFFC41E3A), // STL
        145 to Color(0xFF092C5C), // TB
        146 to Color(0xFF003278), // TEX
        147 to Color(0xFF002D62), // TOR
        158 to Color(0xFFAB0003), // WSH
    )

    fun getTeamColor(teamId: Int?): Color {
        return colors[teamId] ?: Color.Gray
    }
}
