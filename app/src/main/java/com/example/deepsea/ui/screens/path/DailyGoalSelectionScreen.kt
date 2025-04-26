package com.example.deepsea.ui.screens.path

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.deepsea.R

@Composable
fun DailyGoalSelectionPage(navController: NavController) {
    // Define colors
    val lightBlue = Color(0xFFE1F5FE)
    val blue = Color(0xFF29B6F6)
    val green = Color(0xFF76C043)

    // State to track selected goal
    var selectedGoal by remember { mutableStateOf("Serious") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Top bar with back button and progress
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = "Back",
                modifier = Modifier
                    .size(24.dp)
                    .clickable { navController.popBackStack() }
            )

            LinearProgressIndicator(
                progress = 0.5f,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp),
                color = Color(0xFFFFC107), // Yellow as shown in the image
                trackColor = Color(0xFFEEEEEE)
            )
        }

        // Title
        Text(
            text = "Great. Now choose a\ndaily goal.",
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, bottom = 32.dp),
            fontSize = 24.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )

        // Goal options
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                GoalOption(
                    label = "Casual",
                    minutes = 5,
                    isSelected = selectedGoal == "Casual",
                    onSelect = { selectedGoal = "Casual" }
                )

                Divider(modifier = Modifier.padding(horizontal = 16.dp))

                GoalOption(
                    label = "Regular",
                    minutes = 10,
                    isSelected = selectedGoal == "Regular",
                    onSelect = { selectedGoal = "Regular" }
                )

                Divider(modifier = Modifier.padding(horizontal = 16.dp))

                GoalOption(
                    label = "Serious",
                    minutes = 15,
                    isSelected = selectedGoal == "Serious",
                    onSelect = { selectedGoal = "Serious" }
                )

                Divider(modifier = Modifier.padding(horizontal = 16.dp))

                GoalOption(
                    label = "Intense",
                    minutes = 20,
                    isSelected = selectedGoal == "Intense",
                    onSelect = { selectedGoal = "Intense" }
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Continue button
        Button(
            onClick = {
                navController.navigate("path_selection") // Replace with your next screen route
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = green
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                "CONTINUE",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun GoalOption(
    label: String,
    minutes: Int,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    val backgroundColor = if (isSelected) Color(0xFFE1F5FE) else Color.White
    val textColor = if (isSelected) Color(0xFF29B6F6) else Color.Black

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .clickable { onSelect() }
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = textColor
        )

        Text(
            text = "$minutes min / day",
            fontSize = 14.sp,
            color = Color.Gray
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DailyGoalSelectionPreview() {
    DailyGoalSelectionPage(navController = rememberNavController())
}