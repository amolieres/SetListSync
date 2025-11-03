package com.amolieres.setlistync.common.di

import com.amolieres.setlistync.feature.main.presentation.MainViewModel
import com.amolieres.setlistync.feature.user.presentation.UserAuthViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel{ UserAuthViewModel(get(), get()) }
    viewModel{ MainViewModel()}
}
