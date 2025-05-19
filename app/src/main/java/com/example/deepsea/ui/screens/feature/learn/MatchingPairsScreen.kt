package com.example.deepsea.ui.screens.feature.learn

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.deepsea.data.model.exercise.WordPair
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import android.util.Log
import timber.log.Timber

@Composable
fun MatchingPairsScreen(
    viewModel: MatchingPairsViewModel, // SỬA: Xóa khởi tạo mặc định
    onNavigateToSettings: () -> Unit = {},
    onComplete: () -> Unit
) {
    val englishWords by viewModel.englishWords.collectAsState()
    val japaneseWords by viewModel.japaneseWords.collectAsState()
    val isAudioPlaying by viewModel.isAudioPlaying.collectAsState()
    val progress by viewModel.progress.collectAsState()
    val hearts by viewModel.hearts.collectAsState()
    val showFeedback by viewModel.showFeedback.collectAsState()
    val isCorrectMatch by viewModel.isCorrectMatch.collectAsState()
    val selectedEnglishWord by viewModel.selectedEnglishWord.collectAsState()
    val selectedJapaneseWord by viewModel.selectedJapaneseWord.collectAsState()

    // Thêm log để gỡ lỗi
    LaunchedEffect(progress, englishWords) {
        Timber.tag("MatchingPairsScreen")
            .d("Progress: $progress, isGameCompleted: ${viewModel.isGameCompleted()}")
    }

    // Simulate audio completion
    LaunchedEffect(isAudioPlaying) {
        if (isAudioPlaying) {
            delay(1000)
            viewModel.setAudioPlayingState(false)
        }
    }

    // Auto-dismiss feedback
    LaunchedEffect(showFeedback) {
        if (showFeedback) {
            delay(1500)
            viewModel.dismissFeedback()
        }
    }

    // Trigger onComplete when all pairs are matched
    LaunchedEffect(englishWords) {
        if (viewModel.isGameCompleted()) {
            delay(500)
            Log.d("MatchingPairsScreen", "Game completed, calling onComplete")
            onComplete()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
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
                            .fillMaxWidth(progress)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF76C043))
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "❤️", fontSize = 24.sp)
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

            // Parallel Columns for English and Japanese
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // English Words Column
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
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
                }

                // Japanese Words Column
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
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
            }

            // Check Button
            Button(
                onClick = { viewModel.checkMatch() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(top = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF76C043)
                ),
                shape = RoundedCornerShape(28.dp),
                enabled = selectedEnglishWord != null && selectedJapaneseWord != null
            ) {
                Text(
                    text = "CHECK",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        // Feedback Overlay
        AnimatedVisibility(
            visible = showFeedback,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (isCorrectMatch) Color(0xFF76C043) else Color(0xFFFF5252))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = if (isCorrectMatch) Icons.Default.Check else Icons.Default.Close,
                        contentDescription = if (isCorrectMatch) "Correct" else "Incorrect",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isCorrectMatch) "Good job!" else "Try again!",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun WordCard(
    text: String,
    pronunciation: String? = null,
    isSelected: Boolean,
    isMatched: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
            .alpha(if (isMatched) 0.5f else 1.0f)
            .clickable(onClick = onClick, enabled = !isMatched),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                isMatched -> Color.Green.copy(alpha = 0.3f)
                isSelected -> Color.Blue.copy(alpha = 0.3f)
                else -> MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
            pronunciation?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}



//@Preview(showBackground = true)
//@Composable
//fun MatchingPairsScreenPreview() {
//    MaterialTheme {
//        Surface {
//            MatchingPairsScreen(
//                viewModel = FakeMatchingPairsViewModel(),
//                onComplete = {}
//            )
//        }
//    }
//}