package com.amolieres.setlistync.common.di

import com.amolieres.setlistync.core.data.remote.SongCatalogRepositoryImpl
import com.amolieres.setlistync.core.data.remote.deezer.DeezerApi
import com.amolieres.setlistync.core.data.remote.getsongbpm.GetSongBpmApi
import com.amolieres.setlistync.core.domain.song.repository.SongCatalogRepository
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val networkModule = module {
    single {
        HttpClient {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
            install(Logging) {
                logger = Logger.SIMPLE   // println → visible dans Logcat (System.out) sans bridge SLF4J
                level = LogLevel.ALL
            }
        }
    }
    single { DeezerApi(get()) }
    single { GetSongBpmApi(get()) }
    single<SongCatalogRepository> { SongCatalogRepositoryImpl(get(), get()) }
}
