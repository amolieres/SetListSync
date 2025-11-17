package com.amolieres.setlistync.common.di

import com.amolieres.setlistync.core.data.local.SetListSyncDatabase
import com.amolieres.setlistync.core.data.preferences.UserPreferencesRepository
import com.amolieres.setlistync.core.data.preferences.UserPreferencesRepositoryImpl
import com.amolieres.setlistync.core.data.user.UserRepositoryImpl
import com.amolieres.setlistync.core.domain.user.repository.UserRepository
import org.koin.dsl.module

val repositoryModule =  module {
    single { get<SetListSyncDatabase>().userDao() }
    single<UserRepository> { UserRepositoryImpl(get()) }
    single<UserPreferencesRepository> { UserPreferencesRepositoryImpl(get()) }
}