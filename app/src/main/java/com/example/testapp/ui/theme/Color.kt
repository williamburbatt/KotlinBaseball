package com.example.testapp.ui.theme

import androidx.compose.ui.graphics.Color

// Your original palette
val DimGrey = Color(0xFF6C756B)
val CoolSteel = Color(0xFF93ACB5)
val BabyBlueIce = Color(0xFF96C5F7)
val IcyBlue = Color(0xFFA9D3FF)
val GhostWhite = Color(0xFFF2F4FF)

// High-contrast accessibility variants (darkened versions of your palette)
val DarkDimGrey = Color(0xFF353B35)
val DarkSteel = Color(0xFF3F5258)
val DeepIceBlue = Color(0xFF003355)

// Material 3 Functional Mapping
val LightPrimary = DarkDimGrey
val LightOnPrimary = Color.White
val LightPrimaryContainer = IcyBlue
val LightOnPrimaryContainer = DarkDimGrey

val LightSecondary = DarkSteel
val LightOnSecondary = Color.White
val LightSecondaryContainer = BabyBlueIce
val LightOnSecondaryContainer = DarkSteel

val LightTertiary = CoolSteel
val LightBackground = GhostWhite
val LightSurface = Color.White
val LightOutline = DimGrey
