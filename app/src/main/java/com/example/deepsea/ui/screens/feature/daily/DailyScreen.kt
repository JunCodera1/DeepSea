package com.example.deepsea.ui.screens.feature.daily

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.deepsea.data.model.daily.TaskCategory
import com.example.deepsea.ui.components.DailyHeader
import com.example.deepsea.ui.components.DailyTasksContent
import com.example.deepsea.ui.components.LanguageProgressContent

@Composable
fun DailyPage() {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Daily Tasks", "Language Progress")

    Column(modifier = Modifier.fillMaxSize()) {
        // Header
        DailyHeader()

        // Tabs
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = colorScheme.primaryContainer,
            contentColor = colorScheme.onPrimaryContainer
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title) }
                )
            }
        }

        // Content based on selected tab
        when (selectedTab) {
            0 -> DailyTasksContent()
            1 -> LanguageProgressContent()
        }
    }
}

@Composable
fun DailySummary(tasks: List<Task>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Daily Summary",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Calculate completion statistics
            val totalTasks = tasks.size
            val completedTasks = tasks.count { it.progress >= it.target }
            val completionPercentage = if (totalTasks > 0) (completedTasks * 100 / totalTasks) else 0

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "$completedTasks/$totalTasks Tasks",
                        fontSize = 16.sp
                    )
                    Text(
                        text = "Completed: $completionPercentage%",
                        fontSize = 14.sp,
                        color = colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }

                CircularProgressIndicator(
                    progress = completionPercentage / 100f,
                    modifier = Modifier.size(50.dp),
                    color = colorScheme.primary,
                    trackColor = colorScheme.surfaceVariant
                )
            }
        }
    }
}

data class Task(
    val name: String,
    val progress: Int,
    val target: Int,
    val category: TaskCategory
)

data class LanguageProgress(
    val name: String,
    val overallProgress: Int,
    val skills: List<Int> // [speaking, writing, reading, listening]
)

fun getLanguageLevel(progress: Int): String {
    return when {
        progress >= 90 -> "C2 - Proficient"
        progress >= 75 -> "C1 - Advanced"
        progress >= 60 -> "B2 - Upper Intermediate"
        progress >= 45 -> "B1 - Intermediate"
        progress >= 30 -> "A2 - Elementary"
        else -> "A1 - Beginner"
    }
}

fun String.capitalize(): String {
    return this.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
}

@Preview
@Composable
fun DailyPagePreview() {
    MaterialTheme {
        DailyPage()
    }
}

@Preview
@Composable
fun DailyTasksContentPreview() {
    MaterialTheme {
        DailyTasksContent()
    }
}

@Preview
@Composable
fun LanguageProgressContentPreview() {
    MaterialTheme {
        LanguageProgressContent()
    }
}