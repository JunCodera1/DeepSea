package com.example.deepsea.ui.screens.feature.learn

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay

@Composable
fun MatchingPairsScreen(
    viewModel: MatchingPairsViewModel = viewModel(),
    onNavigateToSettings: () -> Unit = {}
) {
    val englishWords by viewModel.englishWords.collectAsState()
    val japaneseWords by viewModel.japaneseWords.collectAsState()
    val isAudioPlaying by viewModel.isAudioPlaying.collectAsState()
    val progress by viewModel.progress.collectAsState()
    val hearts by viewModel.hearts.collectAsState()
    val showFeedback by viewModel.showFeedback.collectAsState()
    val isCorrectMatch by viewModel.isCorrectMatch.collectAsState()
    val correctAnswerForFeedback by viewModel.correctAnswerForFeedback.collectAsState()

    // Simulate audio completion after a delay
    LaunchedEffect(isAudioPlaying) {
        if (isAudioPlaying) {
            delay(1000) // Simulate audio duration
            viewModel.setAudioPlayingState(false)
        }
    }

    // Auto-dismiss feedback after a delay
    LaunchedEffect(showFeedback) {
        if (showFeedback) {
            delay(1500)
            viewModel.dismissFeedback()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Top Bar with Settings and Progress
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Settings Icon
                IconButton(
                    onClick = onNavigateToSettings,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray.copy(alpha = 0.3f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = Color.Gray
                    )
                }

                // Progress Bar
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp)
                        .height(24.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.LightGray)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(progress) // Use fraction instead of fixed width
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF76C043)) // Green color
                    )
                }

                // Hearts
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "❤️",
                        fontSize = 24.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = hearts.toString(),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Red
                    )
                }
            }

            // Title
            Text(
                text = "Tap the matching pairs",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 24.dp),
                color = Color.DarkGray
            )

            // Word Matching Grid - English side (left column)
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(englishWords) { pair ->
                    WordCard(
                        text = pair.english,
                        isSelected = pair.isSelected,
                        isMatched = pair.isMatched,
                        onClick = {
                            viewModel.selectWord(true, pair)
                        }
                    )
                }
                items(japaneseWords) { pair ->
                    WordCard(
                        text = pair.japanese,
                        pronunciation = pair.pronunciation,
                        isSelected = pair.isSelected,
                        isMatched = pair.isMatched,
                        onClick = {
                            viewModel.selectWord(false, pair)
                        }
                    )
                }
            }

            // Continue button (showing when all pairs are matched)
            if (viewModel.isGameCompleted()) {
                Button(
                    onClick = { viewModel.resetGame() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(top = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF76C043)
                    ),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Text(
                        text = "CONTINUE",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                // Check button (visible only during gameplay)
                Button(
                    onClick = { /* Check functionality */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(top = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.LightGray
                    ),
                    shape = RoundedCornerShape(28.dp),
                    enabled = false
                ) {
                    Text(
                        text = "CHECK",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        // Overlay feedback when a match is attempted
        AnimatedVisibility(
            visible = showFeedback,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            if (isCorrectMatch) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF76C043))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Correct",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Good job!",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFF5252))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Incorrect",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Try again!",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WordCard(
    text: String,
    pronunciation: String = "",
    isSelected: Boolean,
    isMatched: Boolean,
    onClick: () -> Unit
) {
    val cardAlpha = when {
        isMatched -> 0.5f  // Faded appearance for matched cards
        else -> 1f
    }

    val backgroundColor = when {
        isMatched -> Color.White
        isSelected -> Color.White
        else -> Color.White
    }

    val borderColor = when {
        isMatched -> Color.LightGray.copy(alpha = 0.5f)
        isSelected -> Color(0xFF2196F3)
        else -> Color.LightGray
    }

    val textColor = when {
        isMatched -> Color.LightGray
        else -> Color.DarkGray
    }

    val pronunciationColor = when {
        isMatched -> Color.LightGray
        else -> Color.Gray
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable(enabled = !isMatched) { onClick() }
            .alpha(cardAlpha),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = text,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = textColor,
                textAlign = TextAlign.Center
            )

            if (pronunciation.isNotEmpty()) {
                Text(
                    text = pronunciation,
                    fontSize = 14.sp,
                    color = pronunciationColor,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MatchingPairsScreenPreview() {
    MaterialTheme {
        Surface {
            MatchingPairsScreen()
        }
    }
}