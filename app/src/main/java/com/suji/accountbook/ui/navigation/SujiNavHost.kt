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
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.suji.accountbook.ui.about.AboutScreen
import com.suji.accountbook.ui.analysis.AnalysisScreen
import com.suji.accountbook.ui.home.HomeScreen
import com.suji.accountbook.ui.record.AddRecordScreen
import com.suji.accountbook.ui.record.EditRecordScreen
import com.suji.accountbook.ui.record.RecordScreen
import com.suji.accountbook.ui.settings.SettingsScreen

@Composable
fun SujiNavHost(
    navController: NavHostController = rememberNavController()
) {
    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController)
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
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Start,
                        animationSpec = tween(300, easing = EaseIn)
                    )
                },
                exitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Start,
                        animationSpec = tween(300, easing = EaseOut)
                    )
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

                composable(route = Screen.EditRecordBaseRoute) {
                    EditRecordScreen(
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
                        onNavigateToAbout = { navController.navigate(Screen.About.routeKey) }
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
