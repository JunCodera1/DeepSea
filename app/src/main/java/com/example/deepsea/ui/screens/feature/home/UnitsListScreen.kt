package com.example.deepsea.ui.screens.feature.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.deepsea.ui.components.SectionData
import com.example.deepsea.ui.components.UnitContent
import com.example.deepsea.ui.components.UnitData
import com.example.deepsea.ui.components.UnitHeader

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
    onJumpToSection: (sectionIndex: Int, unitIndex: Int) -> Unit,
    onStarClicked: (coordinateInRoot: Float, isInteractive: Boolean, unitId: Long) -> Unit, // Updated signature
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
                onStarClicked = { coordinateInRoot, isInteractive ->
                    onStarClicked(coordinateInRoot, isInteractive, unit.id) // Pass unit.id
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