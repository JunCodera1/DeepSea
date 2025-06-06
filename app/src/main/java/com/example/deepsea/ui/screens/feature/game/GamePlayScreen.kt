package com.example.deepsea.ui.screens.feature.game

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.deepsea.R
import kotlinx.coroutines.delay

@Composable
fun GamePlayScreen(
    currentPlayer: Player,
    question: Question,
    playerScore: Int,
    playerHealth: Int,
    mapProgress: Int,
    currentQuestionIndex: Int,
    totalQuestions: Int,
    onAnswerSelected: (Int, Int) -> Unit, // Thêm timeUsed
    gameMode: GameMode,
    difficulty: DifficultyLevel
) {
    val primaryColor = Color(0xFF0078D7)
    val accentColor = Color(0xFFFF9500)

    var timeRemaining by remember { mutableStateOf(15) }
    var selectedAnswerIndex by remember { mutableStateOf<Int?>(null) }
    var isAnswerCorrect by remember { mutableStateOf<Boolean?>(null) }
    var isAnswerSubmitted by remember { mutableStateOf(false) }
    var shouldProceed by remember { mutableStateOf(false) }

    // Hiệu ứng phóng to khi trả lời
    val answerScale by animateFloatAsState(
        targetValue = if (isAnswerSubmitted) 1.1f else 1f,
        label = "answerScale"
    )

    // Timer effect
    LaunchedEffect(key1 = currentQuestionIndex) {
        selectedAnswerIndex = null
        isAnswerSubmitted = false
        isAnswerCorrect = null
        timeRemaining = 15

        while (timeRemaining > 0 && !isAnswerSubmitted) {
            delay(1000)
            timeRemaining--
        }

        if (!isAnswerSubmitted) {
            isAnswerSubmitted = true
            isAnswerCorrect = false
            shouldProceed = true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Game header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Q${currentQuestionIndex + 1}/$totalQuestions - ${difficulty.name}",
                fontWeight = FontWeight.Medium
            )

            Text(
                text = "Time: $timeRemaining",
                fontWeight = FontWeight.Medium,
                color = if (timeRemaining <= 5) Color.Red else Color.Black
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Thanh tiến trình bản đồ
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(Color(0xFFE1E8ED))
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_treasure),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(32.dp),
                tint = accentColor
            )
            Icon(
                painter = painterResource(id = R.drawable.ic_player),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .offset(x = (mapProgress * 3).dp)
                    .size(32.dp),
                tint = primaryColor
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Thanh máu
        LinearProgressIndicator(
            progress = playerHealth / 100f,
            modifier = Modifier.fillMaxWidth(),
            color = Color.Green,
            trackColor = Color.Red
        )
        Text(
            text = "Health: $playerHealth",
            fontWeight = FontWeight.Medium,
            color = if (playerHealth <= 30) Color.Red else Color.Black
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Câu hỏi
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
                    text = when (gameMode) {
                        GameMode.QUIZ -> question.text
                        GameMode.SCRAMBLE -> "${question.text} (Unscramble to find the word)"
                        GameMode.MATCH -> "${question.text} (Match the word to its meaning)"
                        GameMode.DAILY_CHALLENGE -> "Challenge: ${question.text}"
                        else -> question.text
                    },
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
                if (question.imageResource != null) {
                    Image(
                        painter = painterResource(id = question.imageResource),
                        contentDescription = null,
                        modifier = Modifier
                            .size(200.dp)
                            .padding(top = 8.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Các lựa chọn trả lời
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
                    }
                    .scale(answerScale),
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

        // Giải thích nếu trả lời sai
        if (isAnswerSubmitted && !isAnswerCorrect!!) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFAE6))
            ) {
                Text(
                    text = question.explanation,
                    modifier = Modifier.padding(16.dp),
                    color = Color.DarkGray,
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Nút submit
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
                text = if (!isAnswerSubmitted) "SUBMIT ANSWER"
                else if (isAnswerCorrect == true) "CORRECT!"
                else "INCORRECT!",
                fontWeight = FontWeight.Bold
            )
        }

        if (shouldProceed) {
            LaunchedEffect(Unit) {
                delay(2000) // Chờ 2 giây để xem giải thích
                onAnswerSelected(selectedAnswerIndex ?: -1, 15 - timeRemaining)
                shouldProceed = false
            }
        }
    }
}