package com.example.deepsea.ui.screens.feature

import androidx.compose.foundation.clickable

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsPage(
    onBackPressed: () -> Unit,
    onPreferencesClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onCoursesClick: () -> Unit = {},
    onDeepSeaForSchoolsClick: () -> Unit = {},
    onPrivacySettingsClick: () -> Unit = {},
    onHelpCenterClick: () -> Unit = {},
    onFeedbackClick: () -> Unit = {},
    onSignOut: () -> Unit = {},
    paddingValues: PaddingValues = PaddingValues(0.dp)
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF5F7F9)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Top app bar with back button
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    TextButton(onClick = {}) {
                        Text(
                            text = "DONE",
                            color = Color(0xFF4DB6FF),
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF5F7F9),
                    titleContentColor = Color.Black
                )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Account Section
                Text(
                    text = "Account",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    color = Color.White
                ) {
                    Column {
                        SettingsItem(
                            title = "Preferences",
                            onClick = onPreferencesClick
                        )
                        Divider()
                        SettingsItem(
                            title = "Profile",
                            onClick = onProfileClick
                        )
                        Divider()
                        SettingsItem(
                            title = "Notifications",
                            onClick = onNotificationsClick
                        )
                        Divider()
                        SettingsItem(
                            title = "Courses",
                            onClick = onCoursesClick
                        )
                        Divider()
                        SettingsItem(
                            title = "DeepSea for Schools",
                            onClick = onDeepSeaForSchoolsClick
                        )
                        Divider()
                        SettingsItem(
                            title = "Privacy settings",
                            onClick = onPrivacySettingsClick
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Support Section
                Text(
                    text = "Support",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    color = Color.White
                ) {
                    Column {
                        SettingsItem(
                            title = "Help Center",
                            onClick = onHelpCenterClick
                        )
                        Divider()
                        SettingsItem(
                            title = "Feedback",
                            onClick = onFeedbackClick
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Sign Out Button
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    color = Color.White
                ) {
                    TextButton(
                        onClick = onSignOut,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Color(0xFF4DB6FF)
                        )
                    ) {
                        Text(
                            text = "SIGN OUT",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun SettingsItem(
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp, horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 16.sp,
            color = Color.DarkGray
        )

        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = "Navigate",
            tint = Color.LightGray
        )
    }
}