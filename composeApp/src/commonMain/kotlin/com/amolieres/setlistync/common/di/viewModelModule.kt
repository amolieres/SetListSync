package com.amolieres.setlistync.common.di

import com.amolieres.setlistync.feature.band.creation.presentation.BandCreationViewModel
import com.amolieres.setlistync.feature.band.detail.presentation.BandDetailViewModel
import com.amolieres.setlistync.feature.band.edit.presentation.BandEditViewModel
import com.amolieres.setlistync.feature.band.members.presentation.BandMembersViewModel
import com.amolieres.setlistync.feature.band.songDetail.presentation.BandSongDetailViewModel
import com.amolieres.setlistync.feature.band.songs.presentation.BandSongsViewModel
import com.amolieres.setlistync.feature.main.presentation.MainViewModel
import com.amolieres.setlistync.feature.settings.presentation.SettingsViewModel
import com.amolieres.setlistync.feature.user.presentation.UserAuthViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { UserAuthViewModel(get(), get(), get()) }         // CreateUserUseCase, LoginUserUseCase, AutoLoginUseCase
    viewModel { SettingsViewModel(get(), get(), get(), get(), get()) }
    viewModel { MainViewModel(get()) }                                            // ObserveAllBandsUseCase
    viewModel { BandCreationViewModel(get(), get()) }                             // CreateBandUseCase, AddMemberToBandUseCase
    viewModel { BandDetailViewModel(get(), get(), get(), get()) }                 // SavedStateHandle, ObserveBandUseCase, DeleteBandUseCase, ObserveSongsUseCase
    viewModel { BandEditViewModel(get(), get(), get()) }                          // SavedStateHandle, GetBandUseCase, UpdateBandUseCase
    viewModel { BandMembersViewModel(get(), get(), get(), get(), get()) }         // SavedStateHandle, ObserveBandUseCase, AddMember, RemoveMember, UpdateMember
    viewModel { BandSongsViewModel(get(), get(), get(), get()) }                  // SavedStateHandle, ObserveBandUseCase, ObserveSongsUseCase, DeleteSongUseCase
    viewModel { BandSongDetailViewModel(get(), get(), get(), get(), get(), get()) } // SavedStateHandle, GetSongUseCase, AddSongUseCase, UpdateSongUseCase, SearchSongsUseCase, GetSongAudioFeaturesUseCase
}
