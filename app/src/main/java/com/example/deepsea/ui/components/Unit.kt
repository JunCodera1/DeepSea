package com.example.deepsea.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.deepsea.ui.theme.FeatherGreen
import com.example.deepsea.ui.theme.FeatherGreenDark
import com.example.deepsea.R
import com.example.deepsea.text.PrimaryText
import com.example.deepsea.text.TitleText


@Composable
fun UnitContent(
    unitIndex: Int,
    colorMain : Color,
    colorDark : Color,
    @DrawableRes unitImage : Int,
    starCount: Int,
    onStarClicked : (coordinateInRoot : Float, isInteractive : Boolean) -> Unit
) {
    Box {
        Column (
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            repeat(starCount) { starIndex ->
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val alignPercentage = remember {
                        orderToPercentage(starIndex, unitIndex % 2 == 0)
                    }
                    Spacer(modifier = Modifier.fillMaxWidth(alignPercentage))
                    if (starIndex == 0) SelectableStarButton(
                        isInitial = unitIndex == 0,
                        colorMain = colorMain,
                        colorDark = colorDark,
                        onStarClicked = onStarClicked
                    )
                    else StarButton(onStarClicked)
                }
            }
        }

        Image(
            modifier = Modifier
                .size(200.dp)
                .align(alignment = if (unitIndex % 2 == 0) Alignment.CenterEnd else Alignment.CenterStart),
            painter = painterResource(id = unitImage), // dùng tài nguyên local (@DrawableRes)
            colorFilter = ColorFilter.colorMatrix(
                colorMatrix = ColorMatrix().apply {
                    setToSaturation(0f)
                }
            ),
            contentDescription = "duo"
        )
    }
}

@Composable
fun UnitsLazyColumn(
    modifier : Modifier,
    state: LazyListState,
    units: List<UnitData>,
    starCountPerUnit: Int,
    onStarClicked : (coordinateInRoot : Float, isInteractive : Boolean) -> Unit
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize(),
        state = state,
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        units.forEachIndexed { unitIndex, unit ->
            item {
                UnitHeader(
                    modifier = Modifier.fillMaxWidth(),
                    data = unit
                )
                Spacer(modifier = Modifier.height(48.dp))
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
        item {
            Spacer(modifier = Modifier.height(400.dp))
        }
    }
}

@Composable
@Preview
fun UnitHeader(
    modifier: Modifier = Modifier,
    data: UnitData = UnitData()
) {
    Row(
        modifier = modifier
            .background(data.color)
            .padding(horizontal = 12.dp)
            .padding(top = 24.dp, bottom = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            TitleText(
                text = data.title,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(12.dp))
            PrimaryText(
                text = data.description,
                color = Color.White,
                fontSize = 18.sp
            )
        }
        Box (
            modifier = Modifier
                .background(color = data.darkerColor, shape = RoundedCornerShape(10.dp))
                .padding(horizontal = 1.5.dp)
                .padding(top = 1.5.dp, bottom = 3.dp)
        ) {
            Box (
                modifier = Modifier
                    .background(color = data.color, shape = RoundedCornerShape(10.dp))
                    .padding(8.dp)
            ) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_notebook),
                    tint = Color.White,
                    contentDescription = "more"
                )
            }
        }
    }
}

@Immutable
data class UnitData(
    val title: String = "Unit 1",
    val color: Color = FeatherGreen,
    val darkerColor: Color = FeatherGreenDark,
    val description: String = "Make introductions",
    val image: Int = R.drawable.ic_booking
)



/**
 * Tính toán phần trăm căn lề ngang cho mỗi star trong một unit.
 *
 * @param order Thứ tự của ngôi sao (bắt đầu từ 0)
 * @param alignRight Nếu true thì star được căn từ bên phải (dành cho unit chẵn), ngược lại căn từ bên trái
 * @return Giá trị Float từ 0f đến 1f để dùng với fillMaxWidth()
 */
fun orderToPercentage(order: Int, alignRight: Boolean): Float {
    val step = 0.15f // khoảng cách giữa các star theo chiều ngang
    val percentage = order * step
    return if (alignRight) 1f - percentage else percentage
}

