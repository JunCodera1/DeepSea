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

import com.example.deepsea.text.TitleText
import com.example.deepsea.ui.screens.feature.orderToPercentage
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
    val level: String = "A1",
    val units: List<UnitData> = emptyList()
)
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
    val title: String = "Unit 1",
    val color: Color = FeatherGreen,
    val darkerColor: Color = FeatherGreenDark,
    val description: String = "Make introductions",

    @DrawableRes val image: Int = R.drawable.cut
)

/**
 * Displays the main content of a unit including star buttons and background image
 *
 * @param unitIndex Index of the unit in the list (used for alignment calculation)
 * @param colorMain Primary color for the stars
 * @param colorDark Secondary color for the stars (for 3D effect)
 * @param unitImage Resource ID for the unit's background image
 * @param starCount Number of stars/lessons to display for this unit
 * @param onStarClicked Callback when a star is clicked, provides coordinates and interactivity state
 */
@Composable
fun UnitContent(
    unitIndex: Int,
    colorMain: Color,
    colorDark: Color,
    @DrawableRes unitImage: Int,
    starCount: Int,
    onStarClicked: (coordinateInRoot: Float, isInteractive: Boolean) -> Unit
) {
    // Use Box to allow overlapping components (stars and background image)
    Box {
        // Stars column
        Column(
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            repeat(starCount) { starIndex ->
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Calculate alignment percentage based on star index and unit position

                    val alignPercentage = remember {
                        orderToPercentage(starIndex, unitIndex % 2 == 0)
                    }

                    // Space before the star
                    Spacer(modifier = Modifier.fillMaxWidth(alignPercentage))

                    // Show different star style for the first star in the first unit
                    if (starIndex == 0) {
                        SelectableStarButton(
                            isInitial = unitIndex == 0,
                            colorMain = colorMain,
                            colorDark = colorDark,
                            onStarClicked = onStarClicked
                        )
                    } else {
                        StarButton(onStarClicked)
                    }
                }
            }
        }

        // Background unit image (desaturated)
        Image(
            modifier = Modifier
                .size(210.dp).padding(40.dp)
                .align(
                    // Alternate image alignment based on unit index
                    alignment = if (unitIndex % 2 == 0) Alignment.CenterEnd else Alignment.CenterStart
                ),
            painter = painterResource(id = unitImage),
//            colorFilter = ColorFilter.colorMatrix(
//                colorMatrix = ColorMatrix().apply {
//                    setToSaturation(0f) // Make image grayscale
//                }
//            ),
            contentDescription = "Unit image"
        )
    }
}

/**
 * A LazyColumn that displays all units with their headers and content
 *
 * @param modifier Modifier for the column
 * @param state LazyListState to control scrolling
 * @param units List of UnitData to display
 * @param starCountPerUnit Number of stars/lessons to display per unit
 * @param onStarClicked Callback when a star is clicked
 */
@Composable
fun  UnitsListScreen(
    modifier: Modifier,
    state: LazyListState,
    units: List<UnitData>,
    starCountPerUnit: Int,
    onStarClicked: (coordinateInRoot: Float, isInteractive: Boolean) -> Unit
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        state = state,
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Generate items for each unit
        units.forEachIndexed { unitIndex, unit ->
            item {
                // Unit header with title and description
                UnitHeader(
                    modifier = Modifier.fillMaxWidth(),
                    data = unit
                )

                // Space between header and content
                Spacer(modifier = Modifier.height(48.dp))

                // Unit content with stars and background image
                UnitContent(
                    unitIndex = unitIndex,
                    starCount = starCountPerUnit,
                    unitImage = unit.image,
                    colorMain = unit.color,
                    colorDark = unit.darkerColor,
                    onStarClicked = onStarClicked
                )
            }
        }

        // Add space at the bottom for better scrolling experience
        item {
            Spacer(modifier = Modifier.height(400.dp))
        }
    }
}

/**
 * Header component for each unit, displaying title, description, and an icon
 *
 * @param modifier Modifier for the header
 * @param data UnitData containing title, colors, and description
 */
@Composable
@Preview
fun UnitHeader(
    modifier: Modifier = Modifier,
    data: UnitData = UnitData()
) {
    Row(
        modifier = modifier
            .background(data.color)
            .padding(horizontal = 12.dp)
            .padding(top = 24.dp, bottom = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Title and description
        Column {
            TitleText(
                text = data.title,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(12.dp))
            PrimaryText(
                text = data.description,
                color = Color.White,
                fontSize = 18.sp
            )
        }

        // Notebook icon with 3D effect using nested boxes
        Box(
            modifier = Modifier
                .background(color = data.darkerColor, shape = RoundedCornerShape(10.dp))
                .padding(horizontal = 1.5.dp)
                .padding(top = 1.5.dp, bottom = 3.dp)
        ) {
            Box(
                modifier = Modifier
                    .background(color = data.color, shape = RoundedCornerShape(10.dp))
                    .padding(8.dp)
            ) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_notebook),
                    tint = Color.White,
                    contentDescription = "Notebook icon"
                )
            }
        }
    }
}


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