package com.example.deepsea.ui.screens.feature.home

import android.util.Log
import androidx.annotation.DrawableRes
import androidx.compose.foundation.*
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
import com.example.deepsea.ui.components.*
import com.example.deepsea.ui.viewmodel.home.HomeViewModel

@Composable
fun UnitsListScreen(
    modifier: Modifier = Modifier,
    totalSectionCount: Int,
    section: SectionData,
    sections: List<SectionData>,
    state: LazyListState,
    sectionIndex: Int,
    units: List<UnitData>,
    starCountPerUnit: Int,
    completedStars: Map<Long, Set<Int>>,
    homeViewModel: HomeViewModel,
    navController: NavController,
    onJumpToSection: (sectionIndex: Int, unitIndex: Int) -> Unit,
    onGuideBookClicked: (unitId: Long) -> Unit,
    onStarClicked: (coordinateInRoot: Float, isInteractive: Boolean, unitId: Long, starIndex: Int) -> Unit,
    onStarComplete: (unitId: Long, starIndex: Int) -> Unit
) {
    Log.d("UnitsListScreen", "Completed stars: $completedStars")

    var isDialogShown by remember { mutableStateOf(false) }
    var selectedUnitId by remember { mutableLongStateOf(-1L) }
    var selectedStarIndex by remember { mutableIntStateOf(-1) }
    var dialogTransition by remember { mutableFloatStateOf(0f) }
    var isDialogInteractive by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            state = state,
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(units.size) { index ->
                val unit = units[index]
                UnitHeader(
                    modifier = Modifier.fillMaxWidth(),
                    data = unit,
                    onGuideBookClicked = { onGuideBookClicked(unit.id) }
                )
                Spacer(modifier = Modifier.height(48.dp))
                UnitContent(
                    unitIndex = index,
                    starCount = starCountPerUnit,
                    unitImage = unit.image,
                    colorMain = unit.color,
                    colorDark = unit.darkerColor,
                    completedStars = completedStars[unit.id] ?: emptySet(),
                    unitId = unit.id,
                    onStarClicked = { coordinateInRoot, isInteractive, starIndex ->
                        selectedUnitId = unit.id
                        selectedStarIndex = starIndex
                        isDialogInteractive = isInteractive
                        dialogTransition = coordinateInRoot
                        isDialogShown = true
                        onStarClicked(coordinateInRoot, isInteractive, unit.id, starIndex)
                    }
                )
            }
            if (sectionIndex < totalSectionCount - 1) {
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
                        modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
                        fontWeight = FontWeight.Normal
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { onJumpToSection(sectionIndex + 1, 0) },
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color(0xFF3399FF)
                        ),
                        border = BorderStroke(2.dp, Color(0xFF3399FF)),
                        modifier = Modifier.width(200.dp).height(48.dp)
                    ) {
                        Text(text = "JUMP HERE?", fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Divider(color = Color.LightGray, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }

        if (isDialogShown) {
            val xpAmount = (selectedStarIndex + 1) * 10
            StarDialog(
                isDialogShown = isDialogShown,
                isDialogInteractive = isDialogInteractive,
                dialogTransition = dialogTransition,
                navController = navController,
                xpAmount = xpAmount,
                unitId = selectedUnitId,
                starIndex = selectedStarIndex,
                onDismiss = { isDialogShown = false },
                onStarComplete = {
                    onStarComplete(selectedUnitId, selectedStarIndex)
                    isDialogShown = false
                }
            )
        }
    }
}

@Composable
fun UnitContent(
    unitIndex: Int,
    starCount: Int,
    @DrawableRes unitImage: Int,
    colorMain: Color,
    colorDark: Color,
    completedStars: Set<Int>,
    unitId: Long,
    onStarClicked: (coordinateInRoot: Float, isInteractive: Boolean, starIndex: Int) -> Unit
) {
    var showLockedDialog by remember { mutableStateOf(false) }

    Box {
        Column(verticalArrangement = Arrangement.spacedBy(32.dp)) {
            repeat(starCount) { starIndex ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    val alignPercentage = orderToPercentage(starIndex, unitIndex % 2 == 0)
                    Spacer(modifier = Modifier.fillMaxWidth(alignPercentage))

                    val isCompleted = starIndex in completedStars
                    val isNextUnlocked = starIndex <= completedStars.size // Unlock next star
                    val isInteractive = isCompleted || isNextUnlocked

                    Log.d("UnitContent", "Unit $unitId, Star $starIndex: isCompleted=$isCompleted, isNextUnlocked=$isNextUnlocked, completedStars=$completedStars")

                    if (starIndex == 0 && unitIndex == 0 && completedStars.isEmpty()) {
                        SelectableStarButton(
                            isInitial = true,
                            colorMain = colorMain,
                            colorDark = colorDark,
                            onStarClicked = { coordinateInRoot, _ ->
                                onStarClicked(coordinateInRoot, true, starIndex)
                            }
                        )
                    } else {
                        StarButton(
                            isCompleted = isCompleted,
                            isUnlocked = isNextUnlocked,
                            onStarClicked = { coordinateInRoot, _ ->
                                onStarClicked(coordinateInRoot, isInteractive, starIndex)
                            },
                            onLockedStarClicked = { showLockedDialog = true }
                        )
                    }
                }
            }
        }

        Image(
            modifier = Modifier.size(210.dp).padding(40.dp).align(
                alignment = if (unitIndex % 2 == 0) Alignment.CenterEnd else Alignment.CenterStart
            ),
            painter = painterResource(id = unitImage),
            contentDescription = "Unit image"
        )

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
        modifier = modifier.background(data.color).padding(horizontal = 12.dp).padding(top = 24.dp, bottom = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            TitleText(text = data.title, color = Color.White)
            Spacer(modifier = Modifier.height(12.dp))
            PrimaryText(text = data.description, color = Color.White, fontSize = 18.sp)
        }
        Box(
            modifier = Modifier.clickable { onGuideBookClicked() }
                .background(color = data.darkerColor, shape = RoundedCornerShape(10.dp))
                .padding(horizontal = 1.5.dp).padding(top = 1.5.dp, bottom = 3.dp)
        ) {
            Box(
                modifier = Modifier.background(color = data.color, shape = RoundedCornerShape(10.dp)).padding(8.dp)
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