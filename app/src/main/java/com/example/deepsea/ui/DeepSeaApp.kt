@file:Suppress("UNCHECKED_CAST")

package com.example.deepsea.ui

import android.app.Application
import android.util.Log
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
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
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.deepsea.AI_assistant.VoiceAssistantScreen
import com.example.deepsea.data.api.RetrofitClient
import com.example.deepsea.data.api.UserProfileService
import com.example.deepsea.data.repository.UserProfileRepository
import com.example.deepsea.ui.components.DeepSeaFAButton
import com.example.deepsea.ui.components.DeepSeaScaffold
import com.example.deepsea.ui.components.StreakScreen
import com.example.deepsea.ui.home.DeepSeaBottomBar
import com.example.deepsea.ui.home.composableWithCompositionLocal
import com.example.deepsea.ui.navigation.MainDestinations
import com.example.deepsea.ui.navigation.rememberDeepSeaNavController
import com.example.deepsea.ui.navigation.rememberDeepSeaScaffoldState
import com.example.deepsea.ui.profile.ProfilePage
import com.example.deepsea.ui.screens.auth.ForgotPasswordPage
import com.example.deepsea.ui.screens.auth.LoginPage
import com.example.deepsea.ui.screens.auth.SignupPage
import com.example.deepsea.ui.screens.feature.daily.DailyPage
import com.example.deepsea.ui.screens.feature.game.GamePage
import com.example.deepsea.ui.screens.feature.home.HomeScreen
import com.example.deepsea.ui.screens.feature.learn.LanguageListeningScreen
import com.example.deepsea.ui.screens.feature.learn.WordBuildingScreen
import com.example.deepsea.ui.screens.feature.rank.RankPage
import com.example.deepsea.ui.screens.feature.settings.SettingsPage
import com.example.deepsea.ui.screens.path.DailyGoalSelectionPage
import com.example.deepsea.ui.screens.path.LanguageSelectionPage
import com.example.deepsea.ui.screens.path.PathSelectionFlowPage
import com.example.deepsea.ui.screens.path.SurveySelectionPage
import com.example.deepsea.ui.screens.path.WelcomePage
import com.example.deepsea.ui.theme.DeepSeaTheme
import com.example.deepsea.ui.viewmodel.auth.AuthViewModel
import com.example.deepsea.ui.viewmodel.course.language.LanguageSelectionViewModel
import com.example.deepsea.ui.viewmodel.course.language.LanguageSelectionViewModelFactory
import com.example.deepsea.ui.viewmodel.survey.SurveySelectionViewModel
import com.example.deepsea.ui.viewmodel.survey.SurveyViewModelFactory
import com.example.deepsea.utils.SessionManager
import com.example.deepsea.utils.UserState

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
                                authViewModel.login(email, password, deepSeaNavController.navController)
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
                            onSignUpClick = { username, email, password, avatar, name ->
                                // Updated to handle avatar
                                Log.d("DeepSeaApp", "Attempting signup with email: $email and avatar: ${avatar != null}")
                                authViewModel.signup(username, email, password, avatar, name)
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
    authViewModel: AuthViewModel
) {
    val deepSeaScaffoldState = rememberDeepSeaScaffoldState()
    val deepSeaNavController = rememberDeepSeaNavController()
    val navBackStackEntry by deepSeaNavController.navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val isImportantRoute: Boolean = currentRoute != "login" &&
                                currentRoute != "signup" &&
                                currentRoute != "forgot-password" &&
                                currentRoute != "welcome" &&
                                currentRoute != "language-selection" &&
                                currentRoute != "survey-selection" &&
                                currentRoute != "daily-goal-selection" &&
                                currentRoute != "path_selection" &&
                                currentRoute != "home/streak" &&
                                currentRoute != "listening-screen" &&
                                currentRoute != "word-building-screen"

    val userState by authViewModel.userState.collectAsState()

    LaunchedEffect(userState, currentRoute) {
        Log.d("MainContainer", "UserState: $userState, CurrentRoute: $currentRoute")
        if (userState is UserState.NotLoggedIn && isImportantRoute && currentRoute != "welcome") {
            Log.d("MainContainer", "User not logged in, navigating to welcome")
            deepSeaNavController.navController.navigate("welcome") {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    DeepSeaScaffold(
        floatingActionButton = {
            Box { // This provides the alignment scope
                if (isImportantRoute)
                    DeepSeaFAButton(
                        modifier = Modifier.align(Alignment.TopCenter),
                        containerColor = Color(0xFFB2DFDB),
                        onItemClick = { title ->
                            when (title) {
                                "Explore" -> deepSeaNavController.navController.navigate("explore_route")
                                "Favorites" -> deepSeaNavController.navController.navigate("favorites_route")
                                "Settings" -> deepSeaNavController.navController.navigate("settings_route")
                                "Help" -> deepSeaNavController.navController.navigate("help_route")
                                "Voice Assistant" -> deepSeaNavController.navController.navigate("home/voice_assistant")
                            }
                        }
                    )
            }
        },
        bottomBar = {
            if (isImportantRoute)
                DeepSeaBottomBar(
                    navController = deepSeaNavController.navController
                )
        },
        modifier = modifier,
        snackBarHostState = deepSeaScaffoldState.snackBarHostState,
    ) { padding ->
        NavHost(
            navController = deepSeaNavController.navController,
            startDestination = "welcome",
            modifier = modifier.padding(padding),
            builder = {
                composable("welcome") {
                    WelcomePage(
                        navController = deepSeaNavController,
                        authViewModel = authViewModel
                    )
                }


                // Selection for learn Routes
                composable("path_selection") {
                    val context = LocalContext.current
                    val sessionManager = SessionManager(context)
                    val userProfileService: UserProfileService = RetrofitClient.userProfileService
                    PathSelectionFlowPage(
                        navController = deepSeaNavController.navController,
                        sessionManager = sessionManager,
                        pathService = userProfileService
                    )
                }
                composable("daily-goal-selection") {
                    val context = LocalContext.current
                    val sessionManager = SessionManager(context)
                    DailyGoalSelectionPage(navController = deepSeaNavController.navController,
                        sessionManager= sessionManager)
                }
                composable("survey-selection") {
                    val userProfileRepository = UserProfileRepository(RetrofitClient.userProfileService)

                    val surveySelectionViewModel: SurveySelectionViewModel = viewModel(
                        factory = SurveyViewModelFactory(userProfileRepository)
                    )
                    val context = LocalContext.current
                    val sessionManager = SessionManager(context)
                    SurveySelectionPage(
                        navController = deepSeaNavController.navController,
                        surveySelectionViewModel = surveySelectionViewModel,
                        sessionManager = sessionManager
                    )
                }
                composable("language-selection") {
                    val context = LocalContext.current
                    val sessionManager = SessionManager(context)
                    val userProfileRepository = UserProfileRepository(RetrofitClient.userProfileService)

                    val languageSelectionViewModel: LanguageSelectionViewModel = viewModel(
                        factory = LanguageSelectionViewModelFactory(userProfileRepository)
                    )
                    LanguageSelectionPage(
                        navController = deepSeaNavController.navController,
                        sessionManager = sessionManager,
                        languageSelectionViewModel = languageSelectionViewModel
                    )
                }

                // Main Routes
                composable("home") {
                    HomeScreen(navController = deepSeaNavController.navController)
                }

                composable("home/daily") {
                    DailyPage()
                }

                composable("home/rank") {
                    RankPage()
                }

                composable("listening-screen"){
                    LanguageListeningScreen()
                }

                composable("word-building-screen") {
                    WordBuildingScreen()
                }

                composable("home/profile/{userId}") { backStackEntry ->
                    val context = LocalContext.current
                    val sessionManager = SessionManager(context) // Khởi tạo sessionManager
                    ProfilePage(sessionManager = sessionManager,
                        paddingValues = padding,
                        onNavigateToSettings = {
                            deepSeaNavController.navController.navigate("settings")
                        }
                    )
                }

                composable("home/streak") {
                    StreakScreen(
                        currentStreak = 5,
                        onDismissRequest = {
                            deepSeaNavController.navController.navigate("home")
                        }
                    )
                }

                composable("home/game") {
                    GamePage()
                }
                composable("settings") {
                    SettingsPage(
                        onBackPressed = { deepSeaNavController.navController.popBackStack() },
                        onPreferencesClick = { deepSeaNavController.navController.navigate("preferences") },
                        onProfileClick = { deepSeaNavController.navController.navigate("profile") },
                        onNotificationsClick = { deepSeaNavController.navController.navigate("notifications") },
                        onCoursesClick = { deepSeaNavController.navController.navigate("courses") },
                        onPrivacySettingsClick = { deepSeaNavController.navController.navigate("privacy_settings") },
                        onHelpCenterClick = { deepSeaNavController.navController.navigate("help_center") },
                        onFeedbackClick = { deepSeaNavController.navController.navigate("feedback") },
                        onSignOut = { authViewModel.logout() },
                        paddingValues = padding
                    )
                }
                composable("home/voice_assistant") {
                    VoiceAssistantScreen()
                }
                // Auth Routes
                composable("signup") {
                    SignupPage(
                        navController = deepSeaNavController,
                        onSignUpClick = { username, email, password, avatar, name ->
                            // Updated to handle avatar
                            Log.d("MainContainer", "Attempting signup with email: $email and avatar: ${avatar != null}")
                            authViewModel.signup(username, email, password, avatar)
                        },
                        onSignInClick = {
                            deepSeaNavController.navController.navigate("login")
                        },
                        authViewModel = authViewModel,
                        onRegisterSuccess = {
                            Log.d("MainContainer", "Registration success, navigating to home/learn")
                            deepSeaNavController.navController.navigate("home/learn") {
                                popUpTo("welcome") { inclusive = true }
                            }
                        }
                    )
                }
                composable("login") {
                    LoginPage(
                        navController = deepSeaNavController,
                        authViewModel = authViewModel,
                        onLoginSuccess = {
                            Log.d("MainContainer", "Login success, navigating to learn-selection")
                        },
                        onSignInClick = { email, password ->
                            Log.d("MainContainer", "Attempting login with email: $email")
                            authViewModel.login(email, password, deepSeaNavController.navController)
                        }
                    )
                }
                composable("forgot-password") {
                    ForgotPasswordPage(deepSeaNavController.navController)
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