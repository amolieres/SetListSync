package com.amolieres.setlistync.feature.user.ui

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.amolieres.setlistync.feature.user.presentation.UserAuthRoute
import com.amolieres.setlistync.feature.main.presentation.MainScreenRoute

fun NavGraphBuilder.userGraph(
    navController: NavController
) {
    composable<UserAuthRoute> {
        UserAuthRoot(
            onNavigateToMain = {
                navController.navigate(MainScreenRoute) {
                    popUpTo<UserAuthRoute> { inclusive = false }
                }
            }
        )
    }
}
