package com.example.testapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.testapp.R
import com.example.testapp.ui.theme.TestAppTheme

@Composable
fun SportSelectionScreen(onSportClick: (Int) -> Unit) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Surface(shadowElevation = 4.dp) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 48.dp, bottom = 16.dp, start = 24.dp)
                ) {
                    Text(
                        text = "Leagues",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Select a league to view teams and rosters.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            LeagueCard(
                name = "Major League",
                description = "MLB - The Big Leagues",
                iconRes = R.drawable.ic_logo_stadium,
                iconColor = Color(0xFF002D72), // MLB Blue
                onClick = { onSportClick(1) }
            )
            
            LeagueCard(
                name = "Triple-A",
                description = "International & Pacific Coast Leagues",
                iconRes = R.drawable.ic_logo_diamond,
                iconColor = Color(0xFFBA0C2F), // AAA Red
                onClick = { onSportClick(11) }
            )
            
            LeagueCard(
                name = "Double-A",
                description = "Eastern, Southern & Texas Leagues",
                iconRes = R.drawable.ic_logo_diamond,
                iconColor = Color(0xFF005A9C), // AA Blue
                onClick = { onSportClick(12) }
            )
            
            LeagueCard(
                name = "High-A",
                description = "South Atlantic, Midwest & NW Leagues",
                iconRes = R.drawable.ic_logo_diamond,
                iconColor = Color(0xFF003831), // High-A Green
                onClick = { onSportClick(13) }
            )
            
            LeagueCard(
                name = "Single-A",
                description = "California, Carolina & Florida State Leagues",
                iconRes = R.drawable.ic_logo_diamond,
                iconColor = Color(0xFFFD5A1E), // Single-A Orange
                onClick = { onSportClick(14) }
            )
        }
    }
}

@Composable
fun LeagueCard(
    name: String, 
    description: String, 
    iconRes: Int,
    iconColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = iconColor
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name, 
                    style = MaterialTheme.typography.titleLarge, 
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = description, 
                    style = MaterialTheme.typography.bodyMedium, 
                    color = MaterialTheme.colorScheme.outline
                )
            }
            
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outlineVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SportSelectionPreview() {
    TestAppTheme {
        SportSelectionScreen(onSportClick = {})
    }
}
