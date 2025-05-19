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
import com.example.deepsea.ui.components.TopBar
import com.example.deepsea.ui.viewmodel.home.CourseUiState
import com.example.deepsea.ui.viewmodel.home.HomeViewModel
import com.example.deepsea.ui.viewmodel.home.HomeViewModelFactory
import com.example.deepsea.ui.viewmodel.home.NavigationEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.initialize(context)
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currentSectionIndex by viewModel.currentSectionIndex
    val currentUnitIndex = currentSectionIndex

    // Get completed stars from the ViewModel
    val completedStars by viewModel.completedStars.collectAsStateWithLifecycle()

    val coroutineScope = rememberCoroutineScope()
    val starCountPerUnit = 5

    var isDialogShown by remember { mutableStateOf(false) }
    var isDialogInteractive by remember { mutableStateOf(false) }
    var dialogTransition by remember { mutableFloatStateOf(0f) }
    var rootHeight by remember { mutableFloatStateOf(0f) }
    var selectedUnitId by remember { mutableStateOf<Long?>(null) }
    var selectedStarIndex by remember { mutableStateOf<Int?>(null) }

    var isHeaderExpanded by remember { mutableStateOf(false) }
    val unitListStates = remember { mutableMapOf<Int, LazyListState>() }
    val visibleUnitIndices = remember { mutableStateMapOf<Int, Int>() }

    val pagerState = rememberPagerState {
        when (uiState) {
            is CourseUiState.Success -> (uiState as CourseUiState.Success).sections.size
            else -> 0
        }
    }
    LaunchedEffect(key1 = true) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is NavigationEvent.ToGuideBook -> {
                    navController.navigate("unit_guide/${event.unitId}")
                }
            }
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

            LaunchedEffect(units.size) {
                units.indices.forEach { index ->
                    if (!unitListStates.containsKey(index)) {
                        unitListStates[index] = LazyListState()
                    }
                }
            }

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
                            visibleUnitIndex = visibleUnitIndices.getOrDefault(
                                currentSectionIndex,
                                0
                            ),
                            navController = navController,
                            onExpandClick = {
                                isHeaderExpanded = true
                                viewModel.updateCurrentUnit(
                                    visibleUnitIndices.getOrDefault(
                                        currentSectionIndex,
                                        0
                                    )
                                )
                            },
                            isExpanded = false,
                            sectionData = currentSection,
                            sections = sections,
                            homeViewModel = viewModel
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
                            completedStars = completedStars,
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
                            sections = sections,
                            navController = navController, // Added navController parameter
                            onGuideBookClicked = { unitId ->
                                viewModel.navigateToGuideBook(unitId)
                            },
                            onStarClicked = { starCoordinate, isInteractive, unitId, starIndex ->
                                selectedStarIndex = starIndex
                                handleStarTap(
                                    coroutineScope = coroutineScope,
                                    starCoordinate = starCoordinate,
                                    isInteractive = isInteractive,
                                    rootHeight = rootHeight,
                                    lazyListState = lazyListState,
                                    unitId = unitId,
                                    onDialogStateChange = { shown, interactive, transition ->
                                        isDialogShown = shown
                                        isDialogInteractive = interactive
                                        dialogTransition = transition
                                        selectedUnitId = unitId
                                    }
                                )
                            },
                            onStarComplete = { unitId, starIndex -> // Added onStarComplete parameter
                                viewModel.completeStar(unitId, starIndex)
                            }
                        )
                    }
                }
            }
        }
    }
}

private fun handleStarTap(
    coroutineScope: CoroutineScope,
    starCoordinate: Float,
    isInteractive: Boolean,
    rootHeight: Float,
    lazyListState: LazyListState,
    unitId: Long,
    onDialogStateChange: (shown: Boolean, interactive: Boolean, transition: Float) -> Unit
) {
    val midCoordinate = rootHeight / 2

    coroutineScope.launch {
        onDialogStateChange(false, isInteractive, 0f)

        val scrollBy = (starCoordinate - midCoordinate).coerceAtLeast(0f)
        lazyListState.animateScrollBy(scrollBy)

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