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
    // State to track selected survey option
    var selectedSurvey by remember { mutableStateOf<String?>(null) }
    val scrollState = rememberScrollState()

    // Purple color for selected state
    val purpleColor = Color(0xFF6750A4)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(scrollState) // 👈 thêm dòng này
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
                    .clickable { /* Handle back navigation */ }
            )

            LinearProgressIndicator(
                progress = 0f,
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
                        text = "How did you hear about DeepSea?",
                        modifier = Modifier.padding(16.dp),
                        fontSize = 18.sp
                    )
                }
            }
        }

        // Header "For English speakers"
        Text(
            text = "For English speakers",
            modifier = Modifier.padding(start = 16.dp, bottom = 16.dp),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        // Language options
        SurveyOption(
            survey = "Friends",
            flagResId = R.drawable.ic_friends,
            isSelected = selectedSurvey == "Friends",
            onSelect = { selectedSurvey = "Friends" }
        )
        SurveyOption(
            survey = "TV",
            flagResId = R.drawable.ic_tv,
            isSelected = selectedSurvey == "TV",
            onSelect = { selectedSurvey = "TV" }
        )
        SurveyOption(
            survey = "TikTok",
            flagResId = R.drawable.ic_tiktok,
            isSelected = selectedSurvey == "TikTok",
            onSelect = { selectedSurvey = "TikTok" }
        )
        SurveyOption(
            survey = "News",
            flagResId = R.drawable.ic_news,
            isSelected = selectedSurvey == "News",
            onSelect = { selectedSurvey = "News" }
        )
        SurveyOption(
            survey = "Youtube",
            flagResId = R.drawable.ic_youtube,
            isSelected = selectedSurvey == "Youtube",
            onSelect = { selectedSurvey = "Youtube" }
        )
        SurveyOption(
            survey = "Social",
            flagResId = R.drawable.ic_social,
            isSelected = selectedSurvey == "Social",
            onSelect = { selectedSurvey = "Social" }
        )
        SurveyOption(
            survey = "Other",
            flagResId = R.drawable.ic_other,
            isSelected = selectedSurvey == "Other",
            onSelect = { selectedSurvey = "Other" }
        )

        Spacer(modifier = Modifier.weight(1f))

        // Continue button - changes color when an option is selected
        val isOptionSelected = selectedSurvey != null
        Button(
            onClick = {
                if (isOptionSelected) {
                    navController.navigate("learn-selection")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isOptionSelected) purpleColor else Color.LightGray
            ),
            shape = RoundedCornerShape(8.dp),
            enabled = isOptionSelected
        ) {
            Text("CONTINUE", fontSize = 16.sp)
        }
    }
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
            // Flag/Icon
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
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SurveySelectionPreview() {
    SurveySelectionPage(navController = rememberNavController())
}