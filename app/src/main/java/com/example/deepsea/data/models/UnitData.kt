package com.example.deepsea.data.models

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.example.deepsea.R
import com.example.deepsea.ui.theme.FeatherGreen
import com.example.deepsea.ui.theme.FeatherGreenDark

// Update your UnitData class to accept parameters:
@Immutable
data class UnitData(
    val title: String = "Unit 1",
    val color: Color = FeatherGreen,
    val darkerColor: Color = FeatherGreenDark,
    val description: String = "Make introductions",
    val image: Int = R.drawable.ic_duo_main
)