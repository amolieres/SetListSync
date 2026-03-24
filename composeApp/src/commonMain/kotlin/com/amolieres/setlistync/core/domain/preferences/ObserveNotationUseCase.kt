package com.amolieres.setlistync.core.domain.preferences

import com.amolieres.setlistync.core.data.preferences.NoteNotation
import com.amolieres.setlistync.core.data.preferences.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow

class ObserveNotationUseCase(
    private val prefs: UserPreferencesRepository
) {
    operator fun invoke(): Flow<NoteNotation> = prefs.noteNotation
}