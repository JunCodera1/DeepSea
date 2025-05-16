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

    // Danh sách câu hỏi mở rộng với nhiều ngôn ngữ và độ khó khác nhau
    val allQuestions = listOf(
        // QUIZ - JAPANESE
        Question(
            id = 1,
            text = "What is the Japanese word for 'house'?",
            options = listOf("犬", "家", "車", "木"),
            correctAnswer = 1,
            gameMode = "QUIZ",
            language = "JAPANESE",
            explanation = "'家' (ie) means 'house'. '犬' (inu) is 'dog', '車' (kuruma) is 'car', '木' (ki) is 'tree'.",
            difficulty = DifficultyLevel.EASY
        ),
        Question(
            id = 2,
            text = "Which sentence is correct in Japanese?",
            options = listOf("私は行きます学校。", "私は学校に行きます。", "私は行きます学校に。", "私は学校に行く。"),
            correctAnswer = 1,
            gameMode = "QUIZ",
            language = "JAPANESE",
            explanation = "'私は学校に行きます。' (Watashi wa gakkou ni ikimasu.) is correct, meaning 'I go to school.' The particle 'に' and verb order are proper.",
            difficulty = DifficultyLevel.EASY
        ),
        Question(
            id = 3,
            text = "What is the past tense of 'to eat' (食べる) in Japanese?",
            options = listOf("食べました", "食べます", "食べられ", "食べている"),
            correctAnswer = 0,
            gameMode = "QUIZ",
            language = "JAPANESE",
            explanation = "'食べました' (tabemashita) is the polite past tense of 'to eat'. '食べます' is present/future, '食べられ' is passive, '食べている' is present continuous.",
            difficulty = DifficultyLevel.MEDIUM
        ),
        Question(
            id = 4,
            text = "What is the Japanese word for 'thank you'?",
            options = listOf("お願い", "ありがとう", "すみません", "おはよう"),
            correctAnswer = 1,
            gameMode = "QUIZ",
            language = "JAPANESE",
            explanation = "'ありがとう' (arigatou) means 'thank you'. 'お願い' (onegai) is 'please', 'すみません' (sumimasen) is 'excuse me', 'おはよう' (ohayou) is 'good morning'.",
            difficulty = DifficultyLevel.EASY
        ),

        // SCRAMBLE
        Question(
            id = 5,
            text = "Unscramble the letters to form a Japanese word for 'book': B-U-K-K-O",
            options = listOf("Bukko", "Kobbu", "Honbu", "Hon"),
            correctAnswer = 3,
            gameMode = "SCRAMBLE",
            language = "JAPANESE",
            explanation = "'本' (hon) is the Japanese word for 'book', formed by unscrambling B-U-K-K-O (simplified as 'hon' in kana).",
            difficulty = DifficultyLevel.EASY
        ),
        Question(
            id = 6,
            text = "Unscramble T-O-M-O-D-A-C-H-I to form a Japanese word for 'friend':",
            options = listOf("Tomodachi", "Chatomi", "Dachito", "Moticha"),
            correctAnswer = 0,
            gameMode = "SCRAMBLE",
            language = "JAPANESE",
            explanation = "'友達' (tomodachi) is the Japanese word for 'friend'.",
            difficulty = DifficultyLevel.EASY
        ),
        Question(
            id = 7,
            text = "Unscramble S-A-K-U-R-A to form a Japanese word:",
            options = listOf("Sakura", "Rusaka", "Kusara", "Arasuk"),
            correctAnswer = 0,
            gameMode = "SCRAMBLE",
            language = "JAPANESE",
            explanation = "'桜' (sakura) means 'cherry blossom' in Japanese.",
            difficulty = DifficultyLevel.MEDIUM
        ),

        // MATCH
        Question(
            id = 8,
            text = "Match the English word to its Japanese meaning: 'Dog'",
            options = listOf("猫", "犬", "鳥", "魚"),
            correctAnswer = 1,
            gameMode = "MATCH",
            language = "JAPANESE",
            explanation = "'犬' (inu) means 'dog' in Japanese. '猫' (neko) is 'cat', '鳥' (tori) is 'bird', '魚' (sakana) is 'fish'.",
            difficulty = DifficultyLevel.EASY
        ),
        Question(
            id = 9,
            text = "Match the English word to its Japanese meaning: 'Cat'",
            options = listOf("犬", "猫", "鳥", "魚"),
            correctAnswer = 1,
            gameMode = "MATCH",
            language = "JAPANESE",
            explanation = "'猫' (neko) means 'cat' in Japanese. '犬' (inu) is 'dog', '鳥' (tori) is 'bird', '魚' (sakana) is 'fish'.",
            difficulty = DifficultyLevel.EASY
        ),
        Question(
            id = 10,
            text = "Match the English word to its Japanese meaning: 'Water'",
            options = listOf("水", "雲", "火", "風"),
            correctAnswer = 0,
            gameMode = "MATCH",
            language = "JAPANESE",
            explanation = "'水' (mizu) means 'water' in Japanese. '雲' (kumo) is 'cloud', '火' (hi) is 'fire', '風' (kaze) is 'wind'.",
            difficulty = DifficultyLevel.MEDIUM
        ),

        // DAILY CHALLENGE - Mix of modes
        Question(
            id = 11,
            text = "Today's challenge: What is the Japanese word for 'goodbye'?",
            options = listOf("こんにちは", "さようなら", "ありがとう", "おやすみ"),
            correctAnswer = 1,
            gameMode = "DAILY_CHALLENGE",
            language = "JAPANESE",
            explanation = "'さようなら' (sayounara) means 'goodbye' in Japanese.",
            difficulty = DifficultyLevel.EASY
        ),
        Question(
            id = 12,
            text = "Today's challenge: Unscramble N-I-H-O-N to form a Japanese word for 'Japan':",
            options = listOf("Nihon", "Honni", "Inhon", "Nonhi"),
            correctAnswer = 0,
            gameMode = "DAILY_CHALLENGE",
            language = "JAPANESE",
            explanation = "'日本' (nihon) is the Japanese word for 'Japan'.",
            difficulty = DifficultyLevel.MEDIUM
        )
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