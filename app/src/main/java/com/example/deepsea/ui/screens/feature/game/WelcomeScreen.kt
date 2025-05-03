package com.example.deepsea.ui.screens.feature.game

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.deepsea.R
import com.example.deepsea.ui.components.GameModeCard

@Composable
fun WelcomeScreen(onModeSelected: (GameMode) -> Unit) {
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
            text = "Language Battle",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold,
                color = primaryColor
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Challenge other learners and win rewards!",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "SELECT A GAME MODE",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.DarkGray
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Game mode cards
        GameModeCard(
            title = "Vocabulary Race",
            description = "Race to match words with their correct meanings",
            iconContent = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_bolt),
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(32.dp)
                )

            },
            onClick = { onModeSelected(GameMode.VOCABULARY) }
        )

        Spacer(modifier = Modifier.height(12.dp))

        GameModeCard(
            title = "Grammar Challenge",
            description = "Choose the correct grammatical form in sentences",
            iconContent = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_emoji_event),
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(32.dp)
                )
            },
            onClick = { onModeSelected(GameMode.GRAMMAR) }
        )

        Spacer(modifier = Modifier.height(12.dp))

        GameModeCard(
            title = "Listening Duel",
            description = "Test your listening skills against an opponent",
            iconContent = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_group),
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(32.dp)
                )
            },
            onClick = { onModeSelected(GameMode.LISTENING) }
        )
    }
}