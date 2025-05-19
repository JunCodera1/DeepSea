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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.example.deepsea.R
import com.example.deepsea.text.TitleText
import com.google.accompanist.systemuicontroller.rememberSystemUiController


// Sửa đổi cho TopBar.kt
// Cập nhật TopBar để đổi hướng mũi tên khi mở rộng header
@Composable
fun TopBar(
    sectionIndex: Int,
    units: List<UnitData> = listOf(UnitData()),
    sectionData: SectionData,
    visibleUnitIndex: Int = 0,
    navController: NavController,
    onExpandClick: () -> Unit = {}, // Callback for expand click
    isExpanded: Boolean = false, // Expanded state
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
    val showGemsDialog = remember { mutableStateOf(false) } // State for gems navigation
    val showHeartsDialog = remember { mutableStateOf(false) } // State for hearts navigation
    var expanded by remember { mutableStateOf(false) }

    val currentUnit = units.getOrNull(visibleUnitIndex)
    val animatedColor by animateColorAsState(
        targetValue = currentUnit?.color ?: Color.Gray,
        animationSpec = tween(durationMillis = 600)
    )
    val darkerColor = currentUnit?.darkerColor ?: Color.DarkGray

    systemUiController.setStatusBarColor(animatedColor)
    systemUiController.setNavigationBarColor(Color.White)

    // Handle navigation for streak, gems, and hearts
    LaunchedEffect(showStreakDialog.value, showGemsDialog.value, showHeartsDialog.value) {
        when {
            showStreakDialog.value -> {
                navController.navigate("home/streak")
                showStreakDialog.value = false
            }
            showGemsDialog.value -> {
                navController.navigate("home/gems")
                showGemsDialog.value = false
            }
            showHeartsDialog.value -> {
                navController.navigate("home/hearts")
                showHeartsDialog.value = false
            }
        }
    }

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
                Box(
                    modifier = Modifier
                        .wrapContentSize(Alignment.TopStart)
                ) {
                    BarIcon(
                        icon = R.drawable.flag_japan,
                        onClick = { expanded = true }
                    )

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.background(Color.White)
                    ) {
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Image(
                                        painter = painterResource(id = R.drawable.flag_japan),
                                        contentDescription = null,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Japanese", fontWeight = FontWeight.Bold)
                                }
                            },
                            onClick = {
                                expanded = false
                            }
                        )

                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = null,
                                        tint = Color.Gray
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Add Course", color = Color.Gray)
                                }
                            },
                            onClick = {},
                            enabled = false
                        )
                    }
                }

                BarIcon(
                    icon = R.drawable.ic_fire,
                    text = "1",
                    onClick = { showStreakDialog.value = true }
                )

                BarIcon(
                    icon = R.drawable.ic_gem,
                    text = "505",
                    onClick = { showGemsDialog.value = true } // Navigate to gems screen
                )

                BarIcon(
                    icon = R.drawable.ic_heart,
                    text = "5",
                    onClick = { showHeartsDialog.value = true } // Navigate to hearts screen
                )
            }

            Box(
                modifier = Modifier.clickable { onExpandClick() }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    TitleText(text = sections[sectionIndex].title, color = Color.White, fontSize = 18.sp)
                }

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

@Composable
fun LanguageDropdown() {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .wrapContentSize(Alignment.TopStart)
    ) {
        // Custom button that shows the flag and triggers dropdown
        Box(
            modifier = Modifier
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(8.dp)
                .clickable { expanded = true }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(Color.Red, CircleShape)
                )
            }
        }

        // Dropdown menu
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(Color.White)
                .width(200.dp)
        ) {
            // Japanese option
            DropdownMenuItem(
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(Color.Red, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Japanese", fontWeight = FontWeight.Bold)
                    }
                },
                onClick = {
                    // Handle selection
                    expanded = false
                }
            )

            // Divider
            Divider(color = Color.LightGray, thickness = 1.dp)

            // Add Course option (disabled)
            DropdownMenuItem(
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Add Course", color = Color.Gray)
                    }
                },
                onClick = { },
                enabled = false
            )
        }

        // Text below the flag button
        Text(
            text = "Japanese",
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(top = 45.dp),
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
    }
}

@Composable
fun CourseButton() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Course",
                tint = Color.Gray,
                modifier = Modifier.size(28.dp)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Course",
            color = Color.Gray,
            fontSize = 14.sp
        )
    }
}

@Composable
fun LanguageSelector() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        LanguageDropdown()

        Spacer(modifier = Modifier.width(24.dp))

        CourseButton()
    }
}
