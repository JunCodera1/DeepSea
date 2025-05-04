package com.example.deepsea.ui.screens.feature

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.deepsea.ui.components.UnitContent
import com.example.deepsea.ui.components.UnitData
import com.example.deepsea.ui.components.UnitHeader

@Composable
fun UnitsListScreen(
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