package com.amolieres.setlistync.common.di

import com.amolieres.setlistync.core.domain.band.usecase.*
import com.amolieres.setlistync.core.domain.preferences.ObserveNotationUseCase
import com.amolieres.setlistync.core.domain.preferences.SetNotationUseCase
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
    factory { ObserveAllBandsUseCase(get(), get()) }
    factory { ObserveBandUseCase(get()) }
    factory { GetBandsForUserUseCase(get()) }
    factory { DeleteBandUseCase(get()) }
    factory { DeleteAllBandsUseCase(get()) }
    factory { UpdateBandUseCase(get()) }
    factory { AddMemberToBandUseCase(get()) }
    factory { RemoveMemberFromBandUseCase(get()) }
    factory { UpdateMemberInBandUseCase(get()) }

    // Gig — CRUD
    factory { CreateGigUseCase(get()) }
    factory { GetGigUseCase(get()) }
    factory { GetGigsForBandUseCase(get()) }
    factory { ObserveGigsForBandUseCase(get()) }
    factory { UpdateGigUseCase(get()) }
    factory { DeleteGigUseCase(get()) }

    // Gig — Setlist management
    factory { AddSongToGigUseCase(get()) }
    factory { RemoveSongFromGigUseCase(get()) }
    factory { ReorderGigUseCase(get()) }
    factory { ComputeGigDurationUseCase(get(), get()) }

    // Song
    factory { AddSongUseCase(get()) }
    factory { GetAllSongsUseCase(get()) }
    factory { ObserveSongsUseCase(get()) }
    factory { GetSongUseCase(get()) }
    factory { UpdateSongUseCase(get()) }
    factory { DeleteSongUseCase(get(), get()) }
    factory { SearchSongsUseCase(get()) }
    factory { GetSongAudioFeaturesUseCase(get()) }

    // SongNote
    factory { AddSongNoteUseCase(get()) }
    factory { GetNotesForSongUseCase(get()) }
    factory { GetNoteForMemberAndSongUseCase(get()) }
    factory { UpdateSongNoteUseCase(get()) }
    factory { DeleteSongNoteUseCase(get()) }
}
