package com.example.deepsea.ui.screens.feature.game

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.deepsea.ui.components.PlayerScoreCard
import kotlinx.coroutines.delay

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