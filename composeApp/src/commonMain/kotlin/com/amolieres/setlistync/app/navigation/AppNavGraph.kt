package com.amolieres.setlistync.app.navigation

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.amolieres.setlistync.feature.band.creation.presentation.BandCreationViewModel
import com.amolieres.setlistync.feature.band.creation.ui.BandCreationScreen
import com.amolieres.setlistync.feature.band.detail.presentation.BandDetailViewModel
import com.amolieres.setlistync.feature.band.edit.presentation.BandEditViewModel
import com.amolieres.setlistync.feature.band.members.presentation.BandMembersViewModel
import com.amolieres.setlistync.feature.band.detail.ui.BandDetailScreen
import com.amolieres.setlistync.feature.band.edit.ui.BandEditScreen
import com.amolieres.setlistync.feature.band.members.ui.BandMembersScreen
import com.amolieres.setlistync.feature.main.presentation.MainViewModel
import com.amolieres.setlistync.feature.main.ui.MainScreen
import com.amolieres.setlistync.feature.settings.presentation.SettingsViewModel
import com.amolieres.setlistync.feature.settings.ui.SettingsScreen
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
            val uiState by viewModel.uiState.collectAsState()
            UserAuthScreen(
                uiState,
                onNavigateToMain = {
                    navController.navigate(Destinations.MainScreen) {
                        popUpTo(Destinations.UserAuth) { inclusive = false }
                    }
                },
                eventFlow = viewModel.event,
                onScreenEvent = viewModel::onScreenEvent
            )
        }

        composable(Destinations.MainScreen) {
            val viewModel: MainViewModel = koinViewModel()
            val uiState by viewModel.uiState.collectAsState()
            MainScreen(
                uiState = uiState,
                eventFlow = viewModel.event,
                onScreenEvent = viewModel::onScreenEvent,
                onNavigateToLogin = {
                    navController.navigate(Destinations.UserAuth) {
                        popUpTo(Destinations.MainScreen) { inclusive = false }
                    }
                },
                onNavigateToSettings = {
                    navController.navigate(Destinations.Settings)
                },
                onNavigateToBandDetail = { bandId ->
                    navController.navigate(Destinations.bandDetail(bandId))
                },
                onNavigateToBandCreation = {
                    navController.navigate(Destinations.BandCreation)
                }
            )
        }

        composable(
            route = Destinations.Settings,
            enterTransition = { slideInHorizontally { it } },
            popExitTransition = { slideOutHorizontally { it } }
        ) {
            val viewModel: SettingsViewModel = koinViewModel()
            val uiState by viewModel.uiState.collectAsState()
            SettingsScreen(
                uiState = uiState,
                eventsFlow = viewModel.events,
                onScreenEvent = viewModel::onScreenEvent,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToLogin = {
                    navController.navigate(Destinations.UserAuth) {
                        popUpTo(Destinations.MainScreen) { inclusive = true }
                    }
                }
            )
        }

        composable(Destinations.BandCreation) {
            val viewModel: BandCreationViewModel = koinViewModel()
            val uiState by viewModel.uiState.collectAsState()
            BandCreationScreen(
                uiState = uiState,
                eventsFlow = viewModel.events,
                onScreenEvent = viewModel::onScreenEvent,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToBandDetail = { bandId ->
                    navController.navigate(Destinations.bandDetail(bandId)) {
                        popUpTo(Destinations.BandCreation) { inclusive = true }
                    }
                }
            )
        }

        composable(Destinations.BandDetail) {
            val viewModel: BandDetailViewModel = koinViewModel()
            val uiState by viewModel.uiState.collectAsState()
            BandDetailScreen(
                uiState = uiState,
                eventFlow = viewModel.event,
                onScreenEvent = viewModel::onScreenEvent,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToMembers = { navController.navigate(Destinations.bandMembers(viewModel.bandId)) },
                onNavigateToEdit = { navController.navigate(Destinations.bandEdit(viewModel.bandId)) }
            )
        }

        composable(Destinations.BandEdit) {
            val viewModel: BandEditViewModel = koinViewModel()
            val uiState by viewModel.uiState.collectAsState()
            BandEditScreen(
                uiState = uiState,
                eventsFlow = viewModel.events,
                onScreenEvent = viewModel::onScreenEvent,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Destinations.BandMembers) {
            val viewModel: BandMembersViewModel = koinViewModel()
            val uiState by viewModel.uiState.collectAsState()
            BandMembersScreen(
                uiState = uiState,
                onScreenEvent = viewModel::onScreenEvent,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
