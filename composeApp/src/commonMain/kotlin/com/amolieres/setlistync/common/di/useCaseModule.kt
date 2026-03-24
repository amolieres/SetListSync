package com.amolieres.setlistync.common.di

import com.amolieres.setlistync.core.domain.preferences.ObserveNotationUseCase
import com.amolieres.setlistync.core.domain.preferences.SetNotationUseCase
import com.amolieres.setlistync.core.domain.user.usecase.CreateUserUseCase
import com.amolieres.setlistync.core.domain.user.usecase.DeleteCurrentUserUseCase
import com.amolieres.setlistync.core.domain.user.usecase.GetCurrentUserUseCase
import com.amolieres.setlistync.core.domain.user.usecase.GetUserByIdUseCase
import com.amolieres.setlistync.core.domain.user.usecase.LoginUserUseCase
import com.amolieres.setlistync.core.domain.user.usecase.LogoutUserUseCase
import org.koin.dsl.module

val useCaseModule = module {
    // User
    factory { CreateUserUseCase(get(), get()) }
    factory { GetUserByIdUseCase(get()) }
    factory { LogoutUserUseCase(get()) }
    factory { LoginUserUseCase(get(), get()) }
    factory { GetCurrentUserUseCase(get(), get())}
    factory { DeleteCurrentUserUseCase(get(), get()) }

    // Preferences
    factory { ObserveNotationUseCase(get()) }
    factory { SetNotationUseCase(get()) }

}