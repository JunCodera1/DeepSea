package com.example.deepsea.ui.screens.feature.review

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.deepsea.R

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
            .padding(16.dp)
    ) {
        Text(
            text = "Conversation",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        ReviewOption(
            title = "Listen",
            iconResId = R.drawable.ic_headphones,
            onClick = onListenClick
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Your collections",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        ReviewOption(
            title = "Mistakes",
            iconResId = R.drawable.ic_mistakes,
            onClick = onMistakesClick
        )

        Spacer(modifier = Modifier.height(16.dp))

        ReviewOption(
            title = "Words",
            iconResId = R.drawable.ic_words,
            onClick = onWordsClick
        )

        Spacer(modifier = Modifier.height(16.dp))

        ReviewOption(
            title = "Stories",
            iconResId = R.drawable.ic_stories,
            onClick = onStoriesClick
        )
    }
}

@Composable
fun ReviewOption(
    title: String,
    iconResId: Int,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        onClick = onClick,
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )

            Image(
                painter = painterResource(id = iconResId),
                contentDescription = null,
                modifier = Modifier.size(48.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ReviewScreenPreview() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF5F5F5)
    ) {
        ReviewScreen()
    }
}
