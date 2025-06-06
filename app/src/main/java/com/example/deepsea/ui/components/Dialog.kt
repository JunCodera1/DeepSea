package com.example.deepsea.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import com.example.deepsea.R
import com.example.deepsea.data.model.course.language.LanguageOption
import com.example.deepsea.data.model.daily.TaskCategory
import com.example.deepsea.text.PrimaryText
import com.example.deepsea.text.TitleText
import com.example.deepsea.ui.screens.feature.daily.Task
import com.example.deepsea.ui.screens.feature.daily.capitalize
import com.example.deepsea.ui.screens.path.LanguageOptionItem
import com.example.deepsea.ui.theme.FeatherGreen
import com.example.deepsea.ui.theme.Gray
import com.example.deepsea.ui.theme.Polar

@Composable
fun RewardTreasureDialog(
    reward: String,
    isWinner: Boolean,
    onDismiss: () -> Unit,
    streakDays: Int = 0
) {
    val primaryColor = Color(0xFF0078D7)
    val accentColor = Color(0xFFFF9500)

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Hình ảnh phần thưởng
                Icon(
                    painter = painterResource(id = if (isWinner) R.drawable.ic_treasure else R.drawable.ic_close),
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = if (isWinner) accentColor else Color.Gray
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Tiêu đề
                Text(
                    text = if (isWinner) "Treasure Unlocked!" else "Adventure Reward",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = primaryColor
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Mô tả phần thưởng
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "You earned: $reward",
                        fontSize = 16.sp,
                        color = Color.DarkGray,
                        textAlign = TextAlign.Center
                    )
                    if (streakDays > 0) {
                        Text(
                            text = "Streak: $streakDays days!",
                            fontSize = 14.sp,
                            color = accentColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Nút đóng
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
                ) {
                    Text(
                        text = "COLLECT",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun LanguageSelectionDialog(
    showDialog: Boolean,
    availableLanguages: List<LanguageOption>,
    currentLanguages: Set<LanguageOption>,
    onDismiss: () -> Unit,
    onLanguageSelected: (LanguageOption) -> Unit
) {
    if (showDialog) {
        Dialog(onDismissRequest = onDismiss) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Header with title and close button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Choose a language",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )

                        IconButton(onClick = onDismiss) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color.Gray
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Language list
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 350.dp)
                    ) {
                        items(availableLanguages.size) { index ->
                            val language = availableLanguages[index]
                            val isSelected = currentLanguages.contains(language)

                            LanguageOptionItem(
                                option = language,
                                iconResId = language.flagResId,
                                isSelected = isSelected,
                                onSelect = {
                                    if (!isSelected) {
                                        onLanguageSelected(language)
                                    }
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Add button
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF6750A4)
                        )
                    ) {
                        Text(
                            "Done",
                            fontSize = 16.sp,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Dialog that appears when a star is tapped
 * Provides feedback on level status and interactivity
 *
 * @param isDialogShown Controls dialog visibility
 * @param isDialogInteractive Controls whether dialog shows interactive or locked state
 * @param dialogTransition Vertical position of the dialog
 */
@Composable
fun StarDialog(
    isDialogShown: Boolean,
    isDialogInteractive: Boolean,
    dialogTransition: Float,
    navController: NavController,
    xpAmount: Int,
    unitId: Long,
    starIndex: Int,
    onDismiss: () -> Unit, // Add dismiss callback
    onStarComplete: () -> Unit = {}
) {
    // Instead of a Box with fillMaxSize, use Dialog to show a popup
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        // Animate dialog scaling
        val animatedScale by animateFloatAsState(
            targetValue = if (isDialogShown) 1f else 0f,
            label = "dialogScale"
        )

        // Check if this is an early lesson that should be skippable
        val isEarlyLesson = starIndex < 3

        // Determine the actual interactive status
        val canInteract = isDialogInteractive || isEarlyLesson

        // Dialog content
        Column(
            modifier = Modifier
                .graphicsLayer {
                    transformOrigin = TransformOrigin(0.5f, 0f)
                    scaleY = animatedScale
                    scaleX = animatedScale
                }
                .fillMaxWidth()
                .background(
                    color = when {
                        isDialogInteractive -> FeatherGreen
                        isEarlyLesson -> FeatherGreen.copy(alpha = 0.8f)
                        else -> Polar
                    },
                    shape = RoundedCornerShape(8.dp)
                )
                .border(
                    width = 1.dp,
                    color = Gray,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Dialog title - Dynamic based on lesson status
            Text(
                text = if (starIndex == 0) "Introduction" else "Lesson ${starIndex + 1}",
                color = if (canInteract) Color.White else Color.DarkGray.copy(alpha = 0.5f),
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold
            )

            // Dialog description - Different message for early lessons
            Text(
                text = when {
                    isDialogInteractive -> "Ready to start this lesson"
                    isEarlyLesson -> "Early lesson available for skipping ahead"
                    else -> "Complete all levels above to unlock this"
                },
                color = if (canInteract) Color.White else Color.DarkGray.copy(alpha = 0.3f)
            )

            // Voice Assistant Button
            Button(
                onClick = { navController.navigate("home/voice_assistant") },
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Filled.Call, contentDescription = "Voice")
                    Text("Voice Assistant")
                }
            }

            // XP Counter Animation when a star is completed
            if (canInteract) {
                var showXpAnimation by remember { mutableStateOf(false) }

                LaunchedEffect(isDialogShown) {
                    if (isDialogShown) {
                        // Reset animation state when dialog opens
                        showXpAnimation = false
                    }
                }

                AnimatedVisibility(
                    visible = showXpAnimation,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_star),
                            contentDescription = "XP",
                            modifier = Modifier.size(24.dp),
                            colorFilter = ColorFilter.tint(Color.Yellow)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "+$xpAmount XP",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Action buttons row - Skip Ahead or Regular Study options for early lessons
            if (isEarlyLesson && !isDialogInteractive) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Skip ahead button - Only for early lessons
                    Button(
                        onClick = {
                            // Call onStarComplete to update the star status
                            onStarComplete()

                            // Navigate to learning session with unitId
                            navController.navigate("learning_session/$unitId")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Skip Ahead +$xpAmount XP",
                            color = FeatherGreen,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Skip warning text
                    Text(
                        text = "You may miss important content by skipping ahead",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            } else {
                // Standard button - Navigate to learning session
                Button(
                    onClick = {
                        if (canInteract) {
                            // Call onStarComplete to update the star status
                            onStarComplete()

                            // Navigate to learning session with unitId
                            navController.navigate("learning_session/$unitId")
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (canInteract)
                            Color.White
                        else
                            Color.DarkGray.copy(alpha = 0.15f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    enabled = canInteract
                ) {
                    Text(
                        text = if (canInteract) "Start +$xpAmount XP" else "LOCKED",
                        color = if (canInteract) FeatherGreen else Color.DarkGray.copy(alpha = 0.5f),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Add close button
            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(top = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White.copy(alpha = 0.2f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Close", color = Color.White)
            }
        }
    }
}

@Composable
fun LockedStarDialog(
    isVisible: Boolean,
    onDismiss: () -> Unit
) {
    if (isVisible) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable { onDismiss() },
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .padding(16.dp)
                    .width(280.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Locked",
                        tint = Color.Gray,
                        modifier = Modifier
                            .size(48.dp)
                            .padding(8.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "This star is locked",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Complete previous stars to unlock this one.",
                        textAlign = TextAlign.Center,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { onDismiss() },
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF3399FF)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text("Got it")
                    }
                }
            }
        }
    }
}


@Composable
fun AddTaskDialog(onDismiss: () -> Unit, onTaskAdded: (Task) -> Unit) {
    var taskName by remember { mutableStateOf("") }
    var targetValue by remember { mutableStateOf("5") }
    var selectedCategory by remember { mutableStateOf(TaskCategory.LANGUAGE) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Task") },
        text = {
            Column {
                OutlinedTextField(
                    value = taskName,
                    onValueChange = { taskName = it },
                    label = { Text("Task Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = targetValue,
                    onValueChange = { if (it.isEmpty() || it.toIntOrNull() != null) targetValue = it },
                    label = { Text("Target Value") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("Category:")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TaskCategory.values().forEach { category ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .padding(4.dp)
                                .clickable { selectedCategory = category }
                                .border(
                                    width = 1.dp,
                                    color = if (selectedCategory == category)
                                        colorScheme.primary
                                    else Color.Transparent,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(8.dp)
                        ) {
                            Icon(
                                painter = when (category) {
                                    TaskCategory.LANGUAGE -> painterResource(R.drawable.ic_record_voice_outlined)
                                    TaskCategory.FITNESS -> painterResource(R.drawable.ic_fitness)
                                    TaskCategory.WORK -> painterResource(R.drawable.ic_work_suitcase)
                                    TaskCategory.PERSONAL -> painterResource(R.drawable.ic_personal)
                                },
                                contentDescription = null
                            )
                            Text(
                                text = category.name.lowercase().capitalize(),
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (taskName.isNotBlank() && targetValue.toIntOrNull() != null) {
                        onTaskAdded(
                            Task(
                                name = taskName,
                                progress = 0,
                                target = targetValue.toInt(),
                                category = selectedCategory
                            )
                        )
                    }
                },
                enabled = taskName.isNotBlank() && targetValue.toIntOrNull() != null
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
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