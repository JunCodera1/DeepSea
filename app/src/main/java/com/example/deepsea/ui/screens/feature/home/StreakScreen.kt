@file:Suppress("DEPRECATION")

package com.example.deepsea.ui.components

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.deepsea.R
import com.example.deepsea.ui.viewmodel.home.CourseUiState
import com.example.deepsea.ui.viewmodel.home.HomeViewModel
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*

@Composable
fun StreakScreen(
    homeViewModel: HomeViewModel,
    onDismissRequest: () -> Unit
) {
    val scrollState = rememberScrollState()
    val today = LocalDate.now()
    val currentYearMonth = remember { mutableStateOf(YearMonth.now()) }
    val currentStreak by homeViewModel.dailyStreak.collectAsState()
    val userProfile by homeViewModel.uiState.collectAsState() // Get user profile from uiState

    // Extract streakHistory from userProfile
    val streakHistory = (userProfile as? CourseUiState.Success)?.userProgress?.streakHistory ?: emptyList()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .verticalScroll(scrollState)
    ) {
        StreakTopBar(onDismissRequest)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            StreakHeader(currentStreak)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    MonthSelector(
                        currentYearMonth = currentYearMonth.value,
                        onPreviousMonth = {
                            currentYearMonth.value = currentYearMonth.value.minusMonths(1)
                        },
                        onNextMonth = {
                            currentYearMonth.value = currentYearMonth.value.plusMonths(1)
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    DaysOfWeekHeader()
                    CalendarDays(
                        yearMonth = currentYearMonth.value,
                        today = today,
                        activeDay = today.dayOfMonth,
                        streakHistory = streakHistory // Pass streak history
                    )
                }
            }
            StreakChallengeSection()
            StreakSocietySection()
        }
    }
}

@Composable
fun StreakTopBar(onDismissRequest: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = onDismissRequest) {
            Icon(
                imageVector = Icons.Outlined.Close,
                contentDescription = "Close",
                tint = Color.Gray
            )
        }

        Text(
            text = "Streak",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = Color.Gray
        )

        val context = LocalContext.current

        IconButton(onClick = {
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "Here, what you want to share")
                type = "text/plain"
            }

            val shareIntent = Intent.createChooser(sendIntent, "Share via:")
            context.startActivity(shareIntent)
        }) {
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = "Share",
                tint = Color.Gray
            )
        }
    }
}

@Composable
fun StreakHeader(currentStreak: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFFA726))
            .padding(vertical = 24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "$currentStreak",
                    fontSize = 64.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "day streak!",
                    fontSize = 18.sp,
                    color = Color.White
                )
            }

            Image(
                painter = painterResource(id = R.drawable.ic_fire),
                contentDescription = "Streak flame",
                modifier = Modifier.size(100.dp),
                colorFilter = ColorFilter.tint(Color.White)
            )
        }

        // Streak achievement card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .align(Alignment.BottomCenter)
                .offset(y = 40.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_fire),
                    contentDescription = "Achievement medal",
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFFFFD54F), CircleShape)
                        .padding(8.dp)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = "You've earned your longest streak ever!",
                    fontSize = 16.sp,
                    color = Color.DarkGray
                )
            }
        }
    }
}

@Composable
fun MonthSelector(
    currentYearMonth: YearMonth,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPreviousMonth) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowLeft,
                contentDescription = "Previous month",
                tint = Color.Gray
            )
        }

        Text(
            text = "${currentYearMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${currentYearMonth.year}",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )

        IconButton(onClick = onNextMonth) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "Next month",
                tint = Color.Gray
            )
        }
    }
}

@Composable
fun DaysOfWeekHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        val daysOfWeek = listOf("Mo", "Tu", "We", "Th", "Fr", "Sa", "Su")
        daysOfWeek.forEach { day ->
            Text(
                text = day,
                modifier = Modifier.width(24.dp),
                textAlign = TextAlign.Center,
                color = Color.Gray,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun CalendarDays(
    yearMonth: YearMonth,
    today: LocalDate,
    activeDay: Int,
    streakHistory: List<LocalDate> // Add streak history parameter
) {
    val firstDay = yearMonth.atDay(1).dayOfWeek.value
    val daysInMonth = yearMonth.lengthOfMonth()
    val isCurrentMonth = yearMonth.month == today.month && yearMonth.year == today.year

    Column(modifier = Modifier.fillMaxWidth()) {
        var dayCounter = 1
        val totalSlots = ((firstDay - 1) % 7) + daysInMonth
        val rows = (totalSlots + 6) / 7

        for (row in 0 until rows) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (col in 1..7) {
                    val dayIndex = row * 7 + col
                    val offset = (firstDay - 1) % 7

                    if (dayIndex <= offset || dayIndex > daysInMonth + offset) {
                        Box(modifier = Modifier.size(36.dp))
                    } else {
                        val day = dayIndex - offset
                        val currentDate = yearMonth.atDay(day)
                        val isToday = isCurrentMonth && day == today.dayOfMonth
                        val isActive = streakHistory.contains(currentDate)

                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(
                                    when {
                                        isToday -> Color(0xFFFFA726)
                                        isActive -> Color(0xFFFFE0B2)
                                        else -> Color.Transparent
                                    },
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = day.toString(),
                                color = when {
                                    isToday -> Color.White
                                    else -> Color.DarkGray
                                },
                                textAlign = TextAlign.Center,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StreakChallengeSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp)
    ) {
        Text(
            text = "Streak Challenge",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "7-day Challenge",
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp
                    )

                    Text(
                        text = "Day 1 of 7",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Progress indicator
                    LinearProgressIndicator(
                        progress = 0.14f, // 1/7
                        modifier = Modifier
                            .weight(1f)
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = Color(0xFFFFA726),
                        trackColor = Color(0xFFE0E0E0)
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    // Challenge milestone icons
                    Row {
                        ChallengeMilestone(day = 7, isActive = true)
                        Spacer(modifier = Modifier.width(8.dp))
                        ChallengeMilestone(day = 14, isActive = false)
                        Spacer(modifier = Modifier.width(8.dp))
                        ChallengeMilestone(day = 30, isActive = false)
                    }
                }
            }
        }
    }
}

@Composable
fun ChallengeMilestone(day: Int, isActive: Boolean) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .background(
                if (isActive) Color(0xFFFFA726) else Color(0xFFE0E0E0),
                RoundedCornerShape(4.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.toString(),
            color = if (isActive) Color.White else Color.Gray,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun StreakSocietySection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Text(
            text = "Streak Society",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = null
                )

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = "Reach a 7 day streak to join the Streak Society and earn exclusive rewards.",
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )
            }
        }
    }
}