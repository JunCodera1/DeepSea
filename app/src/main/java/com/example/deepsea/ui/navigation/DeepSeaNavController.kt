package com.example.deepsea.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.deepsea.ui.theme.ProvideDeepSeaColors

object MainDestinations {
    const val HOME_ROUTE = "home"
    const val LOGIN_ROUTE = "login"
    const val SIGNUP_ROUTE = "signup"
    const val FORGOT_PASSWORD_ROUTE = "forgotPassword"
    const val VERIFY_EMAIL_ROUTE = "verifyEmail"
    const val RESET_PASSWORD_ROUTE = "resetPassword"
}

@Composable
fun rememberDeepSeaNavController(
    navController: NavHostController = rememberNavController()
): DeepSeaNavController = remember(navController) {
    DeepSeaNavController(navController)
}

class DeepSeaNavController(
    val navController: NavHostController
){
    fun upPress(){
        navController.navigateUp()
    }

    fun navigateToBottomBarRoute(route: String){
        if(route != navController.currentDestination?.route){
            navController.navigate(route){
                launchSingleTop = true
                restoreState = true

                //makes going back to the start destination when pressing back in any other bottom tab.
                popUpTo(findStartDestination(navController.graph).id){
                    saveState = true
                }
            }
        }
    }

    fun navigateToSignUp(signupId: Long, origin: String, from: NavBackStackEntry){
        if(from.lifecycleIsResumed())
            navController.navigate("${MainDestinations.SIGNUP_ROUTE}")
    }

    fun navigateToLogin(loginId: Long, origin: String, from: NavBackStackEntry){
        if(from.lifecycleIsResumed())
            navController.navigate("${MainDestinations.LOGIN_ROUTE}")
    }

    fun navigateToForgotPassword(forgotPasswordId: Long, origin: String, from: NavBackStackEntry){
        if(from.lifecycleIsResumed())
            navController.navigate("${MainDestinations.FORGOT_PASSWORD_ROUTE}/$forgotPasswordId?origin=$origin")
    }

    fun navigateToVerifyEmail(verifyEmailId: Long, origin: String, from: NavBackStackEntry){
        if(from.lifecycleIsResumed())
            navController.navigate("${MainDestinations.LOGIN_ROUTE}")
    }

    fun navigateToResetPassword(resetPasswordId: Long, origin: String, from: NavBackStackEntry){
        if(from.lifecycleIsResumed())
            navController.navigate("${MainDestinations.FORGOT_PASSWORD_ROUTE}/$resetPasswordId?origin=$origin")
    }
}

/**
 * If the lifecycle is not resumed it means this NavBackStackEntry already processed a nav event.
 *
 * This is used to de-duplicate navigation events.
 */
private fun NavBackStackEntry.lifecycleIsResumed() =
    this.lifecycle.currentState == Lifecycle.State.RESUMED

private val NavGraph.startDestination: NavDestination?
    get() = findNode(startDestinationId)

private tailrec fun findStartDestination(graph: NavDestination): NavDestination{
    return if (graph is NavGraph) findStartDestination(graph.startDestination!!) else graph
}