package com.example.deepsea.ui.screens.feature

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.deepsea.R
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

@Composable
fun GameModeCard(
    title: String,
    description: String,
    iconContent: @Composable () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF0F7FF))
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                iconContent()
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = description,
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }

            Icon(
                imageVector = Icons.Filled.ArrowForward,
                contentDescription = null,
                tint = Color.Gray
            )
        }
    }
}

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

@Composable
fun GamePlayScreen(
    currentPlayer: Player,
    opponent: Player,
    question: Question,
    playerScore: Int,
    opponentScore: Int,
    currentQuestionIndex: Int,
    totalQuestions: Int,
    onAnswerSelected: (Boolean) -> Unit
) {
    val primaryColor = Color(0xFF0078D7)
    val accentColor = Color(0xFFFF9500)

    var timeRemaining by remember { mutableStateOf(15) }
    var selectedAnswerIndex by remember { mutableStateOf<Int?>(null) }
    var isAnswerCorrect by remember { mutableStateOf(false) }
    var isAnswerSubmitted by remember { mutableStateOf(false) }

    // Timer effect
    LaunchedEffect(key1 = currentQuestionIndex) {
        selectedAnswerIndex = null
        isAnswerSubmitted = false
        timeRemaining = 15

        while (timeRemaining > 0 && !isAnswerSubmitted) {
            delay(1000)
            timeRemaining--
        }

        if (!isAnswerSubmitted) {
            onAnswerSelected(false)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Game header with progress
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Question ${currentQuestionIndex + 1}/$totalQuestions",
                fontWeight = FontWeight.Medium
            )

            Text(
                text = "Time: $timeRemaining",
                fontWeight = FontWeight.Medium,
                color = if (timeRemaining <= 5) Color.Red else Color.Black
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        LinearProgressIndicator(
            progress = (currentQuestionIndex.toFloat() + 0.5f) / totalQuestions,
            modifier = Modifier.fillMaxWidth(),
            color = primaryColor,
            trackColor = Color(0xFFE1E8ED)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Players scores
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            PlayerScoreCard(
                player = currentPlayer,
                score = playerScore,
                isCurrentPlayer = true
            )

            Text(
                text = "VS",
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .align(Alignment.CenterVertically),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.Gray
            )

            PlayerScoreCard(
                player = opponent,
                score = opponentScore,
                isCurrentPlayer = false
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Question
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = question.text,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Answer options
        question.options.forEachIndexed { index, option ->
            val isSelected = selectedAnswerIndex == index
            val borderColor = when {
                !isAnswerSubmitted -> if (isSelected) primaryColor else Color.LightGray
                isAnswerSubmitted && index == question.correctAnswer -> Color.Green
                isAnswerSubmitted && isSelected && index != question.correctAnswer -> Color.Red
                else -> Color.LightGray
            }

            val backgroundColor = when {
                !isAnswerSubmitted -> if (isSelected) Color(0xFFE1F0FF) else Color.White
                isAnswerSubmitted && index == question.correctAnswer -> Color(0xFFE1FFE1)
                isAnswerSubmitted && isSelected && index != question.correctAnswer -> Color(0xFFFFE1E1)
                else -> Color.White
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clickable(enabled = !isAnswerSubmitted) {
                        if (!isAnswerSubmitted) {
                            selectedAnswerIndex = index
                        }
                    },
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = backgroundColor),
                border = BorderStroke(1.dp, borderColor)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${('A' + index)}.",
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) primaryColor else Color.Gray
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Text(
                        text = option,
                        modifier = Modifier.weight(1f)
                    )

                    if (isAnswerSubmitted && index == question.correctAnswer) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = Color.Green
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))
        var isAnswerCorrect by remember { mutableStateOf(false) }
        var isAnswerSubmitted by remember { mutableStateOf(false) }
        var shouldProceed by remember { mutableStateOf(false) }
        // Submit button
        Button(
            onClick = {
                if (selectedAnswerIndex != null && !isAnswerSubmitted) {
                    isAnswerCorrect = selectedAnswerIndex == question.correctAnswer
                    isAnswerSubmitted = true
                    shouldProceed = true
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = selectedAnswerIndex != null && !isAnswerSubmitted,
            colors = ButtonDefaults.buttonColors(
                containerColor = primaryColor,
                disabledContainerColor = Color.LightGray
            )
        ) {
            Text(
                text = if (!isAnswerSubmitted) "SUBMIT ANSWER" else
                    if (isAnswerCorrect) "CORRECT!" else "INCORRECT!",
                fontWeight = FontWeight.Bold
            )
        }
        if (shouldProceed) {
            LaunchedEffect(Unit) {
                delay(1500)
                onAnswerSelected(isAnswerCorrect)
                shouldProceed = false // reset
            }
        }
    }
}

@Composable
fun PlayerScoreCard(
    player: Player,
    score: Int,
    isCurrentPlayer: Boolean
) {
    Card(
        modifier = Modifier.width(150.dp),
        shape = RoundedCornerShape(8.dp),
        border = if (isCurrentPlayer) BorderStroke(2.dp, Color(0xFF0078D7)) else null
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (isCurrentPlayer) Color(0xFF0078D7) else Color(0xFFE1E8ED)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = null,
                    tint = if (isCurrentPlayer) Color.White else Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = player.name,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp
            )

            Text(
                text = "Level ${player.level}",
                color = Color.Gray,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = score.toString(),
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = if (isCurrentPlayer) Color(0xFF0078D7) else Color.DarkGray
            )
        }
    }
}

@Composable
fun ResultScreen(
    currentPlayer: Player,
    opponent: Player,
    playerScore: Int,
    opponentScore: Int,
    totalQuestions: Int,
    onPlayAgain: () -> Unit,
    onClaimReward: () -> Unit
) {
    val primaryColor = Color(0xFF0078D7)
    val accentColor = Color(0xFFFF9500)

    val playerWins = playerScore > opponentScore
    val isDraw = playerScore == opponentScore

    // Animation for result
    val resultScale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(
            durationMillis = 500,
            easing = FastOutSlowInEasing
        ),
        label = "resultScale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        // Result text
        Text(
            text = when {
                playerWins -> "VICTORY!"
                isDraw -> "IT'S A DRAW!"
                else -> "GOOD EFFORT!"
            },
            fontSize = 32.sp * resultScale,
            fontWeight = FontWeight.Bold,
            color = when {
                playerWins -> Color(0xFFFFD700)
                isDraw -> Color(0xFF0078D7)
                else -> Color(0xFF666666)
            }
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Player scores comparison
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Your score
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "YOU",
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )

                Text(
                    text = playerScore.toString(),
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = primaryColor
                )
            }

            // VS divider
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "VS",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
            }

            // Opponent score
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = opponent.name.uppercase(),
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )

                Text(
                    text = opponentScore.toString(),
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.DarkGray
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Game stats
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "GAME STATS",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.DarkGray
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StatItem(
                        label = "Correct Answers",
                        value = playerScore.toString(),
                        color = primaryColor
                    )

                    StatItem(
                        label = "Incorrect",
                        value = (totalQuestions - playerScore).toString(),
                        color = Color.Gray
                    )

                    StatItem(
                        label = "Accuracy",
                        value = "${(playerScore.toFloat() / totalQuestions * 100).toInt()}%",
                        color = accentColor
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Reward info
        if (playerWins) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFAE6))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(32.dp)
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "You won a reward!",
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Claim your XP and special badge",
                            color = Color.Gray
                        )
                    }
                }
            }
        } else {
            Text(
                text = if (isDraw) "You'll get a small consolation prize" else "Better luck next time!",
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedButton(
                onClick = onPlayAgain,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                border = BorderStroke(1.dp, primaryColor)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Refresh,
                    contentDescription = null,
                    tint = primaryColor
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "PLAY AGAIN",
                    color = primaryColor
                )
            }

            Button(
                onClick = onClaimReward,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
            ) {
                Text(
                    text = if (playerWins) "CLAIM REWARD" else "CONTINUE",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun StatItem(
    label: String,
    value: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )

        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun RewardDialog(
    reward: String,
    isWinner: Boolean,
    onDismiss: () -> Unit
) {
    val primaryColor = Color(0xFF0078D7)
    val accentColor = Color(0xFFFF9500)

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Animate stars for winner
                if (isWinner) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .background(Color(0xFFFFF8E1), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        val infiniteTransition = rememberInfiniteTransition(label = "shine")
                        val rotation by infiniteTransition.animateFloat(
                            initialValue = 0f,
                            targetValue = 360f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(5000, easing = LinearEasing),
                                repeatMode = RepeatMode.Restart
                            ),
                            label = "rotation"
                        )

                        Icon(
                            painter = painterResource(R.drawable.ic_emoji_event),
                            contentDescription = null,
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(64.dp)
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .background(Color(0xFFF5F5F5), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = null,
                            tint = Color(0xFF9E9E9E),
                            modifier = Modifier.size(64.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = if (isWinner) "CONGRATULATIONS!" else "CONSOLATION PRIZE",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isWinner) accentColor else Color.DarkGray
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = buildAnnotatedString {
                        append("You've earned ")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(reward)
                        }
                        append("!")
                    },
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (isWinner) {
                    Text(
                        text = "Keep up the good work and continue your language learning journey!",
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AnimatedVisibility(
                            visible = true,
                            enter = slideInVertically() + expandVertically() + fadeIn(),
                            exit = fadeOut()
                        ) {
                            Text(
                                text = "Daily streak extended!",
                                color = accentColor,
                                fontStyle = FontStyle.Italic,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isWinner) accentColor else primaryColor
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("CONTINUE")
                }
            }
        }
    }
}

@Composable
fun LeaderboardScreen(onClose: () -> Unit) {
    val leaderboardItems = listOf(
        LeaderboardEntry("Player1", 950, 1),
        LeaderboardEntry("You", 820, 2),
        LeaderboardEntry("Player3", 780, 3),
        LeaderboardEntry("Player4", 720, 4),
        LeaderboardEntry("Player5", 650, 5)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Weekly Leaderboard",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            IconButton(onClick = onClose) {
                Icon(Icons.Rounded.Close, contentDescription = "Close")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Top 3 players
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            leaderboardItems.filter { it.rank <= 3 }.forEach { entry ->
                TopPlayerItem(entry)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Divider()

        Spacer(modifier = Modifier.height(16.dp))

        // Rest of the players
        LazyColumn {
            items(leaderboardItems.filter { it.rank > 3 }) { entry ->
                LeaderboardItemRow(entry)
                Divider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    thickness = 0.5.dp
                )
            }
        }
    }
}

@Composable
fun TopPlayerItem(entry: LeaderboardEntry) {
    val size = when (entry.rank) {
        1 -> 80.dp
        2 -> 70.dp
        else -> 60.dp
    }

    val color = when (entry.rank) {
        1 -> Color(0xFFFFD700) // Gold
        2 -> Color(0xFFC0C0C0) // Silver
        else -> Color(0xFFCD7F32) // Bronze
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "#${entry.rank}",
            fontWeight = FontWeight.Bold,
            color = color
        )

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .size(size)
                .border(2.dp, color, CircleShape)
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = entry.username.first().toString(),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = entry.username,
            fontWeight = FontWeight.Medium
        )

        Text(
            text = "${entry.score} XP",
            color = Color.Gray,
            fontSize = 14.sp
        )
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