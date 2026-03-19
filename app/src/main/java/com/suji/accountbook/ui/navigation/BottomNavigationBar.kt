package com.suji.accountbook.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.suji.accountbook.ui.theme.PrimaryLight
import com.suji.accountbook.ui.theme.SurfaceLight

data class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val iconFilled: ImageVector,
    val title: String
)

private const val HOME_ROUTE = "Home"
private const val RECORD_ROUTE = "Record"
private const val ANALYSIS_ROUTE = "Analysis"
private const val SETTINGS_ROUTE = "Settings"

@Composable
fun BottomNavigationBar(
    navController: NavHostController
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val items = listOf(
        BottomNavItem(
            route = HOME_ROUTE,
            icon = Icons.Outlined.Home,
            iconFilled = Icons.Filled.Home,
            title = "首页"
        ),
        BottomNavItem(
            route = RECORD_ROUTE,
            icon = Icons.Outlined.Receipt,
            iconFilled = Icons.Filled.Receipt,
            title = "账单"
        ),
        BottomNavItem(
            route = ANALYSIS_ROUTE,
            icon = Icons.Outlined.Analytics,
            iconFilled = Icons.Filled.Analytics,
            title = "分析"
        ),
        BottomNavItem(
            route = SETTINGS_ROUTE,
            icon = Icons.Outlined.Settings,
            iconFilled = Icons.Filled.Settings,
            title = "设置"
        )
    )

    val shouldShowBottomBar = items.any { item ->
        currentDestination?.route?.contains(item.route) == true
    }

    AnimatedVisibility(
        visible = shouldShowBottomBar,
        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
    ) {
        NavigationBar {
            items.forEach { item ->
                val selected = currentDestination?.route?.contains(item.route) == true

                NavigationBarItem(
                    selected = selected,
                    onClick = {
                        if (!selected) {
                            navController.navigate(item.route) {
                                popUpTo(HOME_ROUTE) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = if (selected) item.iconFilled else item.icon,
                            contentDescription = item.title
                        )
                    },
                    label = { Text(item.title) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PrimaryLight,
                        unselectedIconColor = SurfaceLight.copy(alpha = 0.6f),
                        indicatorColor = PrimaryLight.copy(alpha = 0.1f)
                    )
                )
            }
        }
    }
}