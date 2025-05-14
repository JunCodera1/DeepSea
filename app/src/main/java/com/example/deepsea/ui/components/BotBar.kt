package com.example.deepsea.ui.components

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.deepsea.R
import com.example.deepsea.ui.LocalNavAnimatedVisibilityScope
import com.example.deepsea.ui.navigation.rememberDeepSeaNavController
import com.example.deepsea.ui.nonSpatialExpressiveSpring
import com.example.deepsea.ui.theme.DeepSeaTheme


// Enum class with navigation info
enum class HomeSections(
    @StringRes val title: Int,
    val route: String,
    @DrawableRes val iconRes: Int,
    val primaryColor: Color,
    val secondaryColor: Color
) {
    HOME(
        R.string.home,
        "home",
        R.drawable.home_learn,
        Color(0xFF10805E),
        Color(0xFF38A78F)
    ),
    KANJI(
        R.string.home_kanji,
        "alphabet-screen",
        R.drawable.ic_kanji,
        Color(0xFF10805E),
        Color(0xFF38A78F)
    )
    ,
    REVIEW(
        R.string.home_review,
        "home/review",
        R.drawable.home_review,
        Color(0xFFC97C00),
        Color(0xFFE7A439)
    ),
    RANK(
        R.string.home_rank,
        "home/rank",
        R.drawable.home_rank,
        Color(0xFF00C6C9),
        Color(0xFF95DFDF)
    ),
    GAME(
        R.string.home_game,
        "home/game",
        R.drawable.home_game,
        Color(0xFF2951CD),
        Color(0xFF5273D7)
    ),
    PROFILE(
        R.string.home_profile,
        "home/profile/{userId}",
        R.drawable.home_profile,
        Color(0xFFBB2D72),
        Color(0xFFD1598D)
    )
}

// Modern bottom navigation colors
private val BottomNavPrimaryColor = Color(0xFF152238)
private val BottomNavSurfaceColor = Color(0xFF1E2B45)
private val BottomNavSelectedItemColor = Color.White
private val BottomNavUnselectedItemColor = Color(0x99FFFFFF)

@Composable
fun DeepSeaBottomBar(navController: NavController) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        shadowElevation = 16.dp,
        color = BottomNavPrimaryColor,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        val currentSection = HomeSections.entries.find { currentRoute?.contains(it.route) == true }

        val gradientColors = currentSection?.let {
            listOf(it.primaryColor.copy(alpha = 0.7f), it.secondaryColor.copy(alpha = 0.4f))
        } ?: listOf(Color.Transparent, Color.Transparent)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = gradientColors,
                        startY = 0f,
                        endY = 200f
                    )
                )
        ) {
            NavigationBar(
                containerColor = Color.Transparent,
                contentColor = BottomNavSelectedItemColor,
                tonalElevation = 0.dp,
                modifier = Modifier.fillMaxSize()
            ) {
                HomeSections.entries.forEach { section ->
                    val isSelected = currentRoute?.contains(section.route) == true
                    val iconScale by animateFloatAsState(
                        targetValue = if (isSelected) 1.2f else 1f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMedium
                        ),
                        label = "iconScale"
                    )

                    val iconAlpha by animateFloatAsState(
                        targetValue = if (isSelected) 1f else 0.6f,
                        animationSpec = tween(300),
                        label = "iconAlpha"
                    )

                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            if (currentRoute != section.route) {
                                val route = if (section.route.contains("{userId}")) {
                                    "home/profile/${302}"
                                } else {
                                    section.route
                                }
                                navController.navigate(route) {
                                    launchSingleTop = true
                                    restoreState = true
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                }
                            }
                        },
                        icon = {
                            Box(
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(CircleShape)
                                            .background(
                                                brush = Brush.radialGradient(
                                                    colors = listOf(
                                                        section.secondaryColor.copy(alpha = 0.3f),
                                                        section.primaryColor.copy(alpha = 0.1f)
                                                    )
                                                )
                                            )
                                    )
                                }

                                Icon(
                                    painter = painterResource(id = section.iconRes),
                                    contentDescription = stringResource(id = section.title),
                                    modifier = Modifier
                                        .size(28.dp)
                                        .scale(iconScale)
                                        .graphicsLayer {
                                            alpha = iconAlpha
                                        },
                                    tint = Color.Unspecified
                                )
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = BottomNavSelectedItemColor,
                            unselectedIconColor = BottomNavUnselectedItemColor,
                            selectedTextColor = section.primaryColor,
                            unselectedTextColor = BottomNavUnselectedItemColor,
                            indicatorColor = Color.Transparent
                        )
                    )
                }
            }
        }
    }
}


fun NavGraphBuilder.composableWithCompositionLocal(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    enterTransition: (
    @JvmSuppressWildcards
    AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?
    )? = {
        fadeIn(nonSpatialExpressiveSpring())
    },
    exitTransition: (
    @JvmSuppressWildcards
    AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?
    )? = {
        fadeOut(nonSpatialExpressiveSpring())
    },
    popEnterTransition: (
    @JvmSuppressWildcards
    AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?
    )? =
        enterTransition,
    popExitTransition: (
    @JvmSuppressWildcards
    AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?
    )? =
        exitTransition,
    content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit
) {
    composable(
        route,
        arguments,
        deepLinks,
        enterTransition,
        exitTransition,
        popEnterTransition,
        popExitTransition
    ) {
        CompositionLocalProvider(
            LocalNavAnimatedVisibilityScope provides this@composable
        ) {
            content(it)
        }
    }
}

@Preview
@Composable
private fun DeepSeaBottomNavPreview() {
    DeepSeaTheme {
        DeepSeaBottomBar(
            rememberDeepSeaNavController().navController
        )
    }
}