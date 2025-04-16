package com.example.deepsea.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ProfilePage(userData: UserProfileData) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(Color.White)
    ) {
        // Avatar placeholder
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(Color(0xFFDDDDDD))
                .border(1.dp, Color(0xFFCCCCCC))
        )

        // User info
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = userData.name,
                        style = TextStyle(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "@${userData.username}",
                        style = TextStyle(
                            fontSize = 16.sp,
                            color = Color(0xFF666666)
                        )
                    )
                    Text(
                        text = "Joined ${userData.joinDate}",
                        style = TextStyle(
                            fontSize = 14.sp,
                            color = Color(0xFF666666)
                        )
                    )
                }
                Button(
                    onClick = { /*Add friend action*/ },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007BFF))
                ) {
                    Text(
                        text = "ADD FRIEND",
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Stats section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left column
                Column {
                    Text(
                        text = "Courses",
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Following",
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "${userData.following}",
                        style = TextStyle(fontSize = 16.sp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Followers",
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "${userData.followers}",
                        style = TextStyle(fontSize = 16.sp)
                    )
                }

                // Right column
                Column {
                    Text(
                        text = "Day streak",
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "${userData.dayStreak}",
                        style = TextStyle(fontSize = 16.sp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Total XP",
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "${userData.totalXp}",
                        style = TextStyle(fontSize = 16.sp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Current league",
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "${userData.currentLeague}",
                        style = TextStyle(fontSize = 16.sp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Top 3 finishes",
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "${userData.topFinishes}",
                        style = TextStyle(fontSize = 16.sp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Monthly Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Monthly Badges",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                TextButton(onClick = { /* View all badges */ }) {
                    Text(
                        text = "VIEW ALL",
                        color = Color(0xFF007BFF)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(Color(0xFFF5F5F5))
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UserProfilePreview() {
    val sampleUserData = UserProfileData(
        name = "Huy V6",
        username = "BlackNoir1172005",
        joinDate = "August 2024",
        following = 45,
        followers = 23,
        dayStreak = 235,
        totalXp = 9102,
        currentLeague = "WEEK 2 Ruby",
        topFinishes = 1,
        courses = listOf("Course 1", "Course 2") // Example courses
    )
    ProfilePage(userData = sampleUserData)
}

data class UserProfileData(
    val name:String,
    val username:String,
    val joinDate:String,
    val courses: List<String>,
    val followers: Int,
    val following: Int,
    val dayStreak: Int,
    val totalXp: Int,
    val currentLeague: String,
    val topFinishes: Int,
    val isFriend: Boolean = false


)