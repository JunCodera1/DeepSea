@file:Suppress("UNCHECKED_CAST")

package com.example.deepsea.ui

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
import com.example.deepsea.ui.components.rememberDeepSeaScaffoldState
import com.example.deepsea.ui.home.DeepSeaBottomBar
import com.example.deepsea.ui.home.composableWithCompositionLocal
import com.example.deepsea.ui.navigation.MainDestinations
import com.example.deepsea.ui.navigation.rememberDeepSeaNavController
import com.example.deepsea.ui.screens.DailyPage
import com.example.deepsea.ui.screens.LearnPage
import com.example.deepsea.ui.screens.LoginPage

import com.example.deepsea.ui.screens.ProfilePage
import com.example.deepsea.ui.screens.RankPage
import com.example.deepsea.ui.screens.SignupPage
import com.example.deepsea.ui.screens.WelcomePage
import com.example.deepsea.ui.theme.DeepSeaTheme
import com.example.deepsea.ui.viewmodel.AuthViewModel
import com.example.deepsea.utils.LoginState
import com.example.deepsea.utils.UserState

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview
@Composable
fun DeepSeaApp() {
    val deepSeaNavController = rememberDeepSeaNavController()
    val context = LocalContext.current
    val authViewModel: AuthViewModel = viewModel(
        factory = ViewModelProvider.AndroidViewModelFactory(context.applicationContext as android.app.Application)
    )

    DeepSeaTheme {
        SharedTransitionLayout {
            CompositionLocalProvider(LocalSharedTransitionScope provides this) {
                NavHost(
                    navController = deepSeaNavController.navController,
                    startDestination = "login"
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
                                deepSeaNavController.navController.navigate(MainDestinations.HOME_ROUTE) {
                                    // Clear back stack to prevent going back to login screen
                                    popUpTo(MainDestinations.HOME_ROUTE) { inclusive = true }
                                }
                            },
                            authViewModel = authViewModel
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
                            onSignUpClick = { username, email, password ->
                                {
                                    authViewModel.signup(
                                        username = username,
                                        email = email,
                                        password = password
                                    )
                                }
                            },
                            onSignInClick = {
                                // Navigate to login page
                                deepSeaNavController.navController.navigate("${MainDestinations.LOGIN_ROUTE}/0")
                            },
                            authViewModel = authViewModel,
                            onRegisterSuccess = {
                                // Navigate to home screen after successful registration
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
    val isAuthRoute: Boolean = currentRoute != "login" && currentRoute != "signup" && currentRoute != "welcome"

    val userState by authViewModel.userState.collectAsState()


    LaunchedEffect(userState, currentRoute) {
        if (userState is UserState.NotLoggedIn && isAuthRoute && currentRoute != "welcome") {
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
                composable("signup") {
                    SignupPage(
                        navController = nestedNavController,
                        onSignUpClick = { username, email, password ->
                            authViewModel.signup(username, email, password)
                        },
                        onSignInClick = {
                            nestedNavController.navController.navigate("login")
                        },
                        authViewModel = authViewModel,
                        onRegisterSuccess = {
                            nestedNavController.navController.navigate("home/learn") {
                                popUpTo("welcome") { inclusive = true }
                            }
                        }
                    )
                }
                composable("login") {
                    val loginState by authViewModel.loginState.collectAsState()

                    if (loginState is LoginState.Success) {
                        authViewModel.resetLoginState()

                        nestedNavController.navController.navigate("home/learn") {
                            popUpTo("welcome") { inclusive = true }
                        }
                    }

                    LoginPage(
                        navController = nestedNavController,
                        authViewModel = authViewModel,
                        onLoginSuccess = {
                            TODO()
                        }
                    )
                }
                composable("home/learn") {
                    // Load dashboard data when entering the main area
                    authViewModel.loadDashboard()
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

                    ProfilePage(
                        userState = userState,
                        onLogout = {
                            authViewModel.logout()
                            nestedNavController.navController.navigate("welcome") {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    )
                }

            })



    }
    fun <T> nonSpatialExpressiveSpring() = spring<T>(
        dampingRatio = 1f,
        stiffness = 1600f
    )

}
@OptIn(ExperimentalSharedTransitionApi::class)
val LocalSharedTransitionScope = compositionLocalOf<SharedTransitionScope?> { null }
val LocalNavAnimatedVisibilityScope = compositionLocalOf<AnimatedVisibilityScope?> { null }