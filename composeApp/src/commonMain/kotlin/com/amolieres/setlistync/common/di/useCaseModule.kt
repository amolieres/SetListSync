package com.amolieres.setlistync.common.di

import com.amolieres.setlistync.core.domain.user.usecase.CreateUserUseCase
import com.amolieres.setlistync.core.domain.user.usecase.GetUserByEmailUseCase
import com.amolieres.setlistync.core.domain.user.usecase.GetUserByIdUseCase
import org.koin.dsl.module

val useCaseModule = module {
    // User
    single { CreateUserUseCase(get()) }
    single { GetUserByEmailUseCase(get()) }
    single { GetUserByIdUseCase(get()) }

}