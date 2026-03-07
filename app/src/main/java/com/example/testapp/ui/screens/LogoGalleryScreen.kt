package com.example.testapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.testapp.R

data class LogoItem(val name: String, val resId: Int)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogoGalleryScreen(onBack: () -> Unit) {
    val logos = listOf(
        LogoItem("Classic Ball", R.drawable.ic_logo_classic_ball),
        LogoItem("Crossed Bats", R.drawable.ic_logo_crossed_bats),
        LogoItem("The Stadium", R.drawable.ic_logo_stadium),
        LogoItem("Diamond", R.drawable.ic_logo_diamond),
        LogoItem("Home Plate", R.drawable.ic_logo_home_plate)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Logo Gallery") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            items(logos) { logo ->
                LogoShowcaseCard(logo)
            }
        }
    }
}

@Composable
fun LogoShowcaseCard(logo: LogoItem) {
    Column {
        Text(
            text = logo.name,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Light Mode Showcase
            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF2F4FF), // Ghost White
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            painter = painterResource(id = logo.resId),
                            contentDescription = null,
                            modifier = Modifier.size(80.dp),
                            tint = Color.Unspecified
                        )
                    }
                }
                Text("Light BG", style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(top = 4.dp))
            }

            // Dark Mode Showcase
            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF1A1C1E), // Dark Theme Surface
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            painter = painterResource(id = logo.resId),
                            contentDescription = null,
                            modifier = Modifier.size(80.dp),
                            tint = Color.Unspecified
                        )
                    }
                }
                Text("Dark BG", style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(top = 4.dp))
            }
        }
    }
}
