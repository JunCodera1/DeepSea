package com.example.deepsea.ui.screens.feature.review

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.deepsea.R
import com.example.deepsea.data.model.review.Mistake
import com.example.deepsea.ui.theme.DeepSeaBlue
import com.example.deepsea.ui.theme.HeartRed
import com.example.deepsea.ui.viewmodel.review.MistakesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MistakesScreen(
    onBackClick: () -> Unit,
    viewModel: MistakesViewModel = viewModel(factory =
        MistakesViewModel.Factory(LocalContext.current.applicationContext as Application))
) {
    val mistakes by viewModel.mistakes.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mistakes") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DeepSeaBlue,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = DeepSeaBlue
                    )
                }
                errorMessage != null -> {
                    ErrorMessage(
                        message = errorMessage!!,
                        onRetryClick = { viewModel.loadMistakes() },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                mistakes.isEmpty() -> {
                    EmptyMistakesView(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    MistakesList(
                        mistakes = mistakes,
                        onMarkReviewed = { viewModel.markMistakeAsReviewed(it) },
                        onDelete = { viewModel.deleteMistake(it) }
                    )
                }
            }
        }
    }
}

@Composable
fun MistakesList(
    mistakes: List<Mistake>,
    onMarkReviewed: (Long) -> Unit,
    onDelete: (Long) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(mistakes) { mistake ->
            MistakeItem(
                mistake = mistake,
                onMarkReviewed = { onMarkReviewed(mistake.id) },
                onDelete = { onDelete(mistake.id) }
            )
        }
    }
}

@Composable
fun MistakeItem(
    mistake: Mistake,
    onMarkReviewed: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Từ gốc và phiên âm
            Text(
                text = mistake.word,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = DeepSeaBlue
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Dịch nghĩa đúng
            Text(
                text = "Correct: ${mistake.correctAnswer}",
                fontSize = 18.sp,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Câu trả lời của người dùng
            Text(
                text = "Your answer: ${mistake.userAnswer}",
                fontSize = 18.sp,
                color = HeartRed
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Số lần đã ôn tập
            Text(
                text = "Reviewed ${mistake.reviewCount} times",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = onMarkReviewed,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DeepSeaBlue
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Mark as Reviewed")
                }

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = { showDeleteDialog = true },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = HeartRed.copy(alpha = 0.1f),
                        contentColor = HeartRed
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete"
                    )
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Mistake") },
            text = { Text("Are you sure you want to delete this mistake?") },
            confirmButton = {
                Button(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = HeartRed
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun EmptyMistakesView(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_check_circle),
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = Color(0xFF4CAF50)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "No mistakes yet!",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.DarkGray
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Keep practicing to improve your language skills.",
            fontSize = 16.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
    }
}

@Composable
fun ErrorMessage(
    message: String,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_mistakes),
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = HeartRed
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Oops! Something went wrong",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.DarkGray
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = message,
            fontSize = 16.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onRetryClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = DeepSeaBlue
            )
        ) {
            Text("Retry")
        }
    }
}