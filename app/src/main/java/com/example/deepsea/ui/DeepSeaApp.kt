@file:Suppress("UNCHECKED_CAST")

package com.example.deepsea.ui

import android.app.Application
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.deepsea.ui.components.DeepSeaFAButton
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
import com.example.deepsea.ui.screens.RankPage
import com.example.deepsea.ui.screens.SignupPage
import com.example.deepsea.ui.screens.WelcomePage
import com.example.deepsea.ui.theme.DeepSeaTheme
import com.example.deepsea.ui.viewmodel.AuthViewModel
import com.example.deepsea.utils.UserState
import android.util.Log
import androidx.compose.runtime.remember
import androidx.navigation.compose.rememberNavController
import com.example.deepsea.ui.components.UnitData
import com.example.deepsea.ui.profile.ProfilePage
import com.example.deepsea.ui.profile.UserProfileData
import com.example.deepsea.ui.screens.HomeScreen
import com.example.deepsea.ui.screens.LanguageSelectionPage
import com.example.deepsea.ui.screens.SurveyPage
import com.example.deepsea.ui.theme.FeatherGreen

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview
@Composable
fun DeepSeaApp() {
    val deepSeaNavController = rememberDeepSeaNavController()
    val context = LocalContext.current
    val authViewModel: AuthViewModel = viewModel(
        factory = ViewModelProvider.AndroidViewModelFactory(context.applicationContext as Application)
    )

    DeepSeaTheme {
        SharedTransitionLayout {
            CompositionLocalProvider(LocalSharedTransitionScope provides this) {
                NavHost(
                    navController = deepSeaNavController.navController,
                    startDestination = MainDestinations.HOME_ROUTE
                ) {
                    composableWithCompositionLocal(
                        route = MainDestinations.HOME_ROUTE
                    ) { backStackEntry ->
                        MainContainer(
                            onSnackSelected = deepSeaNavController::navigateToLogin,
                            authViewModel = authViewModel
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
                        LoginPage(
                            navController = deepSeaNavController,
                            onLoginSuccess = {
                                // Navigate to home screen after successful login
                                Log.d("DeepSeaApp", "Login success, navigating to HOME_ROUTE")
                                deepSeaNavController.navController.navigate(MainDestinations.HOME_ROUTE) {
                                    // Clear back stack to prevent going back to login screen
                                    popUpTo(MainDestinations.HOME_ROUTE) { inclusive = true }
                                }
                            },
                            authViewModel = authViewModel,
                            onSignInClick = { email, password ->
                                // Fixed: Directly call login instead of returning another lambda
                                Log.d("DeepSeaApp", "Attempting login with email: $email")
                                authViewModel.login(email, password)
                            }
                        )
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
                        SignupPage(
                            navController = deepSeaNavController,
                            onSignUpClick = { username, email, password, avatar ->
                                // Updated to handle avatar
                                Log.d("DeepSeaApp", "Attempting signup with email: $email and avatar: ${avatar != null}")
                                authViewModel.signup(username, email, password, avatar)
                            },
                            onSignInClick = {
                                // Navigate to login page
                                deepSeaNavController.navController.navigate("${MainDestinations.LOGIN_ROUTE}/0")
                            },
                            authViewModel = authViewModel,
                            onRegisterSuccess = {
                                // Navigate to home screen after successful registration
                                Log.d("DeepSeaApp", "Registration success, navigating to HOME_ROUTE")
                                deepSeaNavController.navController.navigate(MainDestinations.HOME_ROUTE) {
                                    // Clear back stack to prevent going back to signup screen
                                    popUpTo(MainDestinations.HOME_ROUTE) { inclusive = true }
                                }
                            }
                        )
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
    onSnackSelected: (Long, String, NavBackStackEntry) -> Unit,
    authViewModel: AuthViewModel
) {
    val deepSeaScaffoldState = rememberDeepSeaScaffoldState()
    val nestedNavController = rememberDeepSeaNavController()
    val navBackStackEntry by nestedNavController.navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val isAuthRoute: Boolean = currentRoute != "login" &&
                                currentRoute != "signup" &&
                                currentRoute != "welcome" &&
                                currentRoute != "learn-selection" &&
                                currentRoute != "survey-selection"

    val userState by authViewModel.userState.collectAsState()

    LaunchedEffect(userState, currentRoute) {
        Log.d("MainContainer", "UserState: $userState, CurrentRoute: $currentRoute")
        if (userState is UserState.NotLoggedIn && isAuthRoute && currentRoute != "welcome") {
            Log.d("MainContainer", "User not logged in, navigating to welcome")
            nestedNavController.navController.navigate("welcome") {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    DeepSeaScaffold(
        floatingActionButton = {
            Box { // This provides the alignment scope
                if (isAuthRoute)
                    DeepSeaFAButton(
                        modifier = Modifier.align(Alignment.TopCenter),
                        containerColor = Color(0xFFB2DFDB),
                        onItemClick = { title ->
                            when (title) {
                                "Explore" -> nestedNavController.navController.navigate("explore_route")
                                "Favorites" -> nestedNavController.navController.navigate("favorites_route")
                                "Settings" -> nestedNavController.navController.navigate("settings_route")
                                "Help" -> nestedNavController.navController.navigate("help_route")
                            }
                        }
                    )
            }
        },
        bottomBar = {
            if (isAuthRoute)
                DeepSeaBottomBar(
                    navController = nestedNavController.navController
                )
        },
        modifier = modifier,
        snackBarHostState = deepSeaScaffoldState.snackBarHostState,
    ) { padding ->
        NavHost(
            navController = nestedNavController.navController,
            startDestination = "welcome",
            builder = {
                composable("welcome") {
                    WelcomePage(
                        navController = nestedNavController
                    )
                }
                composable("survey-selection") {
                    SurveyPage(nestedNavController.navController)
                }
                composable("learn-selection") {
                    LanguageSelectionPage(nestedNavController.navController)
                }
                composable("home") {
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
                composable("signup") {
                    SignupPage(
                        navController = nestedNavController,
                        onSignUpClick = { username, email, password, avatar ->
                            // Updated to handle avatar
                            Log.d("MainContainer", "Attempting signup with email: $email and avatar: ${avatar != null}")
                            authViewModel.signup(username, email, password, avatar)
                        },
                        onSignInClick = {
                            nestedNavController.navController.navigate("login")
                        },
                        authViewModel = authViewModel,
                        onRegisterSuccess = {
                            Log.d("MainContainer", "Registration success, navigating to home/learn")
                            nestedNavController.navController.navigate("home/learn") {
                                popUpTo("welcome") { inclusive = true }
                            }
                        }
                    )
                }
                composable("login") {
                    LoginPage(
                        navController = nestedNavController,
                        authViewModel = authViewModel,
                        onLoginSuccess = {
                            Log.d("MainContainer", "Login success, navigating to learn-selection")
                            nestedNavController.navController.navigate("survey-selection") {
                                popUpTo("login") { inclusive = true }
                            }
                        },
                        onSignInClick = { email, password ->
                            // Fixed: Directly call login
                            Log.d("MainContainer", "Attempting login with email: $email")
                            authViewModel.login(email, password)
                        }
                    )
                }

                composable("home/learn") {
                    // Load dashboard data when entering the main area
                    LaunchedEffect(Unit) {
                        Log.d("MainContainer", "Loading dashboard data")
                        authViewModel.loadDashboard()
                    }
                    LearnPage()
                }

                composable("home/daily") {
                    DailyPage()
                }

                composable("home/rank") {
                    RankPage()
                }

                composable("home/profile") {
                    val userState by authViewModel.userState.collectAsState()

                    val sampleUserData = UserProfileData(
                        name = "Huy V6",
                        username = "BlackNoir1172005",
                        joinDate = "August 2024",
                        following = 45,
                        followers = 23,
                        dayStreak = 235,
                        totalXp = 9102,
                        currentLeague = "WEEK 2 Ruby",
                        topFinishes = 1,
                        courses = listOf("Course 1", "Course 2") // Example courses
                    )
                    ProfilePage(userData = sampleUserData)
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