package com.example.deepsea.ui.screens.feature.learn

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.deepsea.R
import com.example.deepsea.ui.viewmodel.learn.LearningViewModel
import com.example.deepsea.ui.viewmodel.learn.VocabularyItem
import kotlinx.coroutines.delay

@Composable
fun QuizImageScreen(
    lessonId: Long,
    onBack: () -> Unit,
    onComplete: () -> Unit
) {
    // Create ViewModel using Factory without Hilt
    val learningViewModel: LearningViewModel = viewModel(
        factory = LearningViewModel.Factory(lessonId)
    )

    val currentWord by learningViewModel.currentWord.collectAsState()
    val options by learningViewModel.options.collectAsState()
    val selectedOption = remember { mutableStateOf<String?>(null) }
    val heartCount by learningViewModel.hearts.collectAsState()
    val progressPercent by learningViewModel.progress.collectAsState()

    val isAnswerCorrect by learningViewModel.isAnswerCorrect.collectAsState()
    // Check for game completion
    LaunchedEffect(isAnswerCorrect) {
        if (isAnswerCorrect == true) {
            delay(1000)
            learningViewModel.loadNextWord()
            selectedOption.value = null
            learningViewModel.resetAnswerState()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Top Bar
        TopBar(
            heartCount = heartCount,
            progressPercent = progressPercent,
            onBackClick = onBack
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Word to Learn
        WordToLearnSection(currentWord = currentWord)

        Spacer(modifier = Modifier.height(32.dp))

        // Options Grid
        OptionsGrid(
            options = options,
            selectedOption = selectedOption.value,
            onOptionSelected = { option ->
                selectedOption.value = option
            }
        )

        Spacer(modifier = Modifier.weight(1f))

        // Check Button
        CheckButton(
            isEnabled = selectedOption.value != null,
            onClick = {
                onComplete
                selectedOption.value?.let {
                    learningViewModel.checkAnswer(it)
                }
            }
        )
    }
}

@Composable
fun TopBar(
    heartCount: Int,
    progressPercent: Float,
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Go Back",
                tint = Color.Gray
            )
        }

        LinearProgressIndicator(
            progress = progressPercent,
            modifier = Modifier
                .weight(1f)
                .height(12.dp)
                .clip(RoundedCornerShape(8.dp)),
            color = Color(0xFF58CC02),
            trackColor = Color.LightGray
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 16.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_heart),
                contentDescription = "Hearts",
                tint = Color.Red,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = heartCount.toString(),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.Red,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}

@Composable
fun WordToLearnSection(currentWord: VocabularyItem) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Audio Button
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(Color(0xFF1CB0F6))
                .clickable { /* Play audio functionality would go here */ }
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_audio),
                contentDescription = "Play Audio",
                tint = Color.White,
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Word in Romaji
        Text(
            text = currentWord.romaji,
            fontSize = 16.sp,
            color = Color.Gray
        )

        // Word in Japanese
        Text(
            text = currentWord.native,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun OptionsGrid(
    options: List<VocabularyItem>,
    selectedOption: String?,
    onOptionSelected: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OptionItem(
                item = options.getOrNull(0),
                isSelected = selectedOption == options.getOrNull(0)?.english,
                onOptionSelected = onOptionSelected,
                modifier = Modifier.weight(1f)
            )

            OptionItem(
                item = options.getOrNull(1),
                isSelected = selectedOption == options.getOrNull(1)?.english,
                onOptionSelected = onOptionSelected,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OptionItem(
                item = options.getOrNull(2),
                isSelected = selectedOption == options.getOrNull(2)?.english,
                onOptionSelected = onOptionSelected,
                modifier = Modifier.weight(1f)
            )

            OptionItem(
                item = options.getOrNull(3),
                isSelected = selectedOption == options.getOrNull(3)?.english,
                onOptionSelected = onOptionSelected,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun OptionItem(
    item: VocabularyItem?,
    isSelected: Boolean,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (item == null) return

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp))
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) Color(0xFF58CC02) else Color.LightGray,
                shape = RoundedCornerShape(8.dp)
            )
            .background(if (isSelected) Color(0x1A58CC02) else Color.White)
            .clickable { onOptionSelected(item.english) }
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = item.imageResId),
                contentDescription = item.english,
                modifier = Modifier
                    .size(80.dp)
                    .padding(bottom = 8.dp),
                contentScale = ContentScale.Fit
            )

            Text(
                text = item.english,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.DarkGray
            )
        }
    }
}

@Composable
fun CheckButton(
    isEnabled: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = isEnabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isEnabled) Color(0xFF58CC02) else Color.LightGray
        )
    ) {
        Text(
            text = "CHECK",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}