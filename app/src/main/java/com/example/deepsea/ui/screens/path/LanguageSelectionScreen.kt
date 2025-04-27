package com.example.deepsea.ui.screens.path

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
import com.example.deepsea.R
import com.example.deepsea.data.model.language.LanguageOption
import com.example.deepsea.ui.viewmodel.languageSelection.LanguageSelectionViewModel
import com.example.deepsea.utils.SessionManager


@Composable
fun LanguageSelectionPage(
    navController: NavController,
    languageSelectionViewModel: LanguageSelectionViewModel,
    sessionManager: SessionManager
) {
    val userId by sessionManager.userId.collectAsState(initial = null)

    // State to track selected languages using a set
    val selectedLanguages by languageSelectionViewModel.selectedLanguages.collectAsState()
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

        val languagesIconMap = mapOf(
            LanguageOption.JAPANESE to R.drawable.flag_japan,
            LanguageOption.ITALY to R.drawable.flag_italy,
            LanguageOption.GERMANY to R.drawable.flag_germany,
            LanguageOption.ENGLISH to R.drawable.flag_england,
            LanguageOption.FRENCH to R.drawable.flag_france,
            LanguageOption.SPANISH to R.drawable.flag_spain,
        )

        // Render all language options
        LanguageOption.values().forEach { option ->
            LanguageOptionItem(
                option = option,
                iconResId = languagesIconMap[option] ?: R.drawable.ic_other, // Fallback icon
                isSelected = selectedLanguages.contains(option),
                onSelect = { languageSelectionViewModel.toggleLanguageSelection(option) }
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Continue button - enabled when at least one language is selected
        val isAnyLanguageSelected = selectedLanguages.isNotEmpty()
        Button(
            onClick = {
                if (isAnyLanguageSelected) {
                    languageSelectionViewModel.saveLanguageSelections(userId = userId)
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
fun LanguageOptionItem(
    option: LanguageOption,
    iconResId: Int,
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
                painter = painterResource(id = iconResId),
                contentDescription = option.displayName,
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(4.dp))
            )

            // Language option name
            Text(
                text = option.displayName,
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
}