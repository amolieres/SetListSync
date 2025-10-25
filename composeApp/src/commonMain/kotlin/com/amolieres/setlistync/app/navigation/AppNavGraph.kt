package com.amolieres.setlistync.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.amolieres.setlistync.feature.band.list.ui.BandListScreen
import com.amolieres.setlistync.feature.user.presentation.UserAuthViewModel
import com.amolieres.setlistync.feature.user.ui.UserAuthScreen
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun AppNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = Destinations.UserAuth
) {
    NavHost(navController = navController, startDestination = startDestination, modifier = modifier) {

        composable(Destinations.UserAuth) {
            val viewModel: UserAuthViewModel = koinViewModel()
            val state by viewModel.uiState.collectAsState()
            UserAuthScreen(
                state,
                viewModel,
                onNavigateToBands = {
                navController.navigate(Destinations.BandList) {
                    popUpTo(Destinations.UserAuth) { inclusive = true }
                }
            })
        }

        composable(Destinations.BandList) {
            BandListScreen(onAddBand = {
                //TODO: Navigate to create band
                //navController.navigate(Destinations.CreateBand)
            })
        }
    }
}