package com.amolieres.setlistync.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.amolieres.setlistync.feature.main.presentation.MainViewModel
import com.amolieres.setlistync.feature.main.ui.MainScreen
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
                onNavigateToMain = {
                    navController.navigate(Destinations.MainScreen) {
                        popUpTo(Destinations.UserAuth) { inclusive = false }
                    }
                },
                uiEventFlow = viewModel.uiEvent,
                onScreenEvent = viewModel::onScreenEvent
            )
        }

        composable(Destinations.MainScreen) {
            val viewModel: MainViewModel = koinViewModel()
            val state by viewModel.uiState.collectAsState()
            
            MainScreen(
                state = state,
                uiEventFlow = viewModel.uiEvent,
                onScreenEvent = viewModel::onScreenEvent,
                onNavigateToLogin = {
                  navController.navigate(Destinations.UserAuth) {
                      popUpTo(Destinations.MainScreen) { inclusive = false }
                  }
                }
            )
        }
    }
}