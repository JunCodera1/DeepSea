package com.example.deepsea.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.random.Random

import com.example.deepsea.R

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
fun DailyHeader() {
    val currentDate = remember { java.text.SimpleDateFormat("EEEE, MMMM d, yyyy").format(java.util.Date()) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(colorScheme.primary)
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = "Daily Dashboard",
                color = colorScheme.onPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = currentDate,
                color = colorScheme.onPrimary.copy(alpha = 0.8f),
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun DailyTasksContent() {
    var showAddTaskDialog by remember { mutableStateOf(false) }
    val tasks = remember {
        mutableStateListOf(
            Task("Speaking Practice", 5, 10, TaskCategory.LANGUAGE),
            Task("Vocabulary Review", 20, 30, TaskCategory.LANGUAGE),
            Task("Grammar Exercise", 3, 5, TaskCategory.LANGUAGE),
            Task("Reading", 2, 3, TaskCategory.LANGUAGE),
            Task("Listen to Podcast", 1, 1, TaskCategory.LANGUAGE)
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Daily Summary
            DailySummary(tasks)

            Spacer(modifier = Modifier.height(16.dp))

            // Task List
            Text(
                text = "Today's Tasks",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(tasks) { task ->
                    TaskItem(task) { updatedTask ->
                        val index = tasks.indexOf(task)
                        if (index != -1) {
                            tasks[index] = updatedTask
                        }
                    }
                }
            }
        }

        // FAB for adding new tasks
        FloatingActionButton(
            onClick = { showAddTaskDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = colorScheme.secondary
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Task"
            )
        }

        // Add Task Dialog
        if (showAddTaskDialog) {
            AddTaskDialog(
                onDismiss = { showAddTaskDialog = false },
                onTaskAdded = { newTask ->
                    tasks.add(newTask)
                    showAddTaskDialog = false
                }
            )
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
fun LanguageProgressContent() {
    val languages = remember {
        listOf(
            LanguageProgress("Vietnamese", 65, listOf(10, 8, 5, 7)),
            LanguageProgress("English", 85, listOf(9, 8, 7, 9)),
            LanguageProgress("Japanese", 42, listOf(6, 3, 5, 4)),
            LanguageProgress("French", 28, listOf(3, 4, 2, 5))
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Weekly streak
        WeeklyStreakCard()

        Spacer(modifier = Modifier.height(16.dp))

        // Language progress
        Text(
            text = "Your Languages",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(languages) { language ->
                LanguageCard(language)
            }
        }
    }
}

@Composable
fun WeeklyStreakCard() {
    val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    val streak = remember {
        List(7) { Random.nextBoolean() }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Weekly Streak",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "${streak.count { it }} days",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                daysOfWeek.forEachIndexed { index, day ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(
                                    if (streak[index]) colorScheme.primary
                                    else colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                )
                                .border(
                                    width = 2.dp,
                                    color = if (streak[index]) colorScheme.primary
                                    else colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (streak[index]) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = colorScheme.onPrimary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = day,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LanguageCard(language: LanguageProgress) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = language.name.first().toString(),
                            color = colorScheme.onPrimaryContainer,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = language.name,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = getLanguageLevel(language.overallProgress),
                            fontSize = 14.sp,
                            color = colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }

                Text(
                    text = "${language.overallProgress}%",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            LinearProgressIndicator(
                progress = language.overallProgress / 100f,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = colorScheme.primary,
                trackColor = colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Skills breakdown
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SkillIndicator("Speaking", language.skills[0])
                SkillIndicator("Writing", language.skills[1])
                SkillIndicator("Reading", language.skills[2])
                SkillIndicator("Listening", language.skills[3])
            }
        }
    }
}

@Composable
fun SkillIndicator(name: String, level: Int) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
    ) {
        val color = when {
            level >= 9 -> colorScheme.primary
            level >= 7 -> colorScheme.secondary
            level >= 5 -> colorScheme.tertiary
            else -> colorScheme.error
        }

        Canvas(modifier = Modifier.size(30.dp)) {
            val strokeWidth = 5f
            val center = Offset(size.width / 2, size.height / 2)
            val radius = (size.width - strokeWidth) / 2

            // Background circle
            drawCircle(
                color = Color(0xFF4FC3F7) ,
                radius = radius,
                center = center,
                style = androidx.compose.ui.graphics.drawscope.Stroke(
                    width = strokeWidth
                )
            )

            // Progress arc
            val sweepAngle = 360f * (level / 10f)
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = androidx.compose.ui.graphics.drawscope.Stroke(
                    width = strokeWidth,
                    cap = StrokeCap.Round
                )
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = name,
            fontSize = 12.sp,
            textAlign = TextAlign.Center
        )

        Text(
            text = "$level/10",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}

enum class TaskCategory {
    LANGUAGE, FITNESS, WORK, PERSONAL
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