package com.example.deepsea.ui.screens.feature

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.deepsea.R
import com.example.deepsea.ui.components.SectionData
import com.example.deepsea.ui.components.StarDialog
import com.example.deepsea.ui.components.TopBar
import com.example.deepsea.ui.components.UnitData
import com.example.deepsea.ui.theme.Blue
import com.example.deepsea.ui.theme.BlueDark
import com.example.deepsea.ui.theme.Cyan
import com.example.deepsea.ui.theme.CyanDark
import com.example.deepsea.ui.theme.FeatherGreen
import com.example.deepsea.ui.theme.FeatherGreenDark
import com.example.deepsea.ui.theme.FunctionalRedDark
import com.example.deepsea.ui.theme.Lavender3
import com.example.deepsea.ui.theme.Ocean3
import com.example.deepsea.ui.theme.Pink40
import com.example.deepsea.ui.theme.PinkDark
import com.example.deepsea.ui.theme.Rose3
import com.example.deepsea.ui.theme.Shadow3
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
fun HomeScreen(sections: List<List<UnitData>> = emptyList(), navController: NavController) {
    val sampleUnits = remember {
        if (sections.isEmpty()) {
            listOf(
                // Section 1: Beginner
                listOf(
                    UnitData(title = "Unit 1", color = FeatherGreen, description = "Introduce yourself", image = R.drawable.ic_self_intro),
                    UnitData(title = "Unit 2", color = Ocean3, description = "Greet others", image = R.drawable.ic_greeting),
                    UnitData(title = "Unit 3", color = PinkDark, description = "Say goodbye", image = R.drawable.ic_goodbye),
                    UnitData(title = "Unit 4", color = FunctionalRedDark, darkerColor = Pink40, description = "Talk about where you’re from", image = R.drawable.ic_world_map),
                    UnitData(title = "Unit 5", color = Lavender3, description = "Exchange contact info", image = R.drawable.ic_contact_info),
                ),

                // Section 2: Explorer
                listOf(
                    UnitData(title = "Unit 6", color = CyanDark, description = "Talk about daily routines", image = R.drawable.ic_routine),
                    UnitData(title = "Unit 7", color = Shadow3, description = "Describe people", image = R.drawable.ic_people),
                    UnitData(title = "Unit 8", color = Rose3, description = "Talk about your home", image = R.drawable.ic_home),
                    UnitData(title = "Unit 9", color = FeatherGreen, description = "Talk about your family", image = R.drawable.ic_family),
                    UnitData(title = "Unit 10", color = Ocean3, description = "Talk about weather", image = R.drawable.ic_weather),
                ),

                // Section 3: Traveler
                listOf(
                    UnitData(title = "Unit 11", color = Cyan, description = "Order food", image = R.drawable.ic_food),
                    UnitData(title = "Unit 12", color = Pink40, description = "Order drinks", image = R.drawable.ic_drinks),
                    UnitData(title = "Unit 13", color = BlueDark, description = "Go shopping", image = R.drawable.ic_shopping_cart),
                    UnitData(title = "Unit 14", color = Rose3, description = "Ask about prices", image = R.drawable.ic_price_tag),
                    UnitData(title = "Unit 15", color = FunctionalRedDark, description = "Talk about preferences", image = R.drawable.ic_preferences),
                ),

                // Section 4: Navigator
                listOf(
                    UnitData(title = "Unit 16", color = CyanDark, description = "Ask for directions", image = R.drawable.ic_directions),
                    UnitData(title = "Unit 17", color = Shadow3, description = "Use public transport", image = R.drawable.ic_transport),
                    UnitData(title = "Unit 18", color = Lavender3, description = "Book a taxi", image = R.drawable.ic_taxi),
                    UnitData(title = "Unit 19", color = FeatherGreenDark, description = "Find places in a city", image = R.drawable.ic_map),
                    UnitData(title = "Unit 20", color = Blue, description = "Tell time & make appointments", image = R.drawable.ic_clock),
                ),

                // Section 5: Socializer
                listOf(
                    UnitData(title = "Unit 21", color = PinkDark, description = "Talk about hobbies", image = R.drawable.ic_hobbies),
                    UnitData(title = "Unit 22", color = Cyan, description = "Talk about plans", image = R.drawable.ic_calendar),
                    UnitData(title = "Unit 23", color = Rose3, description = "Make invitations", image = R.drawable.ic_invite),
                    UnitData(title = "Unit 24", color = Lavender3, description = "Accept or decline invitations", image = R.drawable.ic_check_cross),
                    UnitData(title = "Unit 25", color = FunctionalRedDark, description = "Describe past experiences", image = R.drawable.ic_experience),
                ),

                // Section 6: Professional
                listOf(
                    UnitData(title = "Unit 26", color = BlueDark, description = "Talk about jobs", image = R.drawable.ic_jobs),
                    UnitData(title = "Unit 27", color = FeatherGreen, description = "At a job interview", image = R.drawable.ic_interview),
                    UnitData(title = "Unit 28", color = Ocean3, description = "Talk about work routines", image = R.drawable.ic_work_routine),
                    UnitData(title = "Unit 29", color = Pink40, description = "Solve problems at work", image = R.drawable.ic_problem_solve),
                    UnitData(title = "Unit 30", color = CyanDark, description = "Talk about the future", image = R.drawable.ic_future),
                ),

                )
        } else {
            sections
        }
    }

    val sampleSections = listOf(
        SectionData(
            title = "Section 1: Rookie",
            color = FeatherGreen,
            darkerColor = FeatherGreenDark,
            description = "Learn how to introduce yourself and greet others.",
            image = R.drawable.ic_handshake, // 🤝 giới thiệu bản thân
            level = "N5",
            units = sampleUnits[0]
        ),
        SectionData(
            title = "Section 2: Explorer",
            color = Cyan,
            darkerColor = CyanDark,
            description = "Talk about your daily routines and habits.",
            image = R.drawable.ic_calendar, // 🗓️ thói quen hàng ngày
            level = "N5",
            units = sampleUnits[1]
        ),
        SectionData(
            title = "Section 3: Traveler",
            color = Blue,
            darkerColor = BlueDark,
            description = "Learn useful phrases for shopping situations.",
            image = R.drawable.ic_shopping_cart, // 🛒 mua sắm
            level = "N4",
            units = sampleUnits[2]
        ),
        SectionData(
            title = "Section 4: Navigator",
            color = Pink40,
            darkerColor = PinkDark,
            description = "Ask for directions and use public transport.",
            image = R.drawable.ic_map, // 🗺️ hỏi đường
            level = "N4",
            units = sampleUnits[3] // có thể đổi theo logic của bạn
        ),
        SectionData(
            title = "Section 5: Socializer",
            color = Rose3,
            darkerColor = FunctionalRedDark,
            description = "Talk about friends, family, and social life.",
            image = R.drawable.ic_people, // 👥 xã hội
            level = "N3",
            units = sampleUnits[4]
        ),
        SectionData(
            title = "Section 6: Professional",
            color = Shadow3,
            darkerColor = Color.Gray,
            description = "Learn vocabulary for work and job interviews.",
            image = R.drawable.ic_briefcase, // 💼 công việc
            level = "N3",
            units = sampleUnits[5]
        )
    )

    // Quản lý trạng thái pager
    val pagerState = rememberPagerState(pageCount = { sampleUnits.size })
    val coroutineScope = rememberCoroutineScope()
    val starCountPerUnit = 5

    // Trạng thái dialog
    var isDialogShown by remember { mutableStateOf(false) }
    var isDialogInteractive by remember { mutableStateOf(false) }
    var dialogTransition by remember { mutableFloatStateOf(0f) }
    var rootHeight by remember { mutableFloatStateOf(0f) }

    // Trạng thái mở rộng header (hiển thị màn hình chi tiết)
    var isHeaderExpanded by remember { mutableStateOf(false) }
    var selectedSectionIndex by remember { mutableIntStateOf(0) }
    var selectedUnitIndex by remember { mutableIntStateOf(0) }
    val unitListStates = remember {
        mutableMapOf<Int, LazyListState>().apply {
            sampleUnits.indices.forEach { index ->
                put(index, LazyListState())
            }
        }
    }

    val currentSection = sampleSections.getOrNull(selectedSectionIndex) ?: sampleSections.first()
    val currentUnits = sampleUnits.getOrNull(selectedSectionIndex) ?: emptyList()


    // Hiển thị giao diện tương ứng với trạng thái
    if (isHeaderExpanded) {
        SectionDetailScreen(
            section = currentSection, // sử dụng đúng section hiện tại
            sections = sampleSections,
            onContinueClick = {
                isHeaderExpanded = false
                coroutineScope.launch {
                    pagerState.scrollToPage(selectedSectionIndex)
                    delay(100)
                    unitListStates[selectedSectionIndex]?.scrollToItem(selectedUnitIndex)
                }
            },
            onSeeDetailsClick = { },
            navController = navController,
            onJumpToSection = { sectionIndex, unitIndex ->
                selectedSectionIndex = sectionIndex
                selectedUnitIndex = unitIndex
                isHeaderExpanded = false

                coroutineScope.launch {
                    pagerState.scrollToPage(sectionIndex)
                    delay(1000)
                    unitListStates[sectionIndex]?.scrollToItem(unitIndex)
                }
            }
        )
    } else {
        val sectionList: List<SectionData> = sampleSections // hoặc biến nào đó cùng kiểu
        val visibleUnitIndices = remember { mutableStateMapOf<Int, Int>() }
        Scaffold(
            topBar = {
                TopBar(
                    sectionIndex = selectedSectionIndex,
                    units = sampleUnits.getOrNull(selectedSectionIndex) ?: listOf(),
                    visibleUnitIndex = visibleUnitIndices.getOrDefault(selectedSectionIndex, 0),
                    navController = navController,
                    onExpandClick = {
                        isHeaderExpanded = true
                        selectedUnitIndex = visibleUnitIndices.getOrDefault(selectedSectionIndex, 0)
                    },
                    isExpanded = false,
                    sectionData = sectionList.getOrNull(selectedSectionIndex) ?: sectionList[0],
                    sections = sampleSections
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = paddingValues.calculateTopPadding())
            ) {
                val lazyListState = rememberLazyListState()
                val units = sampleUnits.getOrNull(selectedSectionIndex) ?: listOf()

                LaunchedEffect(remember { derivedStateOf { lazyListState.firstVisibleItemIndex } }) {
                    visibleUnitIndices[selectedSectionIndex] = lazyListState.firstVisibleItemIndex
                        .coerceIn(0, units.size - 1)
                }

                UnitsListScreen(
                    modifier = Modifier,
                    totalSectionCount = sampleSections.size,
                    state = lazyListState,
                    units = units,
                    starCountPerUnit = starCountPerUnit,
                    sectionIndex = selectedSectionIndex,
                    onJumpToSection = { sectionIndex, unitIndex ->
                        selectedSectionIndex = sectionIndex + 1
                        selectedUnitIndex = unitIndex
                        isHeaderExpanded = false

                        coroutineScope.launch {
                            pagerState.scrollToPage(sectionIndex)
                            delay(1000)
                            unitListStates[sectionIndex]?.scrollToItem(unitIndex)
                        }
                    },
                    section = sectionList.getOrNull(selectedSectionIndex) ?: sectionList[0],
                    sections = sampleSections
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

// Các hàm hỗ trợ khác giữ nguyên
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