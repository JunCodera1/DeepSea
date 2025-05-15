package com.example.deepsea.ui.components

import androidx.annotation.DrawableRes
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.deepsea.R
import com.example.deepsea.text.PrimaryText
import androidx.compose.foundation.clickable

import com.example.deepsea.text.TitleText
import com.example.deepsea.ui.screens.feature.home.orderToPercentage
import com.example.deepsea.ui.theme.FeatherGreen
import com.example.deepsea.ui.theme.FeatherGreenDark
import androidx.compose.ui.Modifier

// Added missing imports


data class SectionData(
    val title: String,
    val color: Color,
    val darkerColor: Color = color,
    val description: String = "",
    @DrawableRes val image: Int = R.drawable.cut,
    val level: String = "A1", // Changed from nullable to non-nullable with default value
    val units: List<UnitData> = emptyList()
) {
    init {
        // Modified validation to check non-nullable level
        require(level.isNotBlank()) { "Level must not be blank" }
    }
}

/**
 * Data class representing a learning unit with its visual properties
 *
 * @property title The title of the unit shown in the header
 * @property color The main color for the unit's theme
 * @property darkerColor A darker shade of the main color, used for 3D effects
 * @property description A brief description of the unit's content
 * @property image Resource ID for the unit's illustration
 */
@Immutable
data class UnitData(
    val id: Long = 0L,  // Added id property for navigation
    val title: String = "Unit 1",
    val color: Color = FeatherGreen,
    val darkerColor: Color = FeatherGreenDark,
    val description: String = "Make introductions",
    @DrawableRes val image: Int = R.drawable.ic_shopping_cart
)
/**
 * Calculates the horizontal alignment percentage for each star in a unit.
 *
 * @param starIndex Index of the star (starting from 0)
 * @param isRightAligned If true, stars are aligned from right (for even units), otherwise from left
 * @return Float value from 0f to 1f to use with fillMaxWidth()
 */
fun calculateStarPosition(starIndex: Int, isRightAligned: Boolean): Float {
    val step = 0.15f // Horizontal distance between stars
    val percentage = starIndex * step
    return if (isRightAligned) 1f - percentage else percentage
}