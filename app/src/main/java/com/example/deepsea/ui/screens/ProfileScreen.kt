package com.example.deepsea.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ProfilePage(userData: UserProfileData) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF5F7F9)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Profile Header with Avatar and Settings
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFFF3CC))
                    .padding(vertical = 24.dp)
            ) {
                // Settings button
                IconButton(
                    onClick = { /* Open settings */ },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(end = 16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = Color.DarkGray
                    )
                }

                // Avatar placeholder - replace R.drawable.avatar with your actual resource
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF5B3A29))
                ) {
                    // This would be your actual avatar image
                    // For now using a colored box to represent the avatar
                }
            }

            // Username and basic info
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = userData.name,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.DarkGray
                )

                Text(
                    text = "Joined ${userData.joinDate}",
                    fontSize = 16.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Following and Followers row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "${userData.following} Following",
                        fontSize = 16.sp,
                        color = Color(0xFF4DB6FF)
                    )

                    Text(
                        text = "  •  ",
                        fontSize = 16.sp,
                        color = Color.Gray
                    )

                    Text(
                        text = "${userData.followers} Followers",
                        fontSize = 16.sp,
                        color = Color(0xFF4DB6FF)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Add Friends button
                Button(
                    onClick = { /* Add friend action */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF4DB6FF)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = Color(0xFF4DB6FF)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ADD FRIENDS",
                        fontWeight = FontWeight.Bold
                    )
                }

                // Share button (represented by the export icon in your reference)
                IconButton(
                    onClick = { /* Share profile */ },
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(end = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share profile",
                        tint = Color(0xFF4DB6FF)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Statistics header
            Text(
                text = "Statistics",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp),
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Year in Review Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFE3F2FD)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "2023 Year in Review",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.DarkGray
                        )

                        Text(
                            text = "Look back at 2023 and discover your unique learner style!",
                            fontSize = 16.sp,
                            color = Color.Gray
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { /* Open year in review */ },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4DB6FF)
                            )
                        ) {
                            Text(
                                text = "SEE YEAR IN REVIEW",
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    // Placeholder for mascot image
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF8BC34A))
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Stats cards
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Day streak card
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Fire icon placeholder
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(Color(0xFFFFA726), CircleShape)
                        )

                        Text(
                            text = "${userData.dayStreak}",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.DarkGray
                        )

                        Text(
                            text = "Day streak",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // XP card
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // XP icon placeholder
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(Color(0xFFFFEB3B), CircleShape)
                        )

                        Text(
                            text = "${userData.totalXp}",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.DarkGray
                        )

                        Text(
                            text = "Total XP",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Achievement icons row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Achievement icon placeholders
                achievementIcon(Color(0xFFFFB74D))
                achievementIcon(Color(0xFF4FC3F7))
                achievementIcon(Color(0xFFBCAAA4))
                achievementIcon(Color(0xFF9575CD), isSelected = true)
                achievementIcon(Color(0xFFFFD54F))
                achievementIcon(Color(0xFFFFB74D))
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun achievementIcon(color: Color, isSelected: Boolean = false) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(
                if (isSelected) Color(0xFFE3F2FD) else Color.Transparent
            )
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(color)
                .align(Alignment.Center)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun UserProfilePreview() {
    val sampleUserData = UserProfileData(
        name = "DaveP",
        username = "davep",
        joinDate = "September 2015",
        following = 5,
        followers = 7,
        dayStreak = 786,
        totalXp = 89024,
        currentLeague = "Diamond League",
        topFinishes = 3,
        courses = listOf("Italian", "Spanish"),
        isFriend = false
    )
    ProfilePage(userData = sampleUserData)
}

data class UserProfileData(
    val name: String,
    val username: String,
    val joinDate: String,
    val courses: List<String>,
    val followers: Int,
    val following: Int,
    val dayStreak: Int,
    val totalXp: Int,
    val currentLeague: String,
    val topFinishes: Int,
    val isFriend: Boolean = false
)