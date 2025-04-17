package com.example.deepsea.ui.screens

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.rememberNavController
import com.example.deepsea.text.PrimaryText
import com.example.deepsea.text.TitleText
import com.example.deepsea.ui.components.TopBar
import com.example.deepsea.ui.components.UnitData
import com.example.deepsea.ui.components.UnitsLazyColumn
import com.example.deepsea.ui.home.DeepSeaBottomBar
import com.example.deepsea.ui.theme.FeatherGreen
import com.example.deepsea.ui.theme.Gray
import com.example.deepsea.ui.theme.Polar
import kotlinx.coroutines.launch

/**
 * Extension function to add composable with composition local to NavGraphBuilder
 * Provides default transition animation
 */
fun NavGraphBuilder.composableWithCompositionLocal(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    enterTransition: (@JvmSuppressWildcards AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition)? = {
        fadeIn()
    }
) {
    // Implementation to be added later
}

/**
 * HomeScreen is the main screen of the DeepSea app
 * Displays a list of learning units with interactive stars
 *
 * @param units List of learning units to display
 * @param navController Navigation controller for app navigation
 */
@Composable
fun HomeScreen(units: List<UnitData> = emptyList(), navController: NavController) {
    // State management
    val lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val starCountPerUnit = 5

    // Track visible unit for the top bar
    val visibleHeadingIndex by remember {
        derivedStateOf { lazyListState.firstVisibleItemIndex }
    }

    // Dialog state
    var isDialogShown by remember { mutableStateOf(false) }
    var isDialogInteractive by remember { mutableStateOf(false) }
    var dialogTransition by remember { mutableStateOf(0f) }
    var rootHeight by remember { mutableStateOf(0f) }

    // Main layout with top and bottom bars
    Scaffold(
        topBar = {
            TopBar(
                units = units,
                visibleUnitIndex = visibleHeadingIndex,
            )
        }
    ) { paddingValues ->
        // Content area with scrolling units
        UnitsLazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .onGloballyPositioned {
                    rootHeight = it.parentCoordinates!!.size.height.toFloat()
                }
                .pointerInput(Unit) {
                    detectTapGestures(onPress = {
                        isDialogShown = false
                    })
                }
                .width(500.dp),
            state = lazyListState,
            units = units,
            starCountPerUnit = starCountPerUnit
        ) { starCoordinate, isInteractive ->
            // Handle star tap with smooth scrolling
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

    // Dialog overlay for star interaction
    StarDialog(
        isDialogShown = isDialogShown,
        isDialogInteractive = isDialogInteractive,
        dialogTransition = dialogTransition
    )
}

/**
 * Handles the star tap event with smooth scrolling animation
 */
private fun handleStarTap(
    coroutineScope: kotlinx.coroutines.CoroutineScope,
    starCoordinate: Float,
    isInteractive: Boolean,
    rootHeight: Float,
    lazyListState: androidx.compose.foundation.lazy.LazyListState,
    onDialogStateChange: (shown: Boolean, interactive: Boolean, transition: Float) -> Unit
) {
    val midCoordinates = rootHeight / 2

    coroutineScope.launch {
        // Hide dialog during scrolling
        onDialogStateChange(false, isInteractive, 0f)

        // Calculate and animate scroll
        val scrollBy = (starCoordinate - midCoordinates)
        lazyListState.animateScrollBy(scrollBy)

        // Show dialog after scrolling complete
        onDialogStateChange(true, isInteractive, midCoordinates)
    }
}

/**
 * Dialog that appears when a star is tapped
 * Provides feedback on level status and interactivity
 *
 * @param isDialogShown Controls dialog visibility
 * @param isDialogInteractive Controls whether dialog shows interactive or locked state
 * @param dialogTransition Vertical position of the dialog
 */
@Composable
fun StarDialog(
    isDialogShown: Boolean,
    isDialogInteractive: Boolean,
    dialogTransition: Float
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        // Animate dialog scaling
        val animatedScale by animateFloatAsState(
            targetValue = if (isDialogShown) 1f else 0f
        )

        // Dialog content
        Column(
            modifier = Modifier
                .graphicsLayer {
                    translationY = dialogTransition + 100.dp.toPx()
                    transformOrigin = TransformOrigin(0.5f, 0f)
                    scaleY = animatedScale
                    scaleX = animatedScale
                }
                .fillMaxWidth(0.8f)
                .background(
                    color = if (isDialogInteractive) FeatherGreen else Polar,
                    shape = RoundedCornerShape(8.dp)
                )
                .border(
                    width = 1.dp,
                    color = Gray,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            // Dialog title
            TitleText(
                text = "Make introductions",
                color = if (isDialogInteractive) Color.White else Color.DarkGray.copy(0.5f),
                fontSize = 19.sp
            )

            // Dialog description
            PrimaryText(
                text = "Complete all levels above to unlock this",
                color = if (isDialogInteractive) Color.White else Color.DarkGray.copy(0.3f)
            )

            // Action button
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { /* Handle button click */ },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isDialogInteractive) Color.White else Color.DarkGray.copy(0.15f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                TitleText(
                    text = if (isDialogInteractive) "LET'S GO!" else "LOCKED",
                    color = if (isDialogInteractive) FeatherGreen else Color.DarkGray.copy(0.5f),
                    fontSize = 18.sp
                )
            }
        }
    }
}

/**
 * Calculates star positioning based on order and direction
 *
 * @param order Index of the star
 * @param isRTL Whether layout is right-to-left
 * @return Position percentage for horizontal alignment
 */
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

/**
 * Preview composable for the HomeScreen
 */
@Preview
@Composable
private fun HomeScreenPreview() {
    val units = remember {
        listOf(
            UnitData(title = "Unit 1", color = FeatherGreen),
            UnitData(title = "Unit 2", color = Color.Red, darkerColor = Color.Red),
            UnitData(title = "Unit 3", color = Color.Yellow),
            UnitData(title = "Unit 4", color = Color.Gray),
            UnitData(title = "Unit 5", color = Color.Magenta),
            UnitData(title = "Unit 6", color = Color.Blue)
        )
    }
    val navController = rememberNavController()
    HomeScreen(units = units, navController = navController)
}