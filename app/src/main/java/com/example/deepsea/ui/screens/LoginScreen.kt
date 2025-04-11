package com.example.deepsea.ui.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import com.example.deepsea.ui.components.DeepSeaButton


@Preview(showBackground = true)
@Composable
fun LoginPage(){
    DeepSeaButton(
        onClick = {}, shape = RectangleShape
    ) {
        Text(text = "Demo")
    }
}