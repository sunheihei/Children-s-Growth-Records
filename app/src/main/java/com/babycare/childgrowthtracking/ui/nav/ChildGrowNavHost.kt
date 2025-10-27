package com.babycare.childgrowthtracking.ui.nav

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument


import com.babycare.childgrowthtracking.ui.view.ChildrenInfoView
import com.babycare.childgrowthtracking.ui.view.DiaryEditView
import com.babycare.childgrowthtracking.ui.view.GrowthDataCharts
import com.babycare.childgrowthtracking.ui.view.GrowthDiaryView
import com.babycare.childgrowthtracking.ui.view.GrowthRecordView
import com.babycare.childgrowthtracking.ui.view.GuideView
import com.babycare.childgrowthtracking.ui.view.ChildrenView
import com.babycare.childgrowthtracking.ui.view.GrowthData
import com.babycare.childgrowthtracking.ui.view.PrivacyTermService
import com.babycare.childgrowthtracking.ui.view.Setting
import com.babycare.childgrowthtracking.ui.view.SplashView
import com.babycare.childgrowthtracking.utils.GROWTH_DATA
import com.babycare.childgrowthtracking.utils.GROWTH_DATA_CHARTS
import com.babycare.childgrowthtracking.utils.GROWTH_DIARY
import com.babycare.childgrowthtracking.utils.GROWTH_DIARY_EDIT
import com.babycare.childgrowthtracking.utils.GROWTH_RECORD
import com.babycare.childgrowthtracking.utils.NAV_ADD_CHILDREN
import com.babycare.childgrowthtracking.utils.NAV_EDIT_CHILDREN
import com.babycare.childgrowthtracking.utils.NAV_GUIDE
import com.babycare.childgrowthtracking.utils.NAV_HOME
import com.babycare.childgrowthtracking.utils.NAV_LAUNCH
import com.babycare.childgrowthtracking.utils.NAV_SETTING
import com.babycare.childgrowthtracking.utils.PRIVACY_TERM_SERVICE


// 导航主机
@Composable
fun ChildGrowNavHost() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = NAV_LAUNCH) {
        composable(NAV_LAUNCH) {
            SplashView(enterNext = {
                navController.navigate(NAV_HOME) {
                    popUpTo(navController.graph.startDestinationId) {
                        inclusive = true
                    }
                }
            }, navigateToGuideView = {
                navController.navigate(NAV_GUIDE) {
                    popUpTo(navController.graph.startDestinationId) {
                        inclusive = true
                    }
                }
            })
        }
        composable(NAV_GUIDE) {
            GuideView(enterNext = {
                navController.navigate(NAV_HOME) {
                    popUpTo(navController.graph.startDestinationId) {
                        inclusive = true
                    }
                }
            })
        }
        composable(NAV_HOME) {
            ChildrenView(
                addChildren = {
                navController.navigate(NAV_ADD_CHILDREN)
            },
                onEditInfoClick = { child -> navController.navigate("$NAV_EDIT_CHILDREN/${child.id}") },
                onGrowthDataClick = { child -> navController.navigate("$GROWTH_DATA/${child.id}") },
                onGrowthDiaryClick = { child -> navController.navigate("$GROWTH_DIARY/${child.id}") },
                navigateSetting = {
                    navController.navigate("$NAV_SETTING")
                })
        }
        composable(
            NAV_ADD_CHILDREN,
            enterTransition = { slideInVertically(animationSpec = tween(500)) { it } },
            exitTransition = { slideOutVertically(animationSpec = tween(500)) { it } }) {
            ChildrenInfoView(

                navigateBack = { navController.popBackStack() }, checkClick = {
                    navController.popBackStack()
                })
        }
        composable(
            "$NAV_EDIT_CHILDREN/{childId}",
            arguments = listOf(navArgument("childId") { type = NavType.IntType })
        ) { backStackEntry ->
            val childId = backStackEntry.arguments?.getInt("childId") ?: -1
            ChildrenInfoView(
                childId = childId,
                navigateBack = { navController.popBackStack() },
                checkClick = {
                    navController.popBackStack()
                })
        }
        composable(
            "$GROWTH_DATA/{childId}",
            arguments = listOf(navArgument("childId") { type = NavType.IntType })
        ) { backStackEntry ->
            val childId = backStackEntry.arguments?.getInt("childId") ?: -1
            GrowthData(
                childId = childId,
                navigateBack = { navController.popBackStack() },
                navigateCharts = { navController.navigate("$GROWTH_DATA_CHARTS/${childId}") },
                addGrowthRecord = { navController.navigate("$GROWTH_RECORD/${childId}/${-1}") },
                editGrowthRecord = { growthRecordId -> navController.navigate("$GROWTH_RECORD/${-1}/${growthRecordId}") })
        }
        composable(
            "$GROWTH_RECORD/{childId}/{id}",
            arguments = listOf(
                navArgument("childId") { type = NavType.IntType },
                navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val childId = backStackEntry.arguments?.getInt("childId") ?: -1
            val id = backStackEntry.arguments?.getInt("id") ?: -1
            GrowthRecordView(
                id = id,
                childId = childId,
                onSaveSuccess = { navController.popBackStack() },
                navigateBack = { navController.popBackStack() })
        }
        composable(
            "$GROWTH_DATA_CHARTS/{childId}",
            arguments = listOf(navArgument("childId") { type = NavType.IntType })
        ) { backStackEntry ->
            val childId = backStackEntry.arguments?.getInt("childId") ?: -1
            GrowthDataCharts(
                childId = childId, navigateBack = { navController.popBackStack() })
        }
        composable(
            "$GROWTH_DIARY/{childId}",
            arguments = listOf(navArgument("childId") { type = NavType.IntType })
        ) { backStackEntry ->
            val childId = backStackEntry.arguments?.getInt("childId") ?: -1
            GrowthDiaryView(
                childId = childId,
                navigateBack = { navController.popBackStack() },
                addGrowthDiary = { childId -> navController.navigate("$GROWTH_DIARY_EDIT/${childId}/${-1}") },
                diaryItemClick = { diaryId -> navController.navigate("$GROWTH_DIARY_EDIT/${childId}/${diaryId}") })
        }

        composable(
            "$GROWTH_DIARY_EDIT/{childId}/{id}",
            arguments = listOf(
                navArgument("childId") { type = NavType.IntType },
                navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val childId = backStackEntry.arguments?.getInt("childId") ?: -1
            val id = backStackEntry.arguments?.getInt("id") ?: -1
            DiaryEditView(
                id = id,
                childId = childId,
                onOperateSuccess = { navController.popBackStack() },
                navigateBack = { navController.popBackStack() })
        }
        composable(
            route = NAV_SETTING,
            enterTransition = { slideInHorizontally(animationSpec = tween(500)) { it } },
            exitTransition = { slideOutHorizontally(animationSpec = tween(500)) { it } }) {
            Setting(navigateBack = { navController.popBackStack() }, navigateToPrivacy = { ps ->
                navController.navigate("$PRIVACY_TERM_SERVICE/${ps}")
            })
        }
        composable(
            route = "$PRIVACY_TERM_SERVICE/{ps}",
            arguments = listOf(navArgument("ps") { type = NavType.IntType })
        ) { backStackEntry ->
            val ps = backStackEntry.arguments?.getInt("ps") ?: -1
            PrivacyTermService(ps,navigateBack = { navController.popBackStack() })
        }

    }
}