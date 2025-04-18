package com.example.deepsea.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.deepsea.R

@Composable
fun SurveySelectionPage(navController: NavController) {
    // State to track selected survey options
    var selectedSurveys by remember { mutableStateOf(setOf<String>()) }
    val scrollState = rememberScrollState()

    // Purple color for selected state
    val purpleColor = Color(0xFF6750A4)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(scrollState)
    ) {
        // Top bar with back button
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
                    .size(45.dp)
                    .clickable { navController.popBackStack() }
            )

            LinearProgressIndicator(
                progress = 0.2f,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp),
                color = Color(0xFF8CC83C),
                trackColor = Color(0xFFEEEEEE)
            )
        }

        // Mascot and speech bubble
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Green owl mascot
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Mascot",
                    modifier = Modifier
                        .size(100.dp)
                )

                // Speech bubble
                Card(
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(
                        text = "How did you hear about DeepSea? (Select all that apply)",
                        modifier = Modifier.padding(16.dp),
                        fontSize = 18.sp
                    )
                }
            }
        }

        // Header
        Text(
            text = "Select your answers",
            modifier = Modifier.padding(start = 16.dp, bottom = 8.dp),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        // Selected options counter
        if (selectedSurveys.isNotEmpty()) {
            Text(
                text = "${selectedSurveys.size} option${if (selectedSurveys.size > 1) "s" else ""} selected",
                modifier = Modifier.padding(start = 16.dp, bottom = 16.dp),
                fontSize = 16.sp,
                color = purpleColor
            )
        }

        // Survey options
        SurveyOption(
            survey = "Friends",
            flagResId = R.drawable.ic_friends,
            isSelected = selectedSurveys.contains("Friends"),
            onSelect = { toggleSurveySelection("Friends", selectedSurveys) { selectedSurveys = it } }
        )
        SurveyOption(
            survey = "TV",
            flagResId = R.drawable.ic_tv,
            isSelected = selectedSurveys.contains("TV"),
            onSelect = { toggleSurveySelection("TV", selectedSurveys) { selectedSurveys = it } }
        )
        SurveyOption(
            survey = "TikTok",
            flagResId = R.drawable.ic_tiktok,
            isSelected = selectedSurveys.contains("TikTok"),
            onSelect = { toggleSurveySelection("TikTok", selectedSurveys) { selectedSurveys = it } }
        )
        SurveyOption(
            survey = "News",
            flagResId = R.drawable.ic_news,
            isSelected = selectedSurveys.contains("News"),
            onSelect = { toggleSurveySelection("News", selectedSurveys) { selectedSurveys = it } }
        )
        SurveyOption(
            survey = "Youtube",
            flagResId = R.drawable.ic_youtube,
            isSelected = selectedSurveys.contains("Youtube"),
            onSelect = { toggleSurveySelection("Youtube", selectedSurveys) { selectedSurveys = it } }
        )
        SurveyOption(
            survey = "Social",
            flagResId = R.drawable.ic_social,
            isSelected = selectedSurveys.contains("Social"),
            onSelect = { toggleSurveySelection("Social", selectedSurveys) { selectedSurveys = it } }
        )
        SurveyOption(
            survey = "Other",
            flagResId = R.drawable.ic_other,
            isSelected = selectedSurveys.contains("Other"),
            onSelect = { toggleSurveySelection("Other", selectedSurveys) { selectedSurveys = it } }
        )

        Spacer(modifier = Modifier.weight(1f))

        // Continue button - changes color when at least one option is selected
        val isAnyOptionSelected = selectedSurveys.isNotEmpty()
        Button(
            onClick = {
                if (isAnyOptionSelected) {
                    navController.navigate("learn-selection")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isAnyOptionSelected) purpleColor else Color.LightGray
            ),
            shape = RoundedCornerShape(8.dp),
            enabled = isAnyOptionSelected
        ) {
            Text("CONTINUE", fontSize = 16.sp)
        }
    }
}

// Helper function to toggle survey selection
private fun toggleSurveySelection(
    option: String,
    currentSelections: Set<String>,
    updateSelection: (Set<String>) -> Unit
) {
    val updatedSelections = currentSelections.toMutableSet()
    if (updatedSelections.contains(option)) {
        updatedSelections.remove(option)
    } else {
        updatedSelections.add(option)
    }
    updateSelection(updatedSelections)
}

@Composable
fun SurveyOption(
    survey: String,
    flagResId: Int,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    val purpleColor = Color(0xFF6750A4)
    val cardModifier = if (isSelected) {
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .border(2.dp, purpleColor, RoundedCornerShape(16.dp))
            .clickable { onSelect() }
    } else {
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onSelect() }
    }

    Card(
        modifier = cardModifier,
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Image(
                painter = painterResource(id = flagResId),
                contentDescription = survey,
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(4.dp))
            )

            // Survey option name
            Text(
                text = survey,
                modifier = Modifier.padding(start = 16.dp),
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )

            // Add a checkmark icon when selected
            if (isSelected) {
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    painter = painterResource(id = R.drawable.ic_check),
                    contentDescription = "Selected",
                    tint = purpleColor,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SurveySelectionPreview() {
    SurveySelectionPage(navController = rememberNavController())
}