package com.example.deepsea.ui.screens.feature.game

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.deepsea.ui.components.StatItem
import com.example.deepsea.ui.viewmodel.game.GameViewModel

@Composable
fun ResultScreen(
    currentPlayer: Player,
    opponent: Player,
    playerScore: Int,
    opponentScore: Int,
    totalQuestions: Int,
    onPlayAgain: () -> Unit,
    onClaimReward: () -> Unit
){
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