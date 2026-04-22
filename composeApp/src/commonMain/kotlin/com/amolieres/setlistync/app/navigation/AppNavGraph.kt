package com.amolieres.setlistync.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.amolieres.setlistync.feature.band.ui.bandGraph
import com.amolieres.setlistync.feature.main.ui.mainGraph
import com.amolieres.setlistync.feature.settings.ui.settingsGraph
import com.amolieres.setlistync.feature.user.presentation.UserAuthRoute
import com.amolieres.setlistync.feature.user.ui.userGraph

@Composable
fun AppNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: Any = UserAuthRoute
) {
    NavHost(navController = navController, startDestination = startDestination, modifier = modifier) {
        userGraph(navController)
        mainGraph(navController)
        settingsGraph(navController)
        bandGraph(navController)
    }
}
