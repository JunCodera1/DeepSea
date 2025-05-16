package com.example.deepsea.ui.screens.feature.game

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.deepsea.R
import com.example.deepsea.ui.components.StatItem

@Composable
fun ResultScreen(
    currentPlayer: Player,
    playerScore: Int,
    playerHealth: Int,
    totalQuestions: Int,
    onPlayAgain: () -> Unit,
    onClaimReward: () -> Unit,
    gameStats: GameStats,
    gameMode: GameMode,
    isDailyStreak: Boolean
) {
    val primaryColor = Color(0xFF0078D7)
    val accentColor = Color(0xFFFF9500)

    val isTreasureFound = playerScore >= totalQuestions * 0.7 && playerHealth > 0

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

        // Kết quả
        if (isTreasureFound) {
            Text(
                text = "TREASURE FOUND!",
                fontSize = 32.sp * resultScale,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFFD700)
            )
            Image(
                painter = painterResource(id = R.drawable.ic_treasure),
                contentDescription = null,
                modifier = Modifier.size(100.dp)
            )
        } else {
            Text(
                text = "THE TREASURE SLIPPED AWAY!",
                fontSize = 24.sp * resultScale,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF666666)
            )
            Text(
                text = "Keep exploring to find it next time!",
                fontSize = 16.sp,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        // Thống kê
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "ADVENTURE STATS",
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
                        value = gameStats.correctAnswers.toString(),
                        color = primaryColor
                    )
                    StatItem(
                        label = "Incorrect",
                        value = (totalQuestions - gameStats.correctAnswers).toString(),
                        color = Color.Gray
                    )
                    StatItem(
                        label = "Accuracy",
                        value = "${(gameStats.correctAnswers.toFloat() / totalQuestions * 100).toInt()}%",
                        color = accentColor
                    )
                    StatItem(
                        label = "Time Spent",
                        value = "${gameStats.timeSpent}s",
                        color = Color.DarkGray
                    )
                    StatItem(
                        label = "Streak",
                        value = gameStats.streak.toString(),
                        color = if (isDailyStreak) accentColor else primaryColor
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Thông tin phần thưởng
        if (isTreasureFound) {
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
                            text = "You found a treasure!",
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (gameMode == GameMode.DAILY_CHALLENGE) "Streak: ${currentPlayer.streakDays} days" else "Claim your XP and badge",
                            color = Color.Gray
                        )
                    }
                }
            }
        } else {
            Text(
                text = "You gained some experience. Try again!",
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Nút
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
                    text = "NEW ADVENTURE",
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
                    text = if (isTreasureFound) "CLAIM TREASURE" else "CONTINUE",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}