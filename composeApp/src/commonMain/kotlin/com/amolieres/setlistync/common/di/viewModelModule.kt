package com.amolieres.setlistync.common.di

import com.amolieres.setlistync.feature.band.creation.presentation.BandCreationViewModel
import com.amolieres.setlistync.feature.band.detail.presentation.BandDetailViewModel
import com.amolieres.setlistync.feature.band.edit.presentation.BandEditViewModel
import com.amolieres.setlistync.feature.band.gig.detail.presentation.GigDetailViewModel
import com.amolieres.setlistync.feature.band.gig.edit.presentation.GigEditViewModel
import com.amolieres.setlistync.feature.band.members.presentation.BandMembersViewModel
import com.amolieres.setlistync.feature.band.songDetail.presentation.BandSongDetailViewModel
import com.amolieres.setlistync.feature.band.songs.presentation.BandSongsViewModel
import com.amolieres.setlistync.feature.main.presentation.MainViewModel
import com.amolieres.setlistync.feature.settings.presentation.SettingsViewModel
import com.amolieres.setlistync.feature.user.presentation.UserAuthViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { UserAuthViewModel(get(), get(), get()) }
    viewModel { SettingsViewModel(get(), get(), get(), get(), get()) }
    viewModel { MainViewModel(get()) }
    viewModel { BandCreationViewModel(get(), get()) }
    viewModel { BandDetailViewModel(get(), get(), get(), get(), get(), get()) }
    viewModel { BandEditViewModel(get(), get(), get()) }
    viewModel { BandMembersViewModel(get(), get(), get(), get(), get()) }
    viewModel { BandSongsViewModel(get(), get(), get(), get(), get()) }
    viewModel { BandSongDetailViewModel(get(), get(), get(), get(), get(), get(), get()) }
    viewModel { GigDetailViewModel(get(), get(), get(), get(), get(), get(), get()) }
    viewModel { GigEditViewModel(get(), get(), get(), get(), get()) }
}
