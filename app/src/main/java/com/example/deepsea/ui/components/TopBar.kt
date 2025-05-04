@file:Suppress("DEPRECATION")

package com.example.deepsea.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.deepsea.R
import com.example.deepsea.text.TitleText
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import java.time.LocalDate
import java.time.YearMonth


// Sửa đổi cho TopBar.kt
// Cập nhật TopBar để đổi hướng mũi tên khi mở rộng header
@Composable
fun TopBar(
    units: List<UnitData> = listOf(UnitData()),
    sectionData: SectionData,
    visibleUnitIndex: Int = 0,
    navController: NavController,
    onExpandClick: () -> Unit = {}, // Callback cho sự kiện click
    isExpanded: Boolean = false, // Trạng thái mở rộng
    sections: List<SectionData> = listOf(sectionData)
) {
    val coroutineScope = rememberCoroutineScope()

    // Find index of current section in the sections list
    val initialPage = sections.indexOf(sectionData).takeIf { it >= 0 } ?: 0

    // Set up pager state for horizontal swiping between sections
    val pagerState = rememberPagerState(
        initialPage = initialPage,
        pageCount = { sections.size }
    )

    // Keep track of which tab is selected (overview or units)
    var selectedTab by remember { mutableStateOf(0) }

    val systemUiController = rememberSystemUiController()
    val showStreakDialog = remember { mutableStateOf(false) }


    val currentUnit = units.getOrNull(visibleUnitIndex)
    val animatedColor by animateColorAsState(
        targetValue = currentUnit?.color ?: Color.Gray,
        animationSpec = tween(durationMillis = 600)
    )
    val darkerColor = currentUnit?.darkerColor ?: Color.DarkGray

    systemUiController.setStatusBarColor(animatedColor)
    systemUiController.setNavigationBarColor(Color.White)

    Box(
        modifier = Modifier
            .zIndex(10f)
            .drawBehind {
                drawRect(color = animatedColor)
                drawRect(
                    color = darkerColor,
                    topLeft = Offset(x = 0f, y = size.height),
                    size = Size(width = size.width, height = 2.dp.toPx())
                )
            }
            .padding(horizontal = 12.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                BarIcon(R.drawable.flag_japan)
                if (showStreakDialog.value) {
                    navController.navigate("home/streak")
                }

                BarIcon(
                    icon = R.drawable.ic_fire,
                    text = "1",
                    onClick = { showStreakDialog.value = true }
                )

                BarIcon(R.drawable.ic_gem, "505")
                BarIcon(R.drawable.ic_heart, "5")
            }

            Box(
                modifier = Modifier.clickable { onExpandClick() } // Thêm clickable
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    TitleText(text = sections[pagerState.currentPage].title, color = Color.White, fontSize = 18.sp)
                }

                // Sử dụng mũi tên lên hoặc xuống tùy thuộc vào trạng thái mở rộng
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                    tint = Color.White,
                    contentDescription = if (isExpanded) "down" else "up",
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }
        }
    }
}


@Composable
fun BarIcon(@DrawableRes icon: Int, text: String? = null, saturation: Float = 1f, onClick: (() -> Unit)? = null) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(8.dp)
            .clickable(enabled = onClick != null) { onClick?.invoke() }
    ) {
        Image(
            modifier = Modifier.size(24.dp),
            painter = painterResource(id = icon),
            contentDescription = null,
            colorFilter = ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(saturation) })
        )
        text?.let {
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = it, color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

