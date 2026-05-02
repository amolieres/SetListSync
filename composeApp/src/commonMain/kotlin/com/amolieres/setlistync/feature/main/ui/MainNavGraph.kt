package com.amolieres.setlistync.feature.main.ui

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.amolieres.setlistync.feature.band.creation.presentation.BandCreationRoute
import com.amolieres.setlistync.feature.band.detail.presentation.BandDetailRoute
import com.amolieres.setlistync.feature.main.presentation.MainScreenRoute
import com.amolieres.setlistync.feature.settings.presentation.SettingsRoute
import com.amolieres.setlistync.feature.user.presentation.UserAuthRoute

fun NavGraphBuilder.mainGraph(
    navController: NavController
) {
    composable<MainScreenRoute> {
        MainRoot(
            onNavigateToLogin = {
                navController.navigate(UserAuthRoute) {
                    popUpTo<MainScreenRoute> { inclusive = false }
                }
            },
            onNavigateToSettings = { navController.navigate(SettingsRoute) },
            onNavigateToBandDetail = { bandId -> navController.navigate(BandDetailRoute(bandId)) },
            onNavigateToBandCreation = { navController.navigate(BandCreationRoute) }
        )
    }
}
