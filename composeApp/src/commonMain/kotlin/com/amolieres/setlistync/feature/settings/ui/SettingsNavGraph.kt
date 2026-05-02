package com.amolieres.setlistync.feature.settings.ui

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.amolieres.setlistync.feature.main.presentation.MainScreenRoute
import com.amolieres.setlistync.feature.settings.presentation.SettingsRoute
import com.amolieres.setlistync.feature.user.presentation.UserAuthRoute

fun NavGraphBuilder.settingsGraph(
    navController: NavController
) {
    composable<SettingsRoute>(
        enterTransition = { slideInHorizontally { it } },
        popExitTransition = { slideOutHorizontally { it } }
    ) {
        SettingsRoot(
            onNavigateBack = { navController.popBackStack() },
            onNavigateToLogin = {
                navController.navigate(UserAuthRoute) {
                    popUpTo<MainScreenRoute> { inclusive = true }
                }
            }
        )
    }
}
