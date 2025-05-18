package com.example.deepsea.ui.screens.feature.review

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.deepsea.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewScreen(
    modifier: Modifier = Modifier,
    onMistakesClick: () -> Unit = {},
    onWordsClick: () -> Unit = {},
    onStoriesClick: () -> Unit = {},
    onListenClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFF8F9FA),
                        Color(0xFFE9ECEF)
                    )
                )
            )
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        // Header Section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Review & Practice",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A)
            )

            Surface(
                modifier = Modifier.size(48.dp),
                color = Color(0xFF4CAF50),
                shape = RoundedCornerShape(24.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🎯",
                        fontSize = 24.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Conversation Section
        SectionHeader(
            title = "Practice Session",
            subtitle = "Improve your listening skills"
        )

        Spacer(modifier = Modifier.height(16.dp))

        EnhancedReviewOption(
            title = "Listen & Repeat",
            subtitle = "Practice pronunciation and listening",
            iconResId = R.drawable.ic_headphones,
            backgroundColor = Color(0xFF2196F3),
            iconTint = Color.White,
            onClick = onListenClick
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Collections Section
        SectionHeader(
            title = "Your Collections",
            subtitle = "Review saved content"
        )

        Spacer(modifier = Modifier.height(16.dp))

        CollectionGrid(
            onMistakesClick = onMistakesClick,
            onWordsClick = onWordsClick,
            onStoriesClick = onStoriesClick
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun SectionHeader(
    title: String,
    subtitle: String
) {
    Column {
        Text(
            text = title,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1A1A)
        )
        Text(
            text = subtitle,
            fontSize = 14.sp,
            color = Color(0xFF6C757D),
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
fun CollectionGrid(
    onMistakesClick: () -> Unit,
    onWordsClick: () -> Unit,
    onStoriesClick: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CompactReviewOption(
                title = "Mistakes",
                iconResId = R.drawable.ic_mistakes,
                backgroundColor = Color(0xFFFF5722),
                modifier = Modifier.weight(1f),
                onClick = onMistakesClick
            )

            CompactReviewOption(
                title = "Words",
                iconResId = R.drawable.ic_words,
                backgroundColor = Color(0xFF9C27B0),
                modifier = Modifier.weight(1f),
                onClick = onWordsClick
            )
        }

        EnhancedReviewOption(
            title = "Stories Collection",
            subtitle = "Read and learn from stories",
            iconResId = R.drawable.ic_stories,
            backgroundColor = Color(0xFF4CAF50),
            iconTint = Color.White,
            onClick = onStoriesClick
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedReviewOption(
    title: String,
    subtitle: String,
    iconResId: Int,
    backgroundColor: Color,
    iconTint: Color = Color.White,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = tween(100),
        label = "scale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale),
        onClick = {
            isPressed = true
            onClick()
            isPressed = false
        },
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp,
            pressedElevation = 4.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A)
                )
                Text(
                    text = subtitle,
                    fontSize = 14.sp,
                    color = Color(0xFF6C757D),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Surface(
                modifier = Modifier.size(64.dp),
                color = backgroundColor,
                shape = RoundedCornerShape(18.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = iconResId),
                        contentDescription = null,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompactReviewOption(
    title: String,
    iconResId: Int,
    backgroundColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = tween(100),
        label = "scale"
    )

    Card(
        modifier = modifier.scale(scale),
        onClick = {
            isPressed = true
            onClick()
            isPressed = false
        },
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp,
            pressedElevation = 2.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier.size(56.dp),
                color = backgroundColor,
                shape = RoundedCornerShape(16.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = iconResId),
                        contentDescription = null,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1A1A1A)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ReviewScreenPreview() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF8F9FA)
    ) {
        ReviewScreen()
    }
}