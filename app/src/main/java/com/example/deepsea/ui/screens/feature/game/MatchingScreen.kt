package com.example.deepsea.ui.screens.feature.game

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun MatchingScreen(
    currentPlayer: Player,
    onMatchFound: () -> Unit,
    onCancel: () -> Unit
) {
    var matchingProgress by remember { mutableStateOf(0f) }
    var matchingMessage by remember { mutableStateOf("Looking for opponents...") }

    LaunchedEffect(key1 = true) {
        val totalDelay = 5000L // 5 seconds for matching simulation
        val intervalDelay = 50L
        val steps = totalDelay / intervalDelay

        for (i in 0..steps.toInt()) {
            matchingProgress = i / steps.toFloat()
            delay(intervalDelay)

            if (i == (steps * 0.3).toInt()) {
                matchingMessage = "Matching with players of similar level..."
            } else if (i == (steps * 0.7).toInt()) {
                matchingMessage = "Found a challenger! Preparing game..."
            }
        }

        onMatchFound()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(100.dp),
            color = Color(0xFF0078D7),
            trackColor = Color(0xFFE1E8ED),
            strokeWidth = 8.dp,
            progress = matchingProgress
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = matchingMessage,
            fontSize = 18.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Level: ${currentPlayer.level}",
            fontSize = 16.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(48.dp))

        OutlinedButton(
            onClick = onCancel,
            border = BorderStroke(1.dp, Color(0xFF0078D7))
        ) {
            Icon(
                imageVector = Icons.Rounded.Close,
                contentDescription = null,
                tint = Color(0xFF0078D7)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Cancel", color = Color(0xFF0078D7))
        }
    }
}