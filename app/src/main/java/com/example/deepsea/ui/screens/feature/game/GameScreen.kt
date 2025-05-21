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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.deepsea.ui.components.RewardTreasureDialog
import com.example.deepsea.utils.allQuestions

// Game states
enum class GameState {
    WELCOME,
    PLAYING,
    RESULT
}

// Game modes
enum class GameMode {
    QUIZ,
    SCRAMBLE,
    MATCH,
    DAILY_CHALLENGE
}

// Difficulty levels
enum class DifficultyLevel {
    EASY,
    MEDIUM,
    HARD
}

// Player data
data class Player(
    val id: String,
    val name: String,
    val avatar: String? = null,
    val level: Int,
    val xp: Int,
    val streakDays: Int = 0,
    val lastPlayed: Long = 0
)

// Question data
data class Question(
    val id: Long,
    val text: String,
    val options: List<String>,
    val correctAnswer: Int,
    val gameMode: String,
    val language: String,
    val explanation: String,
    val difficulty: DifficultyLevel = DifficultyLevel.EASY,
    val imageResource: Int? = null
)

// Game statistics
data class GameStats(
    val totalAnswers: Int = 0,
    val correctAnswers: Int = 0,
    val timeSpent: Int = 0,
    val streak: Int = 0
)

@Composable
fun GameScreen() {
    val primaryColor = Color(0xFF0078D7)
    val accentColor = Color(0xFFFF9500)
    val backgroundColor = Color(0xFFF2F7FC)

    var gameState by remember { mutableStateOf(GameState.WELCOME) }
    var selectedMode by remember { mutableStateOf<GameMode?>(null) }
    var selectedDifficulty by remember { mutableStateOf(DifficultyLevel.EASY) }
    var questions by remember { mutableStateOf<List<Question>>(emptyList()) }
    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    var playerScore by remember { mutableIntStateOf(0) }
    var playerHealth by remember { mutableIntStateOf(100) }
    var mapProgress by remember { mutableIntStateOf(0) }
    var showRewardDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var gameStats by remember { mutableStateOf(GameStats()) }
    var startTime by remember { mutableLongStateOf(0L) }
    var playerStreak by remember { mutableIntStateOf(0) }

    // Simulate daily streak functionality
    val isDailyStreak = remember { derivedStateOf { System.currentTimeMillis() - startTime < 86400000 } }

    val currentPlayer = Player(
        id = "1",
        name = "Treasure Hunter",
        level = 1,
        xp = 0,
        streakDays = playerStreak
    )


    // Lọc câu hỏi theo chế độ và xử lý lỗi
    LaunchedEffect(selectedMode, selectedDifficulty) {
        if (selectedMode != null) {
            startTime = System.currentTimeMillis()
            questions = allQuestions
                .filter {
                    it.gameMode == selectedMode.toString().uppercase() &&
                            it.difficulty == selectedDifficulty
                }

            // For Daily Challenge, pick questions from different modes
            if (selectedMode == GameMode.DAILY_CHALLENGE) {
                questions = allQuestions.filter {
                    it.difficulty == selectedDifficulty
                }.shuffled().take(5)
            }

            if (questions.isEmpty()) {
                errorMessage = "No questions available for this mode and difficulty!"
                gameState = GameState.WELCOME
            } else {
                gameState = GameState.PLAYING
                currentQuestionIndex = 0
                playerScore = 0
                playerHealth = 100
                mapProgress = 0
                gameStats = GameStats()
                errorMessage = null
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = backgroundColor
    ) {
        Column {
            // Hiển thị lỗi nếu có
            errorMessage?.let { message ->
                Text(
                    text = message,
                    color = Color.Red,
                    modifier = Modifier.padding(16.dp),
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { gameState = GameState.WELCOME },
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
                ) {
                    Text("Back to Welcome", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }

            when (gameState) {
                GameState.WELCOME -> {
                    WelcomeScreen(
                        onModeSelected = { mode ->
                            selectedMode = mode
                        },
                        onDifficultySelected = { difficulty ->
                            selectedDifficulty = difficulty
                        },
                        currentPlayer = currentPlayer
                    )
                }

                GameState.PLAYING -> {
                    if (currentQuestionIndex < questions.size) {
                        Text(
                            text = when (selectedMode) {
                                GameMode.QUIZ -> "Exploring the Quiz Jungle... (${selectedDifficulty.name})"
                                GameMode.SCRAMBLE -> "Decoding ancient Scramble Ruins... (${selectedDifficulty.name})"
                                GameMode.MATCH -> "Navigating the Match Caves... (${selectedDifficulty.name})"
                                GameMode.DAILY_CHALLENGE -> "Today's Language Challenge! (${selectedDifficulty.name})"
                                else -> ""
                            },
                            modifier = Modifier.padding(16.dp),
                            color = primaryColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )

                        GamePlayScreen(
                            currentPlayer = currentPlayer,
                            question = questions[currentQuestionIndex],
                            playerScore = playerScore,
                            playerHealth = playerHealth,
                            mapProgress = mapProgress,
                            currentQuestionIndex = currentQuestionIndex,
                            totalQuestions = questions.size,
                            onAnswerSelected = { selectedAnswer, timeUsed ->
                                gameStats = gameStats.copy(
                                    totalAnswers = gameStats.totalAnswers + 1,
                                    timeSpent = gameStats.timeSpent + timeUsed
                                )

                                if (selectedAnswer == questions[currentQuestionIndex].correctAnswer) {
                                    playerScore++
                                    mapProgress += 100 / questions.size
                                    gameStats = gameStats.copy(
                                        correctAnswers = gameStats.correctAnswers + 1,
                                        streak = gameStats.streak + 1
                                    )
                                } else {
                                    playerHealth -= 20
                                    gameStats = gameStats.copy(streak = 0)
                                }

                                currentQuestionIndex++
                                if (currentQuestionIndex >= questions.size || playerHealth <= 0) {
                                    gameState = GameState.RESULT

                                    if (selectedMode == GameMode.DAILY_CHALLENGE &&
                                        playerScore >= questions.size * 0.7) {
                                        playerStreak++
                                    }
                                }
                            },
                            gameMode = selectedMode ?: GameMode.QUIZ,
                            difficulty = selectedDifficulty
                        )
                    }
                }

                GameState.RESULT -> {
                    ResultScreen(
                        currentPlayer = currentPlayer.copy(streakDays = playerStreak),
                        playerScore = playerScore,
                        playerHealth = playerHealth,
                        totalQuestions = questions.size,
                        onPlayAgain = {
                            gameState = GameState.WELCOME
                            selectedMode = null
                            questions = emptyList()
                            currentQuestionIndex = 0
                            playerScore = 0
                            playerHealth = 100
                            mapProgress = 0
                        },
                        onClaimReward = {
                            showRewardDialog = true
                        },
                        gameStats = gameStats,
                        gameMode = selectedMode ?: GameMode.QUIZ,
                        isDailyStreak = isDailyStreak.value
                    )
                }
            }

            if (showRewardDialog) {
                RewardTreasureDialog(
                    reward = when {
                        playerScore >= questions.size * 0.9 -> "100 XP and a Diamond Badge"
                        playerScore >= questions.size * 0.7 -> "50 XP and a Golden Badge"
                        playerScore >= questions.size * 0.5 -> "25 XP and a Silver Badge"
                        else -> "10 XP Consolation Reward"
                    },
                    isWinner = playerScore >= questions.size * 0.5,
                    onDismiss = { showRewardDialog = false },
                    streakDays = if (selectedMode == GameMode.DAILY_CHALLENGE) playerStreak else 0
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