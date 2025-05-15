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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.deepsea.R
import com.example.deepsea.text.PrimaryText
import com.example.deepsea.text.TitleText
import com.example.deepsea.ui.components.LockedStarDialog
import com.example.deepsea.ui.components.SectionData
import com.example.deepsea.ui.components.SelectableStarButton
import com.example.deepsea.ui.components.StarButton
import com.example.deepsea.ui.components.StarDialog
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
    navController: NavController, // Add NavController parameter
    onJumpToSection: (sectionIndex: Int, unitIndex: Int) -> Unit,
    onStarClicked: (coordinateInRoot: Float, isInteractive: Boolean, unitId: Long, starIndex: Int) -> Unit, // Updated signature
    onGuideBookClicked: (unitId: Long) -> Unit,
    onStarComplete: (unitId: Long, starIndex: Int) -> Unit // Add completion callback
) {
    // Dialog state variables
    var isDialogShown by remember { mutableStateOf(false) }
    var selectedUnitId by remember { mutableLongStateOf(-1L) }
    var selectedStarIndex by remember { mutableIntStateOf(-1) }
    var dialogTransition by remember { mutableFloatStateOf(0f) }
    var isDialogInteractive by remember { mutableStateOf(false) }

    val isLastSection = sectionIndex == totalSectionCount - 1
    val sortedUnits = units.sortedBy {
        "\\d+".toRegex().find(it.title)?.value?.toIntOrNull() ?: Int.MAX_VALUE
    }

    Box(modifier = Modifier.fillMaxSize()) {
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
                        // Store the clicked star information
                        selectedUnitId = unit.id
                        selectedStarIndex = starIndex
                        isDialogInteractive = isInteractive
                        dialogTransition = coordinateInRoot
                        isDialogShown = true

                        // Also call the original handler for any other side effects
                        onStarClicked(coordinateInRoot, isInteractive, unit.id, starIndex)
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

        // Show dialog when a star is clicked
        if (isDialogShown) {
            // Calculate XP based on star index (you can adjust this formula)
            val xpAmount = (selectedStarIndex + 1) * 10

            // Show the dialog without the overlay background
            StarDialog(
                isDialogShown = isDialogShown,
                isDialogInteractive = isDialogInteractive,
                dialogTransition = dialogTransition,
                navController = navController,
                xpAmount = xpAmount,
                unitId = selectedUnitId,
                starIndex = selectedStarIndex,
                onDismiss = { isDialogShown = false }, // Add dismiss callback
                onStarComplete = {
                    // Call the completion callback
                    onStarComplete(selectedUnitId, selectedStarIndex)
                    // Hide the dialog
                    isDialogShown = false
                }
            )
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
    completedStars: Set<Int>,
    unitId: Long,
    onStarClicked: (coordinateInRoot: Float, isInteractive: Boolean, starIndex: Int) -> Unit,
) {
    // Add state for the locked dialog
    var showLockedDialog by remember { mutableStateOf(false) }

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

                    // Determine star status
                    val isCompleted = starIndex in completedStars

                    // Make early lessons (first 3 stars in each unit) always clickable
                    val isEarlyLesson = starIndex < 1

                    val isNextUnlocked = if (completedStars.isEmpty()) starIndex == 0 && unitIndex == 0
                    else starIndex <= completedStars.maxOrNull()?.plus(1) ?: 0

                    // Consider early lessons as interactive
                    val isInteractive = isCompleted || isNextUnlocked || isEarlyLesson

                    // Use SelectableStarButton for completed stars, first star of first unit, or early lessons
                    if (isCompleted || (starIndex == 0 && unitIndex == 0) || isEarlyLesson) {
                        SelectableStarButton(
                            isInitial = unitIndex == 0 && starIndex == 0 && !isCompleted,
                            colorMain = if (isCompleted) colorMain else if (isInteractive) colorMain.copy(alpha = 0.8f) else Color.Gray,
                            colorDark = if (isCompleted) colorDark else if (isInteractive) colorDark.copy(alpha = 0.8f) else Color.DarkGray,
                            onStarClicked = { coordinateInRoot, _ ->
                                // Pass actual interactive status
                                onStarClicked(coordinateInRoot, isInteractive, starIndex)
                            }
                        )
                    } else {
                        // Use a regular StarButton for other stars
                        StarButton(
                            isCompleted = isCompleted,
                            isUnlocked = isNextUnlocked,
                            colorMain = if (isCompleted) colorMain else if (isNextUnlocked) colorMain.copy(alpha = 0.7f) else Color.Gray,
                            colorDark = if (isCompleted) colorDark else if (isNextUnlocked) colorDark.copy(alpha = 0.7f) else Color.DarkGray,
                            onStarClicked = { coordinateInRoot, _ ->
                                onStarClicked(coordinateInRoot, isInteractive, starIndex)
                            },
                            onLockedStarClicked = {
                                // Show the locked dialog when a locked star is clicked
                                showLockedDialog = true
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

        // Display locked dialog when needed
        LockedStarDialog(
            isVisible = showLockedDialog,
            onDismiss = { showLockedDialog = false }
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

