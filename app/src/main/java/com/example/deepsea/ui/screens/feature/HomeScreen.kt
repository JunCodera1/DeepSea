package com.example.deepsea.ui.screens.feature

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.ui.unit.max
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.deepsea.ui.components.StarDialog
import com.example.deepsea.ui.components.TopBar
import com.example.deepsea.ui.components.UnitData
import com.example.deepsea.ui.components.UnitsLazyColumn
import com.example.deepsea.ui.theme.FeatherGreen
import kotlinx.coroutines.launch

/**
 * HomeScreen là màn hình chính của ứng dụng DeepSea
 * Hiển thị danh sách các đơn vị học tập với các ngôi sao tương tác
 * Hỗ trợ vuốt ngang để chuyển giữa các section
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(sections: List<List<UnitData>> = emptyList(), navController: NavController) {
    val defaultSections = remember {
        if (sections.isEmpty()) {
            listOf(
                listOf(
                    UnitData(title = "Section 1: Rookie", color = FeatherGreen),
                    UnitData(title = "Unit 2", color = Color.Red, darkerColor = Color.Red),
                    UnitData(title = "Unit 3", color = Color.Yellow)
                ),
                listOf(
                    UnitData(title = "Section 2: Explorer", color = Color.Gray),
                    UnitData(title = "Unit 5", color = Color.Magenta)
                ),
                listOf(
                    UnitData(title = "Section 3: Traveler", color = Color.Blue),
                    UnitData(title = "Unit 7", color = Color.Cyan)
                )
            )
        } else {
            sections
        }
    }

    // Quản lý trạng thái pager
    val pagerState = rememberPagerState(pageCount = { defaultSections.size })
    val coroutineScope = rememberCoroutineScope()
    val starCountPerUnit = 5

    // Trạng thái dialog
    var isDialogShown by remember { mutableStateOf(false) }
    var isDialogInteractive by remember { mutableStateOf(false) }
    var dialogTransition by remember { mutableStateOf(0f) }
    var rootHeight by remember { mutableStateOf(0f) }

    // Trạng thái mở rộng header (hiển thị màn hình chi tiết)
    var isHeaderExpanded by remember { mutableStateOf(false) }
    var selectedSectionIndex by remember { mutableStateOf(0) }
    var selectedUnitIndex by remember { mutableStateOf(0) }

    // Hiển thị giao diện tương ứng với trạng thái
    if (isHeaderExpanded) {
        val currentSection = defaultSections.getOrNull(selectedSectionIndex) ?: defaultSections.first()
        val currentUnit = currentSection.getOrNull(selectedUnitIndex) ?: currentSection.first()

        UnitDetailScreen(
            unit = currentUnit,
            progress = selectedUnitIndex,
            totalUnits = currentSection.size,
            onContinueClick = {
                // Xử lý nhấn nút Continue
                isHeaderExpanded = false
            },
            onSeeDetailsClick = {
                // Xử lý nhấn nút See Details
                isHeaderExpanded = false
                navController.navigate("details/${currentUnit.title}")
            },
            navController = navController,
            units = currentSection
        )
    } else {
        Scaffold { paddingValues ->
            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .onGloballyPositioned {
                        rootHeight = it.parentCoordinates!!.size.height.toFloat()
                    }
            ) {
                // Horizontal Pager cho các section
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxWidth()
                ) { page ->
                    val units = defaultSections.getOrNull(page) ?: listOf()
                    val lazyListState = rememberLazyListState()

                    // Theo dõi đơn vị hiện tại cho thanh trên cùng
                    val visibleHeadingIndex by remember {
                        derivedStateOf {
                            lazyListState.firstVisibleItemIndex.coerceIn(0, units.size - 1)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .pointerInput(Unit) {
                                detectTapGestures(onPress = {
                                    isDialogShown = false
                                })
                            }
                    ) {
                        Scaffold(
                            topBar = {
                                TopBar(
                                    units = units,
                                    visibleUnitIndex = visibleHeadingIndex,
                                    navController = navController,
                                    onExpandClick = {
                                        // Khi nhấn vào header, mở rộng thành màn hình chi tiết
                                        selectedSectionIndex = page
                                        selectedUnitIndex = visibleHeadingIndex
                                        isHeaderExpanded = true
                                    },
                                    isExpanded = false // Trạng thái không mở rộng trong HomeScreen
                                )
                            }
                        ) { innerPadding ->
                            // Khu vực nội dung với danh sách cuộn đơn vị
                            UnitsLazyColumn(
                                modifier = Modifier.padding(innerPadding),
                                state = lazyListState,
                                units = units,
                                starCountPerUnit = starCountPerUnit
                            ) { starCoordinate, isInteractive ->
                                // Xử lý nhấn vào sao với cuộn mượt
                                handleStarTap(
                                    coroutineScope = coroutineScope,
                                    starCoordinate = starCoordinate,
                                    isInteractive = isInteractive,
                                    rootHeight = rootHeight,
                                    lazyListState = lazyListState,
                                    onDialogStateChange = { shown, interactive, transition ->
                                        isDialogShown = shown
                                        isDialogInteractive = interactive
                                        dialogTransition = transition
                                    }
                                )
                            }
                        }
                    }
                }

                // Chỉ số trang (pagination dots)
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 68.dp), // Đặt ở trên navigation bar
                    horizontalArrangement = Arrangement.Center
                ) {
                    for (i in 0 until defaultSections.size) {
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(
                                    if (pagerState.currentPage == i) Color.White
                                    else Color.White.copy(alpha = 0.5f)
                                )
                                .pointerInput(Unit) {
                                    detectTapGestures {
                                        coroutineScope.launch {
                                            pagerState.animateScrollToPage(i)
                                        }
                                    }
                                }
                        )
                    }
                }

                // Dialog overlay cho tương tác sao
                StarDialog(
                    isDialogShown = isDialogShown,
                    isDialogInteractive = isDialogInteractive,
                    dialogTransition = dialogTransition,
                    navController = navController
                )
            }
        }
    }
}

// Các hàm hỗ trợ khác giữ nguyên
private fun handleStarTap(
    coroutineScope: kotlinx.coroutines.CoroutineScope,
    starCoordinate: Float,
    isInteractive: Boolean,
    rootHeight: Float,
    lazyListState: androidx.compose.foundation.lazy.LazyListState,
    onDialogStateChange: (shown: Boolean, interactive: Boolean, transition: Float) -> Unit
) {
    val midCoordinate = rootHeight / 2

    coroutineScope.launch {
        // Ẩn dialog trong khi scroll
        onDialogStateChange(false, isInteractive, 0f)

        // Tính toán và thực hiện scroll animation
        val scrollBy = (starCoordinate - midCoordinate).coerceAtLeast(0f)
        lazyListState.animateScrollBy(scrollBy)

        // Cập nhật vị trí dialog sau khi scroll hoàn tất
        val finalDialogPosition = starCoordinate - scrollBy
        onDialogStateChange(true, isInteractive, finalDialogPosition)
    }
}

fun orderToPercentage(order: Int, isRTL: Boolean = true): Float {
    val difference = 0.09f
    return when (order) {
        0 -> 0.45f
        1 -> 0.45f - if (isRTL) difference else -difference
        2 -> 0.45f - if (isRTL) difference * 2 else -difference * 2
        3 -> 0.45f - if (isRTL) difference else -difference
        4 -> 0.45f
        else -> 0.45f
    }
}

@Preview
@Composable
private fun HomeScreenPreview() {
    val sections = remember {
        listOf(
            listOf(
                UnitData(title = "Section 1: Rookie", color = FeatherGreen),
                UnitData(title = "Unit 2", color = Color.Red, darkerColor = Color.Red)
            ),
            listOf(
                UnitData(title = "Section 2: Explorer", color = Color.Gray),
                UnitData(title = "Unit 5", color = Color.Magenta)
            ),
            listOf(
                UnitData(title = "Section 3: Traveler", color = Color.Blue),
                UnitData(title = "Unit 7", color = Color.Cyan)
            )
        )
    }
    val navController = rememberNavController()
    HomeScreen(sections = sections, navController = navController)
}