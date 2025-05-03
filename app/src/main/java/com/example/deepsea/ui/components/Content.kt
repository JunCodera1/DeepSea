package com.example.deepsea.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.deepsea.data.model.daily.TaskCategory
import com.example.deepsea.ui.screens.feature.DailySummary
import com.example.deepsea.ui.screens.feature.LanguageProgress
import com.example.deepsea.ui.screens.feature.Task

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