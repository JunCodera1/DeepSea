package com.example.deepsea.ui.screens.feature

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.foundation.gestures.detectTapGestures
import com.example.deepsea.R
import com.example.deepsea.ui.components.TopBar
import com.example.deepsea.ui.components.UnitData
import kotlinx.coroutines.launch

/**
 * Màn hình chi tiết đơn vị học tập
 * Hiển thị khi người dùng nhấn vào phần header trong TopBar
 * Hỗ trợ vuốt ngang để xem các unit khác nhau
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun UnitDetailScreen(
    unit: UnitData,
    progress: Int = 0,
    totalUnits: Int = 10,
    onContinueClick: () -> Unit,
    onSeeDetailsClick: () -> Unit,
    navController: NavController,
    units: List<UnitData> = listOf(unit)
) {
    val coroutineScope = rememberCoroutineScope()

    // Tìm index của unit hiện tại trong danh sách units
    val initialPage = units.indexOf(unit).takeIf { it >= 0 } ?: 0

    // Thiết lập pager state để vuốt ngang giữa các unit
    val pagerState = rememberPagerState(
        initialPage = initialPage,
        pageCount = { units.size }
    )

    // Thiết lập UI với Scaffold để giữ lại TopBar
    Scaffold(
        topBar = {
            TopBar(
                units = units,
                visibleUnitIndex = pagerState.currentPage,
                navController = navController,
                onExpandClick = onContinueClick, // Sử dụng onContinueClick để quay lại màn hình chính khi nhấn lại
                isExpanded = true // Trạng thái mở rộng trong UnitDetailScreen
            )
        }
    ) { paddingValues ->
        // Horizontal Pager cho phép vuốt ngang giữa các unit
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) { page ->
            val currentUnit = units.getOrNull(page) ?: unit

            // Content với phần đệm để không che khuất TopBar
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(currentUnit.color)
            ) {
                // Mascot image at the top
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp), // Giảm chiều cao vì đã có TopBar ở trên
                    contentAlignment = Alignment.Center
                ) {
                    // Add your mascot image here
                    Image(
                        painter = painterResource(id = R.drawable.ic_bag), // Thay thế bằng resource hình ảnh thực tế
                        contentDescription = "Mascot",
                        modifier = Modifier.size(200.dp),
                        contentScale = ContentScale.Fit
                    )

                    // Add dandelions or decorations around
                    // This is just a placeholder - add your actual decorations
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.7f))
                            .align(Alignment.TopEnd)
                            .offset(x = (-50).dp, y = 70.dp)
                    )
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.5f))
                            .align(Alignment.CenterStart)
                            .offset(x = 70.dp, y = (-30).dp)
                    )
                }

                // Content card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 16.dp, vertical = 24.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = currentUnit.color.copy(alpha = 0.8f))
                ) {
                    Column(
                        modifier = Modifier
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Section title - sử dụng title của unit hiện tại
                        Text(
                            text = currentUnit.title,
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Progress indicator - hiển thị trang hiện tại trong tổng số unit
                        Text(
                            text = "${page + 1} / ${units.size} UNITS",
                            color = Color.White,
                            fontSize = 16.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Progress bar
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color.White.copy(alpha = 0.3f))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth((page + 1).toFloat() / units.size)
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color.White)
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // "See Details" button
                        Button(
                            onClick = onSeeDetailsClick,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = currentUnit.color
                            ),
                            shape = RoundedCornerShape(32.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "A1 • SEE DETAILS",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // "Continue" button - thay đổi thành "JUMP HERE" theo hình ảnh
                        Button(
                            onClick = onContinueClick,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White
                            ),
                            shape = RoundedCornerShape(32.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "JUMP HERE",
                                color = currentUnit.color,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Pagination dots
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            repeat(units.size) { index ->
                                Box(
                                    modifier = Modifier
                                        .padding(horizontal = 4.dp)
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (index == pagerState.currentPage) Color.White
                                            else Color.White.copy(alpha = 0.5f)
                                        )
                                        .pointerInput(Unit) {
                                            detectTapGestures {
                                                coroutineScope.launch {
                                                    pagerState.animateScrollToPage(index)
                                                }
                                            }
                                        }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}