package com.example.deepsea.ui.screens.feature.game

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.deepsea.data.model.game.*
import com.example.deepsea.ui.components.RewardDialog
import com.example.deepsea.ui.viewmodel.game.GameViewModel
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
    val id: Long,
    val text: String,
    val options: List<String>,
    val correctAnswer: Int,
    val gameMode: String,
    val language: String
)

@Composable
fun GamePage(viewModel: GameViewModel = viewModel()) {
    val primaryColor = Color(0xFF0078D7)
    val accentColor = Color(0xFFFF9500)
    val backgroundColor = Color(0xFFF2F7FC)

    var gameState by remember { mutableStateOf(GameState.WELCOME) }
    var selectedMode by remember { mutableStateOf<GameMode?>(null) }
    var currentMatch by remember { mutableStateOf<Match?>(null) }
    var questions by remember { mutableStateOf<List<Question>>(emptyList()) }
    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    var playerScore by remember { mutableIntStateOf(0) }
    var opponentScore by remember { mutableIntStateOf(0) }
    var showRewardDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }


    // Map GameViewModel.Player to ui.screens.feature.game.Player
    val currentPlayer = viewModel.currentPlayer.value?.let {
        Player(
            id = it.id,
            name = it.name,
            level = it.level,
            xp = it.xp
        )
    } ?: Player(
        id = "0",
        name = "Unknown",
        level = 1,
        xp = 0
    )

    // Fake opponent
    var opponent by remember {
        mutableStateOf(
            Player(
                id = "fake123",
                name = "AI Opponent",
                avatar = null,
                level = 5,
                xp = 3000
            )
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = backgroundColor
    ) {
        Column {
            // Display error message if any
            errorMessage?.let { message ->
                Text(
                    text = message,
                    color = Color.Red,
                    modifier = Modifier.padding(16.dp)
                )
            }

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
                            viewModel.startMatch(
                                request = GameStartRequest(
                                    userId = 1,
                                    gameMode = selectedMode.toString(),
                                    language = "ENGLISH"
                                ),
                                onSuccess = { match ->
                                    currentMatch = match
                                    viewModel.getMatchQuestions(
                                        matchId = match.id,
                                        onSuccess = { fetchedQuestions ->
                                            questions = fetchedQuestions
                                            gameState = GameState.PLAYING
                                            currentQuestionIndex = 0
                                            playerScore = 0
                                            opponentScore = 0
                                        },
                                        onError = { error ->
                                            errorMessage = error
                                        }
                                    )
                                },
                                onError = { error ->
                                    errorMessage = error
                                }
                            )
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
                            onAnswerSelected = { selectedAnswer ->
                                viewModel.submitAnswer(
                                    request = GameAnswerRequest(
                                        matchId = currentMatch?.id ?: 0,
                                        questionId = questions[currentQuestionIndex].id,
                                        userId = currentPlayer.id.toLong(),
                                        selectedAnswer = selectedAnswer
                                    ),
                                    onSuccess = { response ->
                                        if (response.isCorrect) {
                                            playerScore++
                                        }
                                        // Simulate opponent answering
                                        if (Random.nextBoolean()) {
                                            opponentScore++
                                        }
                                        currentQuestionIndex++
                                        if (response.matchCompleted) {
                                            gameState = GameState.RESULT
                                        }
                                    },
                                    onError = { error ->
                                        errorMessage = error
                                    }
                                )
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
                            currentMatch = null
                            questions = emptyList()
                            currentQuestionIndex = 0
                            playerScore = 0
                            opponentScore = 0
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