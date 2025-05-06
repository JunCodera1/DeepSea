package com.example.deepsea.ui.screens.feature.learn

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.deepsea.data.model.audio.TranslationExercise
import com.example.deepsea.ui.viewmodel.learn.WordBuildingViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun WordBuildingScreen(
    viewModel: WordBuildingViewModel = viewModel(),
    onNavigateToSettings: () -> Unit = {}
) {
    val exercise by viewModel.currentExercise.collectAsState()
    val userProgress by viewModel.userProgress.collectAsState()
    val hearts by viewModel.hearts.collectAsState()
    val isAudioPlaying by viewModel.isAudioPlaying.collectAsState()
    val selectedWords by viewModel.selectedWords.collectAsState()
    val isAnswerCorrect by viewModel.isAnswerCorrect.collectAsState()

    var showFeedback by remember { mutableStateOf(false) }

    // Show feedback when answer is checked
    LaunchedEffect(isAnswerCorrect) {
        if (isAnswerCorrect != null) {
            showFeedback = true
            delay(1500)
            showFeedback = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Top Bar with Settings and Progress
        TopBarWordBuilding(
            progress = userProgress,
            hearts = hearts,
            onSettingsClick = onNavigateToSettings
        )

        // Main Instruction
        Text(
            text = "Translate this sentence",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        // Source text to translate
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.LightGray.copy(alpha = 0.3f)
            )
        ) {
            Text(
                text = exercise.sourceText,
                fontSize = 20.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                textAlign = TextAlign.Center
            )
        }

        // Audio Controls
        AudioControls(
            isAudioPlaying = isAudioPlaying,
            onPlayNormal = { viewModel.playSentenceAudio() },
            onPlaySlow = { viewModel.playSlowSentenceAudio() }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Selected words area
        SelectedWordsArea(
            selectedWords = selectedWords,
            onWordRemove = { index -> viewModel.removeWord(index) },
            onClearAll = { viewModel.clearSelection() }
        )

        // Feedback animation when answer is checked
        AnimatedVisibility(
            visible = showFeedback,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isAnswerCorrect == true) {
                    FeedbackWordBuildingMessage(
                        message = "Great job!",
                        icon = Icons.Default.Check,
                        color = Color.Green
                    )
                } else if (isAnswerCorrect == false) {
                    FeedbackWordBuildingMessage(
                        message = "Try again!",
                        icon = Icons.Default.Close,
                        color = Color.Red
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Word options
        if (!showFeedback) {
            WordOptions(
                words = exercise.wordOptions,
                selectedWords = selectedWords,
                onWordClick = { word -> viewModel.addWord(word) }
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Check Button
        Button(
            onClick = { viewModel.checkAnswer() },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (selectedWords.isNotEmpty()) MaterialTheme.colorScheme.primary else Color.LightGray,
                contentColor = if (selectedWords.isNotEmpty()) Color.White else Color.Gray
            ),
            enabled = selectedWords.isNotEmpty()
        ) {
            Text(
                text = "CHECK",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun TopBarWordBuilding(
    progress: Float,
    hearts: Int,
    onSettingsClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Settings Icon
        IconButton(
            onClick = onSettingsClick,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.LightGray.copy(alpha = 0.3f))
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings",
                tint = Color.DarkGray
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
                    .fillMaxWidth(progress)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFFF9800)) // Orange progress
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
}

@Composable
fun AudioControls(
    isAudioPlaying: Boolean,
    onPlayNormal: () -> Unit,
    onPlaySlow: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        // Regular Speed Audio Button
        AudioButton(
            size = 64.dp,
            isPlaying = isAudioPlaying,
            icon = "🔊",
            iconSize = 24.sp,
            onClick = onPlayNormal
        )

        Spacer(modifier = Modifier.width(16.dp))

        // Slow Audio Button
        AudioButton(
            size = 64.dp,
            isPlaying = isAudioPlaying,
            icon = "🐢",
            iconSize = 24.sp,
            onClick = onPlaySlow
        )
    }
}

@Composable
fun AudioWorldBuildingButton(
    size: androidx.compose.ui.unit.Dp,
    isPlaying: Boolean,
    icon: String,
    iconSize: androidx.compose.ui.unit.TextUnit,
    onClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "audio_animation")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isPlaying) 1.2f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(
        modifier = Modifier
            .size(size)
            .scale(if (isPlaying) scale else 1f)
            .clip(CircleShape)
            .background(Color(0xFF2196F3))
            .clickable(enabled = !isPlaying) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = icon,
            fontSize = iconSize,
            color = Color.White
        )

        if (isPlaying) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color.Black.copy(alpha = 0.1f))
            )
        }
    }
}

@Composable
fun SelectedWordsArea(
    selectedWords: List<String>,
    onWordRemove: (Int) -> Unit,
    onClearAll: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(vertical = 8.dp)
            .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
    ) {
        if (selectedWords.isEmpty()) {
            // Empty state
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Tap words to build your answer",
                    color = Color.Gray,
                    fontSize = 16.sp
                )
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Clear button
                Box(
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(4.dp)
                ) {
                    IconButton(
                        onClick = onClearAll,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Clear all",
                            tint = Color.Gray
                        )
                    }
                }

                // Selected words
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    itemsIndexed(selectedWords) { index, word ->
                        SelectedWordChip(
                            text = word,
                            onRemove = { onWordRemove(index) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SelectedWordChip(
    text: String,
    onRemove: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clickable { onRemove() },
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, Color(0xFF2196F3)),
        color = Color(0xFF2196F3).copy(alpha = 0.2f),
        tonalElevation = 0.dp
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            color = Color(0xFF2196F3),
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 6.dp)
                .wrapContentWidth(unbounded = true) // đảm bảo không bị bó
        )
    }
}


@Composable
fun WordOptions(
    words: List<String>,
    selectedWords: List<String>,
    onWordClick: (String) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        items(words) { word ->
            val isUsed = word in selectedWords
            WordOption(
                text = word,
                isUsed = isUsed,
                onClick = { if (!isUsed) onWordClick(word) }
            )
        }
    }
}

@Composable
fun WordOption(
    text: String,
    isUsed: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(if (isUsed) Color.LightGray.copy(alpha = 0.5f) else Color.White)
            .border(1.dp, if (isUsed) Color.Gray else Color(0xFF2196F3), RoundedCornerShape(24.dp))
            .clickable(enabled = !isUsed) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            color = if (isUsed) Color.Gray else Color(0xFF2196F3),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun FeedbackWordBuildingMessage(
    message: String,
    icon: ImageVector,
    color: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(32.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = message,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Preview(showBackground = true)
@Composable
fun WordBuildingScreenPreview() {
    // Create a mock TranslationExercise
    val mockExercise = TranslationExercise(
        id = "1",
        sourceText = "That's green tea",
        targetText = "それはお茶です。",
        sourceLanguage = "en",
        targetLanguage = "ja",
        wordOptions = listOf("それは", "お", "茶", "です", "。", "緑", "飲み物", "コーヒー")
    )

    // Mock ViewModel for preview
    val mockViewModel = object {
        val currentExercise = MutableStateFlow(mockExercise)
        val userProgress = MutableStateFlow(0.3f)
        val hearts = MutableStateFlow(2)
        val isAudioPlaying = MutableStateFlow(false)
        val selectedWords = MutableStateFlow(listOf("それは", "お"))
        val isAnswerCorrect = MutableStateFlow<Boolean?>(null)

        fun playSentenceAudio() {}
        fun playSlowSentenceAudio() {}
        fun addWord(word: String) {}
        fun removeWord(index: Int) {}
        fun clearSelection() {}
        fun checkAnswer() {}
    }

    MaterialTheme {
        Surface {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Bar Preview
                TopBar(progress = 0.3f, hearts = 2, onSettingsClick = {})

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Translate this sentence",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.LightGray.copy(alpha = 0.3f)
                    )
                ) {
                    Text(
                        text = "That's green tea",
                        fontSize = 20.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }

                AudioControls(
                    isAudioPlaying = false,
                    onPlayNormal = {},
                    onPlaySlow = {}
                )

                SelectedWordsArea(
                    selectedWords = listOf("それは", "お"),
                    onWordRemove = {},
                    onClearAll = {}
                )

                Spacer(modifier = Modifier.height(16.dp))

                WordOptions(
                    words = listOf("それは", "お", "茶", "です", "。", "緑", "飲み物", "コーヒー"),
                    selectedWords = listOf("それは", "お"),
                    onWordClick = {}
                )

                Spacer(modifier = Modifier.height(16.dp))

                FeedbackMessage(
                    message = "Great job!",
                    icon = Icons.Default.Check,
                    color = Color.Green
                )
            }
        }
    }
}