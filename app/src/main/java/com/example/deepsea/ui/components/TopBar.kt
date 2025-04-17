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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.deepsea.R
import com.example.deepsea.text.TitleText
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import java.time.LocalDate
import java.time.YearMonth


@Composable
@Preview
fun TopBar(units: List<UnitData> = listOf(UnitData()), visibleUnitIndex: Int = 0) {
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
                    .padding(top = 60.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                BarIcon(R.drawable.flag_japan)
                if (showStreakDialog.value) {
                    AlertDialog(
                        onDismissRequest = { showStreakDialog.value = false },
                        confirmButton = {},
                        title = { Text("Streak Calendar") },
                        text = {
                            StreakCalendar()
                        }
                    )
                }

                BarIcon(
                    icon = R.drawable.ic_fire,
                    text = "1",
                    onClick = { showStreakDialog.value = true }
                )

                BarIcon(R.drawable.ic_gem, "505")
                BarIcon(R.drawable.ic_heart, "5")
            }

            Box {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    TitleText(text = "Section 1: Rookie", color = Color.White, fontSize = 18.sp)
                }

                Icon(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    tint = Color.White,
                    contentDescription = "up"
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
fun StreakCalendar() {
    val today = LocalDate.now()
    val daysInMonth = YearMonth.now().lengthOfMonth()

    Column {
        Text("Tháng ${today.monthValue}/${today.year}", fontWeight = FontWeight.Bold)

        // Dòng header
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
            listOf("S", "M", "T", "W", "T", "F", "S").forEach {
                Text(it, fontSize = 14.sp)
            }
        }

        // Hiển thị ngày
        val firstDayOfWeek = YearMonth.now().atDay(1).dayOfWeek.value % 7
        var dayCount = 1

        for (week in 0..5) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                for (dayOfWeek in 0..6) {
                    if (week == 0 && dayOfWeek < firstDayOfWeek || dayCount > daysInMonth) {
                        Text("", modifier = Modifier.size(24.dp))
                    } else {
                        val isToday = dayCount == today.dayOfMonth
                        Text(
                            text = "$dayCount",
                            modifier = Modifier
                                .size(28.dp)
                                .background(
                                    if (isToday) Color(0xFFFFC107) else Color.Transparent,
                                    shape = CircleShape
                                ),
                            color = if (isToday) Color.White else Color.Black,
                            textAlign = TextAlign.Center
                        )
                        dayCount++
                    }
                }
            }
        }
    }
}

