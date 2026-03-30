package com.amolieres.setlistync.common.di

import com.amolieres.setlistync.core.domain.band.usecase.*
import com.amolieres.setlistync.core.domain.preferences.ObserveNotationUseCase
import com.amolieres.setlistync.core.domain.preferences.SetNotationUseCase
import com.amolieres.setlistync.core.domain.setList.usecase.*
import com.amolieres.setlistync.core.domain.song.usecase.*
import com.amolieres.setlistync.core.domain.user.usecase.AutoLoginUseCase
import com.amolieres.setlistync.core.domain.user.usecase.CreateUserUseCase
import com.amolieres.setlistync.core.domain.user.usecase.DeleteCurrentUserUseCase
import com.amolieres.setlistync.core.domain.user.usecase.GetCurrentUserUseCase
import com.amolieres.setlistync.core.domain.user.usecase.GetUserByIdUseCase
import com.amolieres.setlistync.core.domain.user.usecase.LoginUserUseCase
import com.amolieres.setlistync.core.domain.user.usecase.LogoutUserUseCase
import org.koin.dsl.module

val useCaseModule = module {
    // User
    factory { AutoLoginUseCase(get(), get()) }
    factory { CreateUserUseCase(get(), get()) }
    factory { GetUserByIdUseCase(get()) }
    factory { LogoutUserUseCase(get()) }
    factory { LoginUserUseCase(get(), get()) }
    factory { GetCurrentUserUseCase(get(), get()) }
    factory { DeleteCurrentUserUseCase(get(), get()) }

    // Preferences
    factory { ObserveNotationUseCase(get()) }
    factory { SetNotationUseCase(get()) }

    // Band
    factory { CreateBandUseCase(get()) }
    factory { GetBandUseCase(get()) }
    factory { GetAllBandsUseCase(get()) }
    factory { ObserveAllBandsUseCase(get()) }
    factory { ObserveBandUseCase(get()) }
    factory { GetBandsForUserUseCase(get()) }
    factory { DeleteBandUseCase(get()) }
    factory { DeleteAllBandsUseCase(get()) }
    factory { UpdateBandUseCase(get()) }
    factory { AddMemberToBandUseCase(get()) }
    factory { RemoveMemberFromBandUseCase(get()) }
    factory { UpdateMemberInBandUseCase(get()) }

    // Gig
    factory { CreateGigUseCase(get()) }
    factory { GetGigsForBandUseCase(get()) }
    factory { UpdateGigUseCase(get()) }
    factory { DeleteGigUseCase(get()) }

    // Song
    factory { AddSongUseCase(get()) }
    factory { GetAllSongsUseCase(get()) }
    factory { ObserveSongsUseCase(get()) }
    factory { GetSongUseCase(get()) }
    factory { UpdateSongUseCase(get()) }
    factory { DeleteSongUseCase(get(), get()) }

    // SongNote
    factory { AddSongNoteUseCase(get()) }
    factory { GetNotesForSongUseCase(get()) }
    factory { GetNoteForMemberAndSongUseCase(get()) }
    factory { UpdateSongNoteUseCase(get()) }
    factory { DeleteSongNoteUseCase(get()) }

    // SetList
    factory { CreateSetListUseCase(get()) }
    factory { GetSetListsUseCase(get()) }
    factory { AddSongToSetListUseCase(get()) }
    factory { RemoveSongFromSetListUseCase(get()) }
    factory { ReorderSetListUseCase(get()) }
    factory { ComputeSetListDurationUseCase(get()) }
    factory { DeleteSetListUseCase(get()) }
}