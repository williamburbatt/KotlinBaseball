package com.example.testapp.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest

@Composable
fun TeamLogo(teamId: Int, modifier: Modifier = Modifier) {
    val logoUrl = "https://www.mlbstatic.com/team-logos/$teamId.svg"
    
    Surface(
        modifier = modifier,
        shape = CircleShape,
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Box(
            modifier = Modifier.padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(logoUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "Team Logo",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 1.5.sp
        )
        HorizontalDivider(modifier = Modifier.padding(top = 4.dp), thickness = 2.dp, color = MaterialTheme.colorScheme.primaryContainer)
    }
}

@Composable
fun PositionBadge(position: String) {
    Surface(
        color = MaterialTheme.colorScheme.primary,
        shape = RoundedCornerShape(4.dp),
        modifier = Modifier.size(width = 36.dp, height = 24.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = position,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Row(modifier = Modifier.padding(end = 12.dp)) {
        Text(text = "$label: ", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
        Text(text = value, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
    }
}
