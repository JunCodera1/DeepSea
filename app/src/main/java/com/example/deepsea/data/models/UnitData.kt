package com.example.deepsea.data.models

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.example.deepsea.R
import com.example.deepsea.ui.theme.FeatherGreen
import com.example.deepsea.ui.theme.FeatherGreenDark

@Immutable
class UnitData {
    val color: Color = FeatherGreen
    val darkerColor: Color = FeatherGreenDark
    val title: String = "Unit 1"
    val description: String = "Make introductions"
    val image : Int = R.drawable.ic_duo_main
}