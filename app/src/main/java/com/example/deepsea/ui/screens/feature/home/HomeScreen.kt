package com.example.deepsea.ui.screens.feature.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.deepsea.data.api.RetrofitClient
import com.example.deepsea.repository.CourseRepository
import com.example.deepsea.ui.components.StarDialog
import com.example.deepsea.ui.components.TopBar
import com.example.deepsea.viewmodel.CourseUiState
import com.example.deepsea.viewmodel.HomeViewModel
import com.example.deepsea.viewmodel.HomeViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * HomeScreen là màn hình chính của ứng dụng DeepSea
 * Hiển thị danh sách các đơn vị học tập với các ngôi sao tương tác
 * Hỗ trợ vuốt ngang để chuyển giữa các section
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(
            CourseRepository(RetrofitClient.courseApiService)
        )
    )
) {
    // Get context for ViewModel initialization
    val context = LocalContext.current

    // Initialize ViewModel with context
    LaunchedEffect(Unit) {
        viewModel.initialize(context)
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currentSectionIndex by viewModel.currentSectionIndex
    val currentUnitIndex = currentSectionIndex

    val coroutineScope = rememberCoroutineScope()
    val starCountPerUnit = 5

    // Trạng thái dialog
    var isDialogShown by remember { mutableStateOf(false) }
    var isDialogInteractive by remember { mutableStateOf(false) }
    var dialogTransition by remember { mutableFloatStateOf(0f) }
    var rootHeight by remember { mutableFloatStateOf(0f) }

    // Trạng thái mở rộng header (hiển thị màn hình chi tiết)
    var isHeaderExpanded by remember { mutableStateOf(false) }
    val unitListStates = remember { mutableMapOf<Int, LazyListState>() }
    val visibleUnitIndices = remember { mutableStateMapOf<Int, Int>() }

    // Quản lý trạng thái pager
    val pagerState = rememberPagerState {
        when (uiState) {
            is CourseUiState.Success -> (uiState as CourseUiState.Success).sections.size
            else -> 0
        }
    }

    when (uiState) {
        is CourseUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is CourseUiState.Error -> {
            ErrorScreen(
                message = (uiState as CourseUiState.Error).message,
                onRetry = { viewModel.loadCourseData(context) }
            )
        }
        is CourseUiState.Success -> {
            val successState = uiState as CourseUiState.Success
            val sections = successState.sections
            val units = successState.units

            // Khởi tạo LazyListState cho từng section nếu chưa có
            LaunchedEffect(units.size) {
                units.indices.forEach { index ->
                    if (!unitListStates.containsKey(index)) {
                        unitListStates[index] = LazyListState()
                    }
                }
            }

            // Hiển thị giao diện tương ứng với trạng thái
            if (isHeaderExpanded) {
                val currentSection = sections.getOrNull(currentSectionIndex) ?: sections.first()

                SectionDetailScreen(
                    section = currentSection,
                    sections = sections,
                    onContinueClick = {
                        isHeaderExpanded = false
                        coroutineScope.launch {
                            pagerState.scrollToPage(currentSectionIndex)
                            delay(100)
                            unitListStates[currentSectionIndex]?.scrollToItem(currentUnitIndex)
                        }
                    },
                    onSeeDetailsClick = { },
                    navController = navController,
                    onJumpToSection = { sectionIndex, unitIndex ->
                        viewModel.updateCurrentSection(sectionIndex)
                        viewModel.updateCurrentUnit(unitIndex)
                        isHeaderExpanded = false

                        coroutineScope.launch {
                            pagerState.scrollToPage(sectionIndex)
                            delay(300)
                            unitListStates[sectionIndex]?.scrollToItem(unitIndex)
                        }
                    }
                )
            } else {
                val currentSection = sections.getOrNull(currentSectionIndex) ?: sections.first()
                val currentUnits = units.getOrNull(currentSectionIndex) ?: emptyList()

                Scaffold(
                    topBar = {
                        TopBar(
                            sectionIndex = currentSectionIndex,
                            units = currentUnits,
                            visibleUnitIndex = visibleUnitIndices.getOrDefault(currentSectionIndex, 0),
                            navController = navController,
                            onExpandClick = {
                                isHeaderExpanded = true
                                viewModel.updateCurrentUnit(visibleUnitIndices.getOrDefault(currentSectionIndex, 0))
                            },
                            isExpanded = false,
                            sectionData = currentSection,
                            sections = sections
                        )
                    }
                ) { paddingValues ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = paddingValues.calculateTopPadding())
                    ) {
                        val lazyListState = unitListStates.getOrDefault(currentSectionIndex, rememberLazyListState())

                        LaunchedEffect(remember { derivedStateOf { lazyListState.firstVisibleItemIndex } }) {
                            val firstVisibleIndex = lazyListState.firstVisibleItemIndex
                                .coerceIn(0, currentUnits.size - 1)
                            visibleUnitIndices[currentSectionIndex] = firstVisibleIndex
                        }

                        UnitsListScreen(
                            modifier = Modifier,
                            totalSectionCount = sections.size,
                            state = lazyListState,
                            units = currentUnits,
                            starCountPerUnit = starCountPerUnit,
                            sectionIndex = currentSectionIndex,
                            onJumpToSection = { sectionIndex, unitIndex ->
                                viewModel.updateCurrentSection(sectionIndex + 1)
                                viewModel.updateCurrentUnit(unitIndex)

                                coroutineScope.launch {
                                    pagerState.scrollToPage(sectionIndex)
                                    delay(300)
                                    unitListStates[sectionIndex]?.scrollToItem(unitIndex)
                                }
                            },
                            section = currentSection,
                            sections = sections
                        ) { starCoordinate, isInteractive ->
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

                        StarDialog(
                            isDialogShown = isDialogShown,
                            isDialogInteractive = isDialogInteractive,
                            dialogTransition = dialogTransition,
                            navController = navController,
                            xpAmount = 15
                        )
                    }
                }
            }
        }
    }
}

// Các hàm hỗ trợ giữ nguyên
private fun handleStarTap(
    coroutineScope: CoroutineScope,
    starCoordinate: Float,
    isInteractive: Boolean,
    rootHeight: Float,
    lazyListState: LazyListState,
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