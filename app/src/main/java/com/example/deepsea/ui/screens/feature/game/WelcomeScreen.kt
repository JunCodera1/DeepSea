package com.example.deepsea.ui.screens.feature.game

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.deepsea.R
import com.example.deepsea.ui.components.GameModeCard

@Composable
fun WelcomeScreen(
    onModeSelected: (GameMode) -> Unit,
    onDifficultySelected: (DifficultyLevel) -> Unit,
    currentPlayer: Player
) {
    val primaryColor = Color(0xFF0078D7)
    val accentColor = Color(0xFFFF9500)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Language Treasure Hunt",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold,
                color = primaryColor
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Explore the island and uncover language treasures! Streak: ${currentPlayer.streakDays} days",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "CHOOSE YOUR ADVENTURE",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.DarkGray
        )

        Spacer(modifier = Modifier.height(16.dp))

        DifficultySelector(
            onDifficultySelected = onDifficultySelected,
            initialDifficulty = DifficultyLevel.EASY
        )

        Spacer(modifier = Modifier.height(16.dp))

        GameModeCard(
            title = "Quiz Jungle",
            description = "Answer questions to find treasures",
            iconContent = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_bolt),
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(32.dp)
                )
            },
            onClick = { onModeSelected(GameMode.QUIZ) }
        )

        Spacer(modifier = Modifier.height(12.dp))

        GameModeCard(
            title = "Scramble Ruins",
            description = "Unscramble words for rewards",
            iconContent = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_emoji_event),
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(32.dp)
                )
            },
            onClick = { onModeSelected(GameMode.SCRAMBLE) }
        )

        Spacer(modifier = Modifier.height(12.dp))

        GameModeCard(
            title = "Match Caves",
            description = "Match words to uncover gold",
            iconContent = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_group),
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(32.dp)
                )
            },
            onClick = { onModeSelected(GameMode.MATCH) }
        )

        Spacer(modifier = Modifier.height(12.dp))

        GameModeCard(
            title = "Daily Challenge",
            description = "Test your skills with a daily mix",
            iconContent = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_challenge),
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(32.dp)
                )
            },
            onClick = { onModeSelected(GameMode.DAILY_CHALLENGE) }
        )
    }
}

@Composable
fun DifficultySelector(
    onDifficultySelected: (DifficultyLevel) -> Unit,
    initialDifficulty: DifficultyLevel
) {
    var selectedDifficulty by remember { mutableStateOf(initialDifficulty) }
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        DifficultyLevel.values().forEach { difficulty ->
            Button(
                onClick = { selectedDifficulty = difficulty; onDifficultySelected(difficulty) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedDifficulty == difficulty) Color.Gray else Color.LightGray
                )
            ) {
                Text(difficulty.name)
            }
        }
    }
}