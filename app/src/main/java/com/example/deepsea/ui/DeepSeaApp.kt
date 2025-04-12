package com.example.deepsea.ui

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.deepsea.ui.components.DeepSeaScaffold
import com.example.deepsea.ui.home.DeepSeaBottomBar
import com.example.deepsea.ui.home.composableWithCompositionLocal
import com.example.deepsea.ui.navigation.MainDestinations
import com.example.deepsea.ui.navigation.rememberDeepSeaNavController
import com.example.deepsea.ui.navigation.rememberDeepSeaScaffoldState
import com.example.deepsea.ui.screens.DailyPage
import com.example.deepsea.ui.screens.GamePage
import com.example.deepsea.ui.screens.LearnPage
import com.example.deepsea.ui.screens.LoginPage
import com.example.deepsea.ui.screens.ProfilePage
import com.example.deepsea.ui.screens.RankPage
import com.example.deepsea.ui.screens.SignupPage
import com.example.deepsea.ui.theme.DeepSeaTheme

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview
@Composable
fun DeepSeaApp(){
    val deepSeaNavController= rememberDeepSeaNavController()

    DeepSeaTheme {
        SharedTransitionLayout {
            CompositionLocalProvider(LocalSharedTransitionScope provides this) {
                NavHost(
                    navController = deepSeaNavController.navController,
                    startDestination = MainDestinations.HOME_ROUTE
                ) {
                    composableWithCompositionLocal(
                        route = MainDestinations.HOME_ROUTE
                    ){backStackEntry ->
                        MainContainer(
                            onSnackSelected = deepSeaNavController::navigateToLogin
                        )
                    }

                    composableWithCompositionLocal(
                        "${MainDestinations.LOGIN_ROUTE}/" +
                                "{${MainDestinations.LOGIN_ID_KEY}}" +
                                "?origin={${MainDestinations.ORIGIN}}",
                        arguments = listOf(
                            navArgument(MainDestinations.LOGIN_ID_KEY) {
                                type = NavType.LongType
                            }
                        ),

                        ) { backStackEntry ->
                        LoginPage()
                    }
                    composableWithCompositionLocal(
                        "${MainDestinations.SIGNUP_ROUTE}/" +
                                "{${MainDestinations.SIGNUP_ID_KEY}}" +
                                "?origin={${MainDestinations.ORIGIN}}",
                        arguments = listOf(
                            navArgument(MainDestinations.SIGNUP_ID_KEY) {
                                type = NavType.LongType
                            }
                        ),

                        ) { backStackEntry ->
                        SignupPage()
                    }
                }
            }
        }
    }


}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun MainContainer(
    modifier: Modifier = Modifier,
    onSnackSelected: (Long, String, NavBackStackEntry) -> Unit
) {
    val deepSeaScaffoldState = rememberDeepSeaScaffoldState()
    val nestedNavController = rememberDeepSeaNavController()

    DeepSeaScaffold(
        bottomBar = {
            DeepSeaBottomBar(
                navController = nestedNavController.navController
            )
        },
        modifier = modifier,
        snackBarHostState = deepSeaScaffoldState.snackBarHostState,
    ) { padding ->
        NavHost(
            navController = nestedNavController.navController,
            startDestination = "home/learn",
            builder = {
                composable("home/learn") {
                    LearnPage()
                }

                composable("home/daily") {
                    DailyPage()
                }

                composable("home/rank") {
                    RankPage()
                }

                composable("home/profile") {
                    ProfilePage()
                }

                composable("home/game") {
                    GamePage()
                }
            })
    }
}


fun <T> nonSpatialExpressiveSpring() = spring<T>(
    dampingRatio = 1f,
    stiffness = 1600f
)

@OptIn(ExperimentalSharedTransitionApi::class)
val LocalSharedTransitionScope = compositionLocalOf<SharedTransitionScope?> { null }
val LocalNavAnimatedVisibilityScope = compositionLocalOf<AnimatedVisibilityScope?> { null }

