package com.amolieres.setlistync.core.domain.preferences

import com.amolieres.setlistync.core.data.preferences.NoteNotation
import com.amolieres.setlistync.core.data.preferences.UserPreferencesRepository

class SetNotationUseCase(
    private val prefs: UserPreferencesRepository
) {
    suspend operator fun invoke(notation: NoteNotation) {
        prefs.setNotation(notation)
    }
}