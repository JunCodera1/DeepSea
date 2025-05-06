package com.example.deepsea.ui.screens.feature.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
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
    onStarClicked: (coordinateInRoot: Float, isInteractive: Boolean) -> Unit
) {
    val isLastSection = sectionIndex == totalSectionCount - 1
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
        // Replace the old Button block with this new one
        if(!isLastSection)
        item {
            Divider(color = Color.LightGray, thickness = 1.dp)
            Spacer(modifier = Modifier.height(24.dp))
            // Title above button
            Text(
                text = sections[sectionIndex].title,
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = sections[sectionIndex].description,
                color = Color.Gray,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                fontWeight = FontWeight.Normal
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Duolingo-style "JUMP HERE?" button
            Button(
                onClick = { onJumpToSection(sectionIndex, 0)
                          },
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