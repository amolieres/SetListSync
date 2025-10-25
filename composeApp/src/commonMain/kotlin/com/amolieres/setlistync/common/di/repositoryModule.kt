package com.amolieres.setlistync.common.di

import com.amolieres.setlistync.core.data.user.UserRepositoryLocal
import com.amolieres.setlistync.core.domain.user.repository.UserRepository
import org.koin.dsl.module

val repositoryModule =  module {
    single<UserRepository> { UserRepositoryLocal() }
}