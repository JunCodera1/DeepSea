package com.example.deepsea.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.deepsea.R
import com.example.deepsea.data.model.daily.TaskCategory
import com.example.deepsea.ui.screens.feature.game.LeaderboardEntry
import com.example.deepsea.ui.screens.feature.Task

@Composable
fun TaskItem(task: Task, onTaskUpdated: (Task) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        colors = CardDefaults.cardColors(
            containerColor = when (task.category) {
                TaskCategory.LANGUAGE -> colorScheme.primaryContainer.copy(alpha = 0.3f)
                TaskCategory.FITNESS -> colorScheme.secondaryContainer.copy(alpha = 0.3f)
                TaskCategory.WORK -> colorScheme.tertiaryContainer.copy(alpha = 0.3f)
                TaskCategory.PERSONAL -> colorScheme.surfaceVariant.copy(alpha = 0.3f)
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(colorScheme.surface)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(colorScheme.primaryContainer)
                            .padding(6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Thay đổi cách nạp icon, sử dụng trực tiếp từ R.drawable
                        Icon(
                            painter = painterResource(
                                id = when (task.category) {
                                    TaskCategory.LANGUAGE -> R.drawable.home_learn // Thay bằng icon ngôn ngữ của bạn
                                    TaskCategory.FITNESS -> R.drawable.home_daily // Thay bằng icon fitness của bạn
                                    TaskCategory.WORK -> R.drawable.home_rank // Thay bằng icon công việc của bạn
                                    TaskCategory.PERSONAL -> R.drawable.home_profile // Thay bằng icon cá nhân của bạn
                                }
                            ),
                            contentDescription = null,
                            tint = Color.Unspecified, // Để hiển thị màu gốc của icon
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = task.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = colorScheme.onSurface
                    )
                }

                Text(
                    text = "${task.progress}/${task.target}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = if (task.progress >= task.target)
                        colorScheme.primary
                    else colorScheme.onSurfaceVariant
                )
            }

            // Các phần code khác giữ nguyên
            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = (task.progress.toFloat() / task.target).coerceIn(0f, 1f),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .height(6.dp)
                    .clip(RoundedCornerShape(6.dp)),
                color = when {
                    task.progress >= task.target -> colorScheme.primary
                    task.progress > 0 -> colorScheme.secondary
                    else -> colorScheme.surfaceVariant
                },
                trackColor = colorScheme.surfaceVariant
            )

            if (expanded) {
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            onTaskUpdated(task.copy(progress = (task.progress - 1).coerceAtLeast(0)))
                        },
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, colorScheme.outline.copy(alpha = 0.5f))
                    ) {
                        // Sử dụng Icons.Default thay vì painterResource nếu không có icon tương ứng
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Decrease",
                            modifier = Modifier.size(18.dp),
                            tint = colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "Decrease",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Button(
                        onClick = {
                            onTaskUpdated(task.copy(progress = task.progress + 1))
                        },
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Increase",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "Increase",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Composable
fun TopPlayerItem(entry: LeaderboardEntry) {
    val size = when (entry.rank) {
        1 -> 80.dp
        2 -> 70.dp
        else -> 60.dp
    }

    val color = when (entry.rank) {
        1 -> Color(0xFFFFD700) // Gold
        2 -> Color(0xFFC0C0C0) // Silver
        else -> Color(0xFFCD7F32) // Bronze
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "#${entry.rank}",
            fontWeight = FontWeight.Bold,
            color = color
        )

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .size(size)
                .border(2.dp, color, CircleShape)
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = entry.username.first().toString(),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = entry.username,
            fontWeight = FontWeight.Medium
        )

        Text(
            text = "${entry.score} XP",
            color = Color.Gray,
            fontSize = 14.sp
        )
    }
}

@Composable
fun StatItem(
    label: String,
    value: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )

        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.Gray
        )
    }
}