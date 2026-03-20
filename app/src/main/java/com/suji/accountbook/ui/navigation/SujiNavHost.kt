package com.suji.accountbook.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.suji.accountbook.ui.about.AboutScreen
import com.suji.accountbook.ui.analysis.AnalysisScreen
import com.suji.accountbook.ui.home.HomeScreen
import com.suji.accountbook.ui.record.AddRecordScreen
import com.suji.accountbook.ui.record.EditRecordScreen
import com.suji.accountbook.ui.record.RecordScreen
import com.suji.accountbook.ui.settings.AISettingsScreen
import com.suji.accountbook.ui.settings.SettingsScreen

private val bottomNavRoutes = listOf("Home", "Record", "Analysis", "Settings")

private fun getRouteIndex(route: String?): Int {
    if (route == null) return -1
    return bottomNavRoutes.indexOfFirst { route.contains(it) }
}

@Composable
fun SujiNavHost(
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    var previousIndex by remember { mutableIntStateOf(0) }
    val currentIndex = getRouteIndex(currentRoute)
    
    LaunchedEffect(currentIndex) {
        if (currentIndex >= 0) {
            previousIndex = currentIndex
        }
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                navController = navController,
                onAddRecordClick = {
                    navController.navigate(Screen.AddRecord.routeKey)
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            NavHost(
                navController = navController,
                startDestination = Screen.Home.routeKey,
                enterTransition = {
                    val targetIdx = getRouteIndex(targetState.destination.route)
                    val initialIdx = getRouteIndex(initialState.destination.route)
                    
                    if (targetIdx >= 0 && initialIdx >= 0) {
                        if (targetIdx > initialIdx) {
                            slideIntoContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Start,
                                animationSpec = tween(300, easing = EaseIn)
                            )
                        } else {
                            slideIntoContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.End,
                                animationSpec = tween(300, easing = EaseIn)
                            )
                        }
                    } else {
                        slideIntoContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Start,
                            animationSpec = tween(300, easing = EaseIn)
                        )
                    }
                },
                exitTransition = {
                    val targetIdx = getRouteIndex(targetState.destination.route)
                    val initialIdx = getRouteIndex(initialState.destination.route)
                    
                    if (targetIdx >= 0 && initialIdx >= 0) {
                        if (targetIdx > initialIdx) {
                            slideOutOfContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Start,
                                animationSpec = tween(300, easing = EaseOut)
                            )
                        } else {
                            slideOutOfContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.End,
                                animationSpec = tween(300, easing = EaseOut)
                            )
                        }
                    } else {
                        slideOutOfContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Start,
                            animationSpec = tween(300, easing = EaseOut)
                        )
                    }
                },
                popEnterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.End,
                        animationSpec = tween(300, easing = EaseIn)
                    )
                },
                popExitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.End,
                        animationSpec = tween(300, easing = EaseOut)
                    )
                }
            ) {
                composable(route = Screen.Home.routeKey) {
                    HomeScreen(
                        onNavigateToRecord = { navController.navigate(Screen.Record.routeKey) },
                        onNavigateToAddRecord = { navController.navigate(Screen.AddRecord.routeKey) },
                        onNavigateToAnalysis = { navController.navigate(Screen.Analysis.routeKey) },
                        onNavigateToSettings = { navController.navigate(Screen.Settings.routeKey) }
                    )
                }

                composable(route = Screen.Record.routeKey) {
                    RecordScreen(
                        onNavigateBack = { navController.popBackStack() },
                        onNavigateToAddRecord = { navController.navigate(Screen.AddRecord.routeKey) },
                        onNavigateToEditRecord = { recordId ->
                            navController.navigate(Screen.EditRecord(recordId).routeKey)
                        }
                    )
                }

                composable(route = Screen.AddRecord.routeKey) {
                    AddRecordScreen(
                        onNavigateBack = { navController.popBackStack() }
                    )
                }

                composable(
                    route = "EditRecord/{recordId}",
                    arguments = listOf(
                        navArgument("recordId") { type = NavType.LongType }
                    )
                ) { backStackEntry ->
                    val recordId = backStackEntry.arguments?.getLong("recordId") ?: -1L
                    EditRecordScreen(
                        recordId = recordId,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }

                composable(route = Screen.Analysis.routeKey) {
                    AnalysisScreen(
                        onNavigateBack = { navController.popBackStack() }
                    )
                }

                composable(route = Screen.Settings.routeKey) {
                    SettingsScreen(
                        onNavigateBack = { navController.popBackStack() },
                        onNavigateToAbout = { navController.navigate(Screen.About.routeKey) },
                        onNavigateToAISettings = { navController.navigate(Screen.AISettings.routeKey) }
                    )
                }

                composable(route = Screen.AISettings.routeKey) {
                    AISettingsScreen(
                        onNavigateBack = { navController.popBackStack() }
                    )
                }

                composable(route = Screen.About.routeKey) {
                    AboutScreen(
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}
