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
fun LanguageSelectionPage(navController: NavController) {
    // State to track selected languages using a set
    var selectedLanguages by remember { mutableStateOf(setOf<String>()) }
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
                progress = 0.1f,
                modifier = Modifier
                    .weight(0.2f)
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
                        text = "Select the languages you want to learn",
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

        // Selected languages counter
        if (selectedLanguages.isNotEmpty()) {
            Text(
                text = "${selectedLanguages.size} language${if (selectedLanguages.size > 1) "s" else ""} selected",
                modifier = Modifier.padding(start = 16.dp, bottom = 8.dp),
                fontSize = 16.sp,
                color = purpleColor
            )
        }

        // Language options
        LanguageOption(
            language = "English",
            flagResId = R.drawable.flag_england,
            isSelected = selectedLanguages.contains("English"),
            onSelect = { toggleLanguageSelection("English", selectedLanguages) { selectedLanguages = it } }
        )
        LanguageOption(
            language = "Spanish",
            flagResId = R.drawable.flag_spain,
            isSelected = selectedLanguages.contains("Spanish"),
            onSelect = { toggleLanguageSelection("Spanish", selectedLanguages) { selectedLanguages = it } }
        )
        LanguageOption(
            language = "French",
            flagResId = R.drawable.flag_france,
            isSelected = selectedLanguages.contains("French"),
            onSelect = { toggleLanguageSelection("French", selectedLanguages) { selectedLanguages = it } }
        )
        LanguageOption(
            language = "German",
            flagResId = R.drawable.flag_germany,
            isSelected = selectedLanguages.contains("German"),
            onSelect = { toggleLanguageSelection("German", selectedLanguages) { selectedLanguages = it } }
        )
        LanguageOption(
            language = "Italian",
            flagResId = R.drawable.flag_italy,
            isSelected = selectedLanguages.contains("Italian"),
            onSelect = { toggleLanguageSelection("Italian", selectedLanguages) { selectedLanguages = it } }
        )
        LanguageOption(
            language = "Japanese",
            flagResId = R.drawable.flag_japan,
            isSelected = selectedLanguages.contains("Japanese"),
            onSelect = { toggleLanguageSelection("Japanese", selectedLanguages) { selectedLanguages = it } }
        )

        Spacer(modifier = Modifier.weight(1f))

        // Continue button - enabled when at least one language is selected
        val isAnyLanguageSelected = selectedLanguages.isNotEmpty()
        Button(
            onClick = {
                if (isAnyLanguageSelected) {
                    navController.navigate("daily-goal-selection") // Replace with your next screen route
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isAnyLanguageSelected) purpleColor else Color.LightGray
            ),
            shape = RoundedCornerShape(8.dp),
            enabled = isAnyLanguageSelected
        ) {
            Text("CONTINUE", fontSize = 16.sp)
        }
    }
}

// Helper function to toggle language selection
private fun toggleLanguageSelection(
    language: String,
    currentSelections: Set<String>,
    updateSelection: (Set<String>) -> Unit
) {
    val updatedSelections = currentSelections.toMutableSet()
    if (updatedSelections.contains(language)) {
        updatedSelections.remove(language)
    } else {
        updatedSelections.add(language)
    }
    updateSelection(updatedSelections)
}

@Composable
fun LanguageOption(
    language: String,
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
            // Flag
            Image(
                painter = painterResource(id = flagResId),
                contentDescription = language,
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(4.dp))
            )

            // Language name
            Text(
                text = language,
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
fun LanguageSelectionPreview() {
    LanguageSelectionPage(navController = rememberNavController())
}