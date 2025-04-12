package com.example.deepsea.ui.home

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.getValue
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
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.deepsea.R
import com.example.deepsea.ui.navigation.rememberDeepSeaNavController
import com.example.deepsea.ui.theme.DeepSeaTheme


// Helper function to determine the index of the route
private fun getRouteIndex(route: String): Int {
    return when {
        route.contains("learn") -> 0
        route.contains("daily") -> 1
        route.contains("rank") -> 2
        route.contains("game") -> 3
        route.contains("profile") -> 4
        else -> -1
    }
}


@Composable
fun NavHostContainer(
    navController: NavHostController,
    padding: PaddingValues
) {
    NavHost(
        navController = navController,
        startDestination = "home/learn",
        modifier = Modifier.padding(paddingValues = padding),
        builder = {
            composable("home/learn") {
                // Content for learn screen
            }

            composable("home/daily") {
                // Content for daily screen
            }

            composable("home/rank") {
                // Content for rank screen
            }

            composable("home/profile") {
                // Content for profile screen
            }

            composable("home/game") {
                // Content for game screen
            }
        })
}
// Enum class with navigation info
enum class HomeSections(
    @StringRes val title: Int,
    val route: String,
    @DrawableRes val iconRes: Int,
    val primaryColor: Color,
    val secondaryColor: Color
) {
    LEARN(
        R.string.home_learn,
        "home/learn",
        R.drawable.home_learn,
        Color(0xFF10805E),
        Color(0xFF38A78F)
    ),
    DAILY(
        R.string.home_daily,
        "home/daily",
        R.drawable.home_daily,
        Color(0xFF1E8A44),
        Color(0xFF4BAD6C)
    ),
    RANK(
        R.string.home_rank,
        "home/rank",
        R.drawable.home_rank,
        Color(0xFFC97C00),
        Color(0xFFE7A439)
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
        "home/profile",
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
            .height(72.dp),
        shadowElevation = 16.dp,
        color = BottomNavPrimaryColor,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        // Get current section
        val currentSection = HomeSections.entries.find { it.route == currentRoute }

        // Gradient based on current section
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
                    val isSelected = currentRoute == section.route
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
                                navController.navigate(section.route) {
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
                                // Background indicator for selected item
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

                                // Icon with animations
                                Icon(
                                    painter = painterResource(id = section.iconRes),
                                    contentDescription = stringResource(id = section.title),
                                    modifier = Modifier
                                        .size(28.dp)
                                        .scale(iconScale)
                                        .graphicsLayer {
                                            alpha = iconAlpha
                                        },
                                    tint = Color.Unspecified // Quan trọng: điều này sẽ giữ nguyên màu vector
                                )
                            }
                        },
                        label = {
                            Text(
                                text = stringResource(id = section.title),
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) section.primaryColor else BottomNavUnselectedItemColor,
                                maxLines = 1
                            )
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

@Preview
@Composable
private fun DeepSeaBottomNavPreview() {
    DeepSeaTheme {
        DeepSeaBottomBar(
            rememberDeepSeaNavController().navController
        )
    }
}