package com.amolieres.setlistync.app.navigation

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.amolieres.setlistync.feature.band.creation.presentation.BandCreationViewModel
import com.amolieres.setlistync.feature.band.creation.ui.BandCreationScreen
import com.amolieres.setlistync.feature.band.detail.presentation.BandDetailViewModel
import com.amolieres.setlistync.feature.band.detail.ui.BandDetailScreen
import com.amolieres.setlistync.feature.band.edit.presentation.BandEditViewModel
import com.amolieres.setlistync.feature.band.edit.ui.BandEditScreen
import com.amolieres.setlistync.feature.band.gig.detail.presentation.GigDetailViewModel
import com.amolieres.setlistync.feature.band.gig.detail.ui.GigDetailScreen
import com.amolieres.setlistync.feature.band.gig.edit.presentation.GigEditViewModel
import com.amolieres.setlistync.feature.band.gig.edit.ui.GigEditScreen
import com.amolieres.setlistync.feature.band.members.presentation.BandMembersViewModel
import com.amolieres.setlistync.feature.band.members.ui.BandMembersScreen
import com.amolieres.setlistync.feature.band.songDetail.presentation.BandSongDetailViewModel
import com.amolieres.setlistync.feature.band.songDetail.ui.BandSongDetailScreen
import com.amolieres.setlistync.feature.band.songs.presentation.BandSongsViewModel
import com.amolieres.setlistync.feature.band.songs.ui.BandSongsScreen
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
                onNavigateToSongs = { navController.navigate(Destinations.bandSongs(viewModel.bandId)) },
                onNavigateToNewGig = { navController.navigate(Destinations.newGig(viewModel.bandId)) },
                onNavigateToGigDetail = { gigId ->
                    navController.navigate(Destinations.gigDetail(viewModel.bandId, gigId))
                },
                onNavigateToEdit = { navController.navigate(Destinations.bandEdit(viewModel.bandId)) }
            )
        }

        composable(Destinations.BandSongs) {
            val viewModel: BandSongsViewModel = koinViewModel()
            val uiState by viewModel.uiState.collectAsState()
            BandSongsScreen(
                uiState = uiState,
                eventFlow = viewModel.event,
                onScreenEvent = viewModel::onScreenEvent,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToNewSong = { navController.navigate(Destinations.newSong(viewModel.bandId)) },
                onNavigateToEditSong = { songId ->
                    navController.navigate(Destinations.editSong(viewModel.bandId, songId.value))
                }
            )
        }

        // ── GigEdit (create or edit gig info) ─────────────────────────────
        composable(
            route = Destinations.GigEdit,
            arguments = listOf(
                navArgument("bandId") { type = NavType.StringType },
                navArgument("gigId") { type = NavType.StringType; nullable = true; defaultValue = null }
            )
        ) {
            val viewModel: GigEditViewModel = koinViewModel()
            val uiState by viewModel.uiState.collectAsState()
            GigEditScreen(
                uiState = uiState,
                eventFlow = viewModel.event,
                onScreenEvent = viewModel::onScreenEvent,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToGigDetail = { gigId ->
                    navController.navigate(Destinations.gigDetail(viewModel.bandId, gigId)) {
                        popUpTo(Destinations.GigEdit) { inclusive = true }
                    }
                },
                onNavigateBackToBandDetail = {
                    navController.navigate(Destinations.bandDetail(viewModel.bandId)) {
                        popUpTo(Destinations.BandDetail) { inclusive = true }
                    }
                },
                isEditMode = viewModel.isEditMode
            )
        }

        // ── GigDetail (setlist manager) ────────────────────────────────────
        composable(
            route = Destinations.GigDetail,
            arguments = listOf(
                navArgument("bandId") { type = NavType.StringType },
                navArgument("gigId") { type = NavType.StringType }
            )
        ) {
            val viewModel: GigDetailViewModel = koinViewModel()
            val uiState by viewModel.uiState.collectAsState()
            GigDetailScreen(
                uiState = uiState,
                eventFlow = viewModel.event,
                onScreenEvent = viewModel::onScreenEvent,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEditGig = {
                    navController.navigate(
                        Destinations.editGigInfo(viewModel.bandId, viewModel.gigId)
                    )
                }
            )
        }

        composable(
            route = Destinations.BandSongDetail,
            arguments = listOf(
                navArgument("bandId") { type = NavType.StringType },
                navArgument("songId") { type = NavType.StringType; nullable = true; defaultValue = null }
            )
        ) {
            val viewModel: BandSongDetailViewModel = koinViewModel()
            val uiState by viewModel.uiState.collectAsState()
            BandSongDetailScreen(
                uiState = uiState,
                eventFlow = viewModel.event,
                onScreenEvent = viewModel::onScreenEvent,
                onNavigateBack = { navController.popBackStack() }
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
