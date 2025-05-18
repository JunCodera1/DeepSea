@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.example.deepsea.ui

import android.app.Application
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.deepsea.AI_assistant.VoiceAssistantScreen
import com.example.deepsea.data.api.RetrofitClient
import com.example.deepsea.data.api.UserProfileService
import com.example.deepsea.data.model.exercise.LessonResult
import com.example.deepsea.data.repository.UnitGuideRepository
import com.example.deepsea.data.repository.UserProfileRepository
import com.example.deepsea.data.repository.WordRepository
import com.example.deepsea.ui.components.DeepSeaBottomBar
import com.example.deepsea.ui.components.DeepSeaScaffold
import com.example.deepsea.ui.components.StreakScreen
import com.example.deepsea.ui.navigation.MainDestinations
import com.example.deepsea.ui.navigation.rememberDeepSeaNavController
import com.example.deepsea.ui.navigation.rememberDeepSeaScaffoldState
import com.example.deepsea.ui.screens.auth.ForgotPasswordPage
import com.example.deepsea.ui.screens.auth.LoginPage
import com.example.deepsea.ui.screens.auth.SignupPage
import com.example.deepsea.ui.screens.feature.daily.DailyPage
import com.example.deepsea.ui.screens.feature.game.GameScreen
import com.example.deepsea.ui.screens.feature.home.ErrorScreen
import com.example.deepsea.ui.screens.feature.home.GemsScreen
import com.example.deepsea.ui.screens.feature.home.HeartsScreen
import com.example.deepsea.ui.screens.feature.home.HomeScreen
import com.example.deepsea.ui.screens.feature.leaderboard.LeaderboardPage
import com.example.deepsea.ui.screens.feature.leaderboard.LoadingIndicator
import com.example.deepsea.ui.screens.feature.learn.JapaneseCharacterLearningScreen
import com.example.deepsea.ui.screens.feature.learn.LessonCompletedScreen
import com.example.deepsea.ui.screens.feature.learn.UnitGuideBookScreen
import com.example.deepsea.ui.screens.feature.listen.ShadowListeningScreen
import com.example.deepsea.ui.screens.feature.profile.PaymentScreen
import com.example.deepsea.ui.screens.feature.profile.ProfilePage
import com.example.deepsea.ui.screens.feature.review.MistakesScreen
import com.example.deepsea.ui.screens.feature.review.ReviewScreen
import com.example.deepsea.ui.screens.feature.review.StoryScreen
import com.example.deepsea.ui.screens.feature.review.WordsScreen
import com.example.deepsea.ui.screens.feature.settings.CoursesScreen
import com.example.deepsea.ui.screens.feature.settings.DeepSeaForSchoolsScreen
import com.example.deepsea.ui.screens.feature.settings.FeedbackScreen
import com.example.deepsea.ui.screens.feature.settings.HelpCenterScreen
import com.example.deepsea.ui.screens.feature.settings.NotificationsScreen
import com.example.deepsea.ui.screens.feature.settings.PreferencesScreen
import com.example.deepsea.ui.screens.feature.settings.PrivacySettingsScreen
import com.example.deepsea.ui.screens.feature.settings.ProfileScreen
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
import com.example.deepsea.ui.viewmodel.learn.GuideUiState
import com.example.deepsea.ui.viewmodel.learn.UnitGuideViewModel
import com.example.deepsea.ui.viewmodel.learn.UnitGuideViewModelFactory
import com.example.deepsea.ui.viewmodel.learn.WordViewModel
import com.example.deepsea.ui.viewmodel.survey.SurveySelectionViewModel
import com.example.deepsea.ui.viewmodel.survey.SurveyViewModelFactory
import com.example.deepsea.utils.LearningSessionManager
import com.example.deepsea.utils.SessionManager
import com.example.deepsea.utils.UserState
import timber.log.Timber

// CompositionLocal for SharedTransitionScope
val LocalSharedTransitionScope = compositionLocalOf<SharedTransitionScope?> { null }
val LocalNavAnimatedVisibilityScope = compositionLocalOf<androidx.compose.animation.AnimatedVisibilityScope?> { null }

@Composable
fun DeepSeaApp() {
    val navController = rememberDeepSeaNavController().navController
    val context = LocalContext.current
    val authViewModel: AuthViewModel = viewModel(
        factory = ViewModelProvider.AndroidViewModelFactory(context.applicationContext as Application)
    )

    DeepSeaTheme {
        SharedTransitionLayout {
            CompositionLocalProvider(LocalSharedTransitionScope provides this) {
                DeepSeaScaffold(
                    bottomBar = {
                        if (checkIfImportantRoute(getCurrentRoute(navController))) {
                            DeepSeaBottomBar(navController = navController)
                        }
                    },
                    snackBarHostState = rememberDeepSeaScaffoldState().snackBarHostState,
                ) { padding ->
                    AppNavHost(
                        navController = navController,
                        authViewModel = authViewModel,
                        modifier = Modifier.padding(padding)
                    )
                }
            }
        }
    }
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    modifier: Modifier = Modifier
) {
    val userState by authViewModel.userState.collectAsState()
    val isLoggingOut = authViewModel.isLoggingOut
    val skipAutoNavigation = authViewModel.skipAutoNavigation
    val currentRoute = getCurrentRoute(navController)

    // Handle auto-navigation
    LaunchedEffect(userState, isLoggingOut, skipAutoNavigation) {
        if (isLoggingOut || skipAutoNavigation) {
            Timber.d("Skipping auto-navigation (logout: $isLoggingOut, skip: $skipAutoNavigation)")
            return@LaunchedEffect
        }

        val isImportantRoute = checkIfImportantRoute(currentRoute)
        if (userState is UserState.NotLoggedIn && isImportantRoute && currentRoute != "welcome") {
            Timber.d("User not logged in, navigating to welcome")
            navController.navigate("welcome") {
                popUpTo(0) { inclusive = true }
                launchSingleTop = true
            }
        } else if (userState is UserState.LoggedIn && currentRoute == "welcome") {
            Timber.d("User logged in, navigating to home")
            navController.navigate("home") {
                popUpTo(0) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = "welcome",
        modifier = modifier
    ) {
        welcomeRoutes(navController, authViewModel)
        setupRoutes(navController)
        homeRoutes(navController)
        learningRoutes(navController)
        reviewRoutes(navController)
        settingsRoutes(navController, authViewModel)
    }
}

// Welcome and Authentication Routes
fun NavGraphBuilder.welcomeRoutes(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    composable("welcome") {
        WelcomePage(
            deepseaNavController = navController,
            authViewModel = authViewModel
        )
    }

    composable(
        route = "${MainDestinations.LOGIN_ROUTE}/{${MainDestinations.LOGIN_ID_KEY}}?origin={${MainDestinations.ORIGIN}}",
        arguments = listOf(
            navArgument(MainDestinations.LOGIN_ID_KEY) { type = NavType.LongType },
            navArgument(MainDestinations.ORIGIN) { type = NavType.StringType; nullable = true }
        )
    ) {
        LaunchedEffect(Unit) {
            authViewModel.resetSkipAutoNavigation()
        }
        LoginPage(
            deepseaNavController = navController,
            authViewModel = authViewModel,
            onLoginSuccess = {
                Timber.d("Login success, navigating to home")
                navController.navigate("home") {
                    popUpTo("welcome") { inclusive = true }
                }
            },
            onSignInClick = { email, password ->
                Timber.d("Attempting login")
                authViewModel.login(email, password, navController)
            }
        )
    }

    composable(
        route = "${MainDestinations.SIGNUP_ROUTE}/{${MainDestinations.SIGNUP_ID_KEY}}?origin={${MainDestinations.ORIGIN}}",
        arguments = listOf(
            navArgument(MainDestinations.SIGNUP_ID_KEY) { type = NavType.LongType },
            navArgument(MainDestinations.ORIGIN) { type = NavType.StringType; nullable = true }
        )
    ) {
        SignupPage(
            navController = navController,
            onSignUpClick = { name, username, email, password, avatar ->
                Timber.d("Attempting signup with name: $name")
                authViewModel.signup(name, username, email, password, avatar)
            },
            onSignInClick = { navController.navigate("${MainDestinations.LOGIN_ROUTE}/0") },
            authViewModel = authViewModel,
            onRegisterSuccess = {
                Timber.d("Registration success, navigating to home")
                navController.navigate("home") {
                    popUpTo("welcome") { inclusive = true }
                }
            }
        )
    }

    composable("forgot-password") {
        ForgotPasswordPage(navController)
    }
}

// Setup Routes
fun NavGraphBuilder.setupRoutes(navController: NavHostController) {
    composable("path_selection") {
        val context = LocalContext.current
        val sessionManager = SessionManager(context)
        val userProfileService: UserProfileService = RetrofitClient.userProfileService
        PathSelectionFlowPage(
            navController = navController,
            sessionManager = sessionManager,
            pathService = userProfileService
        )
    }

    composable("daily-goal-selection") {
        val context = LocalContext.current
        val sessionManager = SessionManager(context)
        DailyGoalSelectionPage(
            navController = navController,
            sessionManager = sessionManager
        )
    }

    composable("survey-selection") {
        val context = LocalContext.current
        val sessionManager = SessionManager(context)
        val userProfileRepository = UserProfileRepository(RetrofitClient.userProfileService)
        val surveySelectionViewModel: SurveySelectionViewModel = viewModel(
            factory = SurveyViewModelFactory(userProfileRepository)
        )
        SurveySelectionPage(
            navController = navController,
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
            navController = navController,
            sessionManager = sessionManager,
            languageSelectionViewModel = languageSelectionViewModel
        )
    }
}

// Home Routes
fun NavGraphBuilder.homeRoutes(navController: NavHostController) {
    composable("home") {
        HomeScreen(navController = navController)
    }

    composable("home/daily") {
        DailyPage()
    }

    composable("home/rank") {
        LeaderboardPage()
    }

    composable("home/profile/{userId}") {
        val context = LocalContext.current
        val sessionManager = SessionManager(context)
        ProfilePage(
            sessionManager = sessionManager,
            paddingValues = PaddingValues(0.dp),
            onNavigateToSettings = { navController.navigate("settings") },
            onNavigateToPayment = { navController.navigate("payment") }
        )
    }

    composable("payment") {
        val context = LocalContext.current
        val sessionManager = SessionManager(context)
        PaymentScreen(
            sessionManager = sessionManager,
            onBackClick = { navController.popBackStack() }
        )
    }

    composable("home/streak") {
        StreakScreen(
            currentStreak = 5,
            onDismissRequest = { navController.navigate("home") }
        )
    }

    composable("home/gems") {
        GemsScreen(navController = navController)
    }

    composable("home/hearts") {
        HeartsScreen(navController = navController)
    }

    composable("home/game") {
        GameScreen()
    }

    composable("home/voice_assistant") {
        VoiceAssistantScreen()
    }

    composable("alphabet-screen") {
        JapaneseCharacterLearningScreen()
    }
}

// Learning Routes
fun NavGraphBuilder.learningRoutes(navController: NavHostController) {
    composable(
        route = "learning_session/{lessonId}",
        arguments = listOf(navArgument("lessonId") { type = NavType.LongType })
    ) { backStackEntry ->
        val lessonId = backStackEntry.arguments?.getLong("lessonId") ?: 1L
        LearningSessionManager(
            lessonId = lessonId,
            navController = navController,
            onComplete = {}
        )
    }

    composable(
        route = "lesson_completed/{xp}/{time}/{accuracy}/{lessonId}",
        arguments = listOf(
            navArgument("xp") { type = NavType.IntType },
            navArgument("time") { type = NavType.StringType },
            navArgument("accuracy") { type = NavType.IntType },
            navArgument("lessonId") { type = NavType.LongType }
        )
    ) { backStackEntry ->
        val xp = backStackEntry.arguments?.getInt("xp") ?: 0
        val time = backStackEntry.arguments?.getString("time") ?: "0:00"
        val accuracy = backStackEntry.arguments?.getInt("accuracy") ?: 100
        val lessonId = backStackEntry.arguments?.getLong("lessonId") ?: 1L
        LessonCompletedScreen(
            navController = navController,
            lessonResult = LessonResult(xp = xp, time = time, accuracy = accuracy),
            lessonId = lessonId
        )
    }

    composable(
        route = "unit_guide/{unitId}",
        arguments = listOf(navArgument("unitId") { type = NavType.LongType })
    ) { backStackEntry ->
        val unitId = backStackEntry.arguments?.getLong("unitId") ?: 0L
        val viewModel: UnitGuideViewModel = viewModel(
            factory = UnitGuideViewModelFactory(UnitGuideRepository(RetrofitClient.unitGuideService))
        )
        LaunchedEffect(unitId) {
            viewModel.loadUnitGuide(unitId)
        }
        val guideState = viewModel.guideState.collectAsState().value
        when (guideState) {
            is GuideUiState.Loading -> LoadingIndicator(Color.Red)
            is GuideUiState.Success -> UnitGuideBookScreen(
                guideData = guideState.data,
                onBack = { navController.popBackStack() },
                onPlayAudio = { audioUrl -> viewModel.playAudio(audioUrl) }
            )
            is GuideUiState.Error -> ErrorScreen(message = guideState.message)
            else -> ErrorScreen(message = "Unknown guide state")
        }
    }
}

// Review Routes
fun NavGraphBuilder.reviewRoutes(navController: NavHostController) {
    composable("home/review") {
        ReviewScreen(
            onMistakesClick = { navController.navigate("mistake-screen") },
            onStoriesClick = { navController.navigate("story-screen") },
            onWordsClick = { navController.navigate("words-screen") },
            onListenClick = { navController.navigate("listen-screen") }
        )
    }

    composable("story-screen") {
        StoryScreen(onBackClick = { navController.popBackStack() })
    }

    composable("words-screen") {
        val repository = WordRepository(RetrofitClient.wordApiService)
        val viewModel = WordViewModel(repository)
        WordsScreen(viewModel)
    }

    composable("mistake-screen") {
        MistakesScreen(onBackClick = { navController.popBackStack() })
    }

    composable("listen-screen") {
        ShadowListeningScreen(onBackClick = { navController.popBackStack() })
    }
}

// Settings Routes
fun NavGraphBuilder.settingsRoutes(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    composable("settings") {
        val context = LocalContext.current
        val localAuthViewModel: AuthViewModel = viewModel(
            factory = ViewModelProvider.AndroidViewModelFactory(context.applicationContext as Application)
        )
        var showLogoutDialog by remember { mutableStateOf(false) }

        if (showLogoutDialog) {
            LogoutConfirmationDialog(
                onConfirm = {
                    Timber.d("Logout confirmed")
                    localAuthViewModel.logout(navController = navController)
                    showLogoutDialog = false
                },
                onDismiss = { showLogoutDialog = false }
            )
        }

        SettingsPage(
            onBackPressed = { navController.popBackStack() },
            onPreferencesClick = { navController.navigate("settings/preferences") },
            onProfileClick = { navController.navigate("settings/profile") },
            onNotificationsClick = { navController.navigate("settings/notifications") },
            onCoursesClick = { navController.navigate("settings/courses") },
            onDeepSeaForSchoolsClick = { navController.navigate("settings/schools") },
            onPrivacySettingsClick = { navController.navigate("settings/privacy") },
            onHelpCenterClick = { navController.navigate("settings/help") },
            onFeedbackClick = { navController.navigate("settings/feedback") },
            onSignOut = {
                Timber.d("Sign out clicked, showing logout dialog")
                showLogoutDialog = true
            }
        )
    }

    composable("settings/preferences") {
        PreferencesScreen(onBackClick = { navController.popBackStack() })
    }

    composable("settings/profile") {
        ProfileScreen(onBackClick = { navController.popBackStack() })
    }

    composable("settings/notifications") {
        NotificationsScreen(onBackClick = { navController.popBackStack() })
    }

    composable("settings/courses") {
        CoursesScreen(onBackClick = { navController.popBackStack() })
    }

    composable("settings/schools") {
        DeepSeaForSchoolsScreen(onBackClick = { navController.popBackStack() })
    }

    composable("settings/privacy") {
        PrivacySettingsScreen(onBackClick = { navController.popBackStack() })
    }

    composable("settings/help") {
        HelpCenterScreen(onBackClick = { navController.popBackStack() })
    }

    composable("settings/feedback") {
        FeedbackScreen(onBackClick = { navController.popBackStack() })
    }
}

@Composable
fun LogoutConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Sign Out", style = MaterialTheme.typography.headlineSmall) },
        text = { Text("Are you sure you want to sign out?", style = MaterialTheme.typography.bodyMedium) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Sign Out", color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        textContentColor = MaterialTheme.colorScheme.onSurface
    )
}

// Utility Functions
@Composable
private fun getCurrentRoute(navController: NavHostController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}

private fun checkIfImportantRoute(currentRoute: String?): Boolean {
    return currentRoute != "login" &&
            currentRoute != "signup" &&
            currentRoute != "forgot-password" &&
            currentRoute != "welcome" &&
            currentRoute != "language-selection" &&
            currentRoute != "survey-selection" &&
            currentRoute != "daily-goal-selection" &&
            currentRoute != "path_selection" &&
            currentRoute != "home/streak" &&
            currentRoute?.startsWith("learning_session") != true &&
            currentRoute?.startsWith("lesson_completed") != true
}

fun <T> nonSpatialExpressiveSpring() = spring<T>(
    dampingRatio = 1f,
    stiffness = 1600f
)