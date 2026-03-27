package com.amolieres.setlistync.common.di

import com.amolieres.setlistync.core.data.band.BandRepositoryImpl
import com.amolieres.setlistync.core.data.band.GigRepositoryImpl
import com.amolieres.setlistync.core.data.local.SetListSyncDatabase
import com.amolieres.setlistync.core.data.preferences.UserPreferencesRepository
import com.amolieres.setlistync.core.data.preferences.UserPreferencesRepositoryImpl
import com.amolieres.setlistync.core.data.setlist.SetListRepositoryImpl
import com.amolieres.setlistync.core.data.song.SongNoteRepositoryImpl
import com.amolieres.setlistync.core.data.song.SongRepositoryImpl
import com.amolieres.setlistync.core.data.user.UserRepositoryImpl
import com.amolieres.setlistync.core.domain.band.repository.BandRepository
import com.amolieres.setlistync.core.domain.band.repository.GigRepository
import com.amolieres.setlistync.core.domain.setList.repository.SetListRepository
import com.amolieres.setlistync.core.domain.song.repository.SongNoteRepository
import com.amolieres.setlistync.core.domain.song.repository.SongRepository
import com.amolieres.setlistync.core.domain.user.repository.UserRepository
import org.koin.dsl.module

val repositoryModule = module {
    // DAOs
    single { get<SetListSyncDatabase>().userDao() }
    single { get<SetListSyncDatabase>().bandDao() }
    single { get<SetListSyncDatabase>().bandMemberDao() }
    single { get<SetListSyncDatabase>().songDao() }
    single { get<SetListSyncDatabase>().songNoteDao() }
    single { get<SetListSyncDatabase>().setListDao() }
    single { get<SetListSyncDatabase>().gigDao() }

    // Repositories
    single<UserRepository> { UserRepositoryImpl(get()) }
    single<UserPreferencesRepository> { UserPreferencesRepositoryImpl(get()) }
    single<BandRepository> { BandRepositoryImpl(get(), get()) }
    single<SongRepository> { SongRepositoryImpl(get()) }
    single<SongNoteRepository> { SongNoteRepositoryImpl(get()) }
    single<SetListRepository> { SetListRepositoryImpl(get()) }
    single<GigRepository> { GigRepositoryImpl(get()) }
}