package com.example.deepsea.ui.screens.feature.home

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.deepsea.R
import com.example.deepsea.text.PrimaryText
import com.example.deepsea.text.TitleText
import com.example.deepsea.ui.components.SectionData
import com.example.deepsea.ui.components.SelectableStarButton
import com.example.deepsea.ui.components.StarButton
import com.example.deepsea.ui.components.UnitData

@Composable
fun UnitsListScreen(
    totalSectionCount: Int,
    section: SectionData,
    sections: List<SectionData> = listOf(section),
    modifier: Modifier,
    state: LazyListState,
    sectionIndex: Int,
    units: List<UnitData>,
    starCountPerUnit: Int,
    completedStars: Map<Long, Set<Int>>, // Add completed stars parameter
    onJumpToSection: (sectionIndex: Int, unitIndex: Int) -> Unit,
    onStarClicked: (coordinateInRoot: Float, isInteractive: Boolean, unitId: Long, starIndex: Int) -> Unit, // Updated signature
    onGuideBookClicked: (unitId: Long) -> Unit
) {
    val isLastSection = sectionIndex == totalSectionCount - 1
    val sortedUnits = units.sortedBy {
        "\\d+".toRegex().find(it.title)?.value?.toIntOrNull() ?: Int.MAX_VALUE
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        state = state,
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(sortedUnits.size) { index ->
            val unit = sortedUnits[index]
            UnitHeader(
                modifier = Modifier.fillMaxWidth(),
                data = unit,
                onGuideBookClicked = {
                    onGuideBookClicked(unit.id)
                }
            )

            Spacer(modifier = Modifier.height(48.dp))

            UnitContent(
                unitIndex = index,
                starCount = starCountPerUnit,
                unitImage = unit.image,
                colorMain = unit.color,
                colorDark = unit.darkerColor,
                completedStars = completedStars[unit.id] ?: emptySet(), // Pass completed stars for this unit
                unitId = unit.id,
                onStarClicked = { coordinateInRoot, isInteractive, starIndex ->
                    onStarClicked(coordinateInRoot, isInteractive, unit.id, starIndex) // Pass starIndex
                }
            )
        }

        if (!isLastSection) {
            item {
                Divider(color = Color.LightGray, thickness = 1.dp)
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = sections[sectionIndex + 1].title,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = sections[sectionIndex + 1].description,
                    color = Color.Gray,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(),
                    fontWeight = FontWeight.Normal
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { onJumpToSection(sectionIndex, 0) },
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF3399FF)
                    ),
                    border = BorderStroke(2.dp, Color(0xFF3399FF)),
                    modifier = Modifier
                        .width(200.dp)
                        .height(48.dp)
                ) {
                    Text(
                        text = "JUMP HERE?",
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
                Divider(color = Color.LightGray, thickness = 1.dp)
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun UnitContent(
    unitIndex: Int,
    colorMain: Color,
    colorDark: Color,
    @DrawableRes unitImage: Int,
    starCount: Int,
    completedStars: Set<Int>, // Completed stars parameter
    unitId: Long,
    onStarClicked: (coordinateInRoot: Float, isInteractive: Boolean, starIndex: Int) -> Unit,
) {
    Box {
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

                    // Determine if this star is completed or unlocked
                    val isCompleted = starIndex in completedStars
                    val isNextUnlocked = if (completedStars.isEmpty()) starIndex == 0 && unitIndex == 0
                    else starIndex <= completedStars.maxOrNull()?.plus(1) ?: 0
                    val isInteractive = isCompleted || isNextUnlocked

                    // Use SelectableStarButton for completed stars or the first star of first unit
                    if (isCompleted || (starIndex == 0 && unitIndex == 0 && isInteractive)) {
                        SelectableStarButton(
                            isInitial = unitIndex == 0 && starIndex == 0 && !isCompleted,
                            colorMain = if (isCompleted) colorMain else if (isNextUnlocked) colorMain.copy(alpha = 0.7f) else Color.Gray,
                            colorDark = if (isCompleted) colorDark else if (isNextUnlocked) colorDark.copy(alpha = 0.7f) else Color.DarkGray,
                            onStarClicked = { coordinateInRoot, interactive ->
                                onStarClicked(coordinateInRoot, isInteractive, starIndex)
                            }
                        )
                    } else {
                        // Use a regular StarButton for uncompleted stars
                        StarButton(
                            isCompleted = isCompleted,
                            isUnlocked = isNextUnlocked,
                            colorMain = if (isCompleted) colorMain else if (isNextUnlocked) colorMain.copy(alpha = 0.7f) else Color.Gray,
                            colorDark = if (isCompleted) colorDark else if (isNextUnlocked) colorDark.copy(alpha = 0.7f) else Color.DarkGray,
                            onStarClicked = { coordinateInRoot, _ ->
                                onStarClicked(coordinateInRoot, isInteractive, starIndex)
                            }
                        )
                    }
                }
            }
        }

        // Background unit image
        Image(
            modifier = Modifier
                .size(210.dp).padding(40.dp)
                .align(
                    alignment = if (unitIndex % 2 == 0) Alignment.CenterEnd else Alignment.CenterStart
                ),
            painter = painterResource(id = unitImage),
            contentDescription = "Unit image"
        )
    }
}
@Composable
fun UnitHeader(
    modifier: Modifier = Modifier,
    data: UnitData = UnitData(),
    onGuideBookClicked: () -> Unit = {}
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

        // Notebook icon with 3D effect using nested boxes - now clickable
        Box(
            modifier = Modifier
                .clickable { onGuideBookClicked() } // Make sure clickable is imported
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