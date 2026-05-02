package com.amolieres.setlistync.feature.band.ui

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.amolieres.setlistync.feature.band.creation.presentation.BandCreationRoute
import com.amolieres.setlistync.feature.band.creation.ui.BandCreationRoot
import com.amolieres.setlistync.feature.band.detail.presentation.BandDetailRoute
import com.amolieres.setlistync.feature.band.detail.ui.BandDetailRoot
import com.amolieres.setlistync.feature.band.edit.presentation.BandEditRoute
import com.amolieres.setlistync.feature.band.edit.ui.BandEditRoot
import com.amolieres.setlistync.feature.band.gig.detail.presentation.GigDetailRoute
import com.amolieres.setlistync.feature.band.gig.detail.ui.GigDetailRoot
import com.amolieres.setlistync.feature.band.gig.edit.presentation.GigEditRoute
import com.amolieres.setlistync.feature.band.gig.edit.ui.GigEditRoot
import com.amolieres.setlistync.feature.band.members.presentation.BandMembersRoute
import com.amolieres.setlistync.feature.band.members.ui.BandMembersRoot
import com.amolieres.setlistync.feature.band.songDetail.presentation.BandSongDetailRoute
import com.amolieres.setlistync.feature.band.songDetail.ui.BandSongDetailRoot
import com.amolieres.setlistync.feature.band.songs.presentation.BandSongsRoute
import com.amolieres.setlistync.feature.band.songs.ui.BandSongsRoot

fun NavGraphBuilder.bandGraph(
    navController: NavController
) {
    composable<BandCreationRoute> {
        BandCreationRoot(
            onNavigateBack = { navController.popBackStack() },
            onNavigateToBandDetail = { bandId ->
                navController.navigate(BandDetailRoute(bandId)) {
                    popUpTo<BandCreationRoute> { inclusive = true }
                }
            }
        )
    }

    composable<BandDetailRoute> {
        BandDetailRoot(
            onNavigateBack = { navController.popBackStack() },
            onNavigateToMembers = { bandId -> navController.navigate(BandMembersRoute(bandId)) },
            onNavigateToSongs = { bandId -> navController.navigate(BandSongsRoute(bandId)) },
            onNavigateToNewGig = { bandId -> navController.navigate(GigEditRoute(bandId = bandId)) },
            onNavigateToGigDetail = { bandId, gigId -> navController.navigate(GigDetailRoute(bandId, gigId)) },
            onNavigateToEdit = { bandId -> navController.navigate(BandEditRoute(bandId)) }
        )
    }

    composable<BandMembersRoute> {
        BandMembersRoot(
            onNavigateBack = { navController.popBackStack() }
        )
    }

    composable<BandSongsRoute> {
        BandSongsRoot(
            onNavigateBack = { navController.popBackStack() },
            onNavigateToNewSong = { bandId -> navController.navigate(BandSongDetailRoute(bandId)) },
            onNavigateToEditSong = { bandId, songId -> navController.navigate(BandSongDetailRoute(bandId, songId)) }
        )
    }

    composable<BandSongDetailRoute> {
        BandSongDetailRoot(
            onNavigateBack = { navController.popBackStack() }
        )
    }

    composable<BandEditRoute> {
        BandEditRoot(
            onNavigateBack = { navController.popBackStack() }
        )
    }

    composable<GigEditRoute> {
        GigEditRoot(
            onNavigateBack = { navController.popBackStack() },
            onNavigateToGigDetail = { bandId, gigId ->
                navController.navigate(GigDetailRoute(bandId, gigId, isEditing = true)) {
                    popUpTo<GigEditRoute> { inclusive = true }
                }
            }
        )
    }

    composable<GigDetailRoute> {
        GigDetailRoot(
            onNavigateBack = { navController.popBackStack() },
            onNavigateToEditGig = { bandId, gigId -> navController.navigate(GigEditRoute(bandId, gigId)) }
        )
    }
}
