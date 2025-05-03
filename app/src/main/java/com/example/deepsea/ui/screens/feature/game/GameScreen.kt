package com.example.deepsea.ui.screens.feature.game

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.deepsea.R
import com.example.deepsea.ui.components.RewardDialog
import kotlinx.coroutines.delay
import kotlin.random.Random

// Game states
enum class GameState {
    WELCOME,
    MATCHING,
    PLAYING,
    RESULT
}

// Game modes
enum class GameMode {
    VOCABULARY,
    GRAMMAR,
    LISTENING
}

// Player data
data class Player(
    val id: String,
    val name: String,
    val avatar: String? = null,
    val level: Int,
    val xp: Int
)

// Question data
data class Question(
    val text: String,
    val options: List<String>,
    val correctAnswer: Int
)

@Composable
fun GamePage() {
    val primaryColor = Color(0xFF0078D7)
    val accentColor = Color(0xFFFF9500)
    val backgroundColor = Color(0xFFF2F7FC)

    var gameState by remember { mutableStateOf(GameState.WELCOME) }
    var selectedMode by remember { mutableStateOf<GameMode?>(null) }
    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    var playerScore by remember { mutableIntStateOf(0) }
    var opponentScore by remember { mutableIntStateOf(0) }
    var showRewardDialog by remember { mutableStateOf(false) }

    // Sample data
    val currentPlayer = Player(
        id = "user1",
        name = "Player",
        level = 8,
        xp = 4500
    )

    val opponent = Player(
        id = "opponent1",
        name = "Opponent",
        level = 9,
        xp = 5200
    )

    val questions = remember {
        listOf(
            Question(
                text = "What is the correct translation of 'Hello' in Spanish?",
                options = listOf("Bonjour", "Hola", "Ciao", "Hallo"),
                correctAnswer = 1
            ),
            Question(
                text = "Choose the correct conjugation: 'He ___ to the store yesterday.'",
                options = listOf("go", "goes", "went", "going"),
                correctAnswer = 2
            ),
            Question(
                text = "What is the Japanese word for 'thank you'?",
                options = listOf("Arigatou", "Konnichiwa", "Sayonara", "Sumimasen"),
                correctAnswer = 0
            ),
            Question(
                text = "Which sentence is grammatically correct?",
                options = listOf(
                    "She don't like chocolate",
                    "She doesn't likes chocolate",
                    "She doesn't like chocolate",
                    "She not like chocolate"
                ),
                correctAnswer = 2
            ),
            Question(
                text = "In French, what is the correct way to say 'Good night'?",
                options = listOf("Bon matin", "Bonne nuit", "Bon soir", "Bon journée"),
                correctAnswer = 1
            )
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = backgroundColor
    ) {
        when (gameState) {
            GameState.WELCOME -> {
                WelcomeScreen(
                    onModeSelected = { mode ->
                        selectedMode = mode
                        gameState = GameState.MATCHING
                    }
                )
            }

            GameState.MATCHING -> {
                MatchingScreen(
                    currentPlayer = currentPlayer,
                    onMatchFound = {
                        gameState = GameState.PLAYING
                        currentQuestionIndex = 0
                        playerScore = 0
                        opponentScore = 0
                    },
                    onCancel = {
                        gameState = GameState.WELCOME
                    }
                )
            }

            GameState.PLAYING -> {
                if (currentQuestionIndex < questions.size) {
                    GamePlayScreen(
                        currentPlayer = currentPlayer,
                        opponent = opponent,
                        question = questions[currentQuestionIndex],
                        playerScore = playerScore,
                        opponentScore = opponentScore,
                        currentQuestionIndex = currentQuestionIndex,
                        totalQuestions = questions.size,
                        onAnswerSelected = { isCorrect ->
                            if (isCorrect) {
                                playerScore++
                            }

                            // Simulate opponent answering
                            if (Random.nextBoolean()) {
                                opponentScore++
                            }

                            currentQuestionIndex++

                            if (currentQuestionIndex >= questions.size) {
                                gameState = GameState.RESULT
                            }
                        }
                    )
                }
            }

            GameState.RESULT -> {
                ResultScreen(
                    currentPlayer = currentPlayer,
                    opponent = opponent,
                    playerScore = playerScore,
                    opponentScore = opponentScore,
                    totalQuestions = questions.size,
                    onPlayAgain = {
                        gameState = GameState.WELCOME
                    },
                    onClaimReward = {
                        showRewardDialog = true
                    }
                )
            }
        }

        if (showRewardDialog) {
            RewardDialog(
                reward = if (playerScore > opponentScore) "50 XP and a Golden Badge"
                else "10 XP Consolation Reward",
                isWinner = playerScore > opponentScore,
                onDismiss = { showRewardDialog = false }
            )
        }
    }
}

@Composable
fun LeaderboardItemRow(entry: LeaderboardEntry) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "#${entry.rank}",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(40.dp)
        )

        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0xFFE1E8ED)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = entry.username.first().toString(),
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = entry.username,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = "${entry.score} XP",
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0078D7)
        )
    }
}

data class LeaderboardEntry(
    val username: String,
    val score: Int,
    val rank: Int
)

@Preview(showBackground = true)
@Composable
fun GameScreenPreview() {
    GamePage()
}