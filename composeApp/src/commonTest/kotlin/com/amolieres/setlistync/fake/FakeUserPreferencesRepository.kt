package com.amolieres.setlistync.fake

import com.amolieres.setlistync.core.data.preferences.NoteNotation
import com.amolieres.setlistync.core.data.preferences.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeUserPreferencesRepository : UserPreferencesRepository {
    override val userId: Flow<String?> = flowOf(null)
    override val userPassword: Flow<String?> = flowOf(null)
    override val noteNotation: Flow<NoteNotation> = flowOf(NoteNotation.EN)

    override suspend fun saveUser(id: String, password: String) {}
    override suspend fun clearUser() {}
    override suspend fun setNotation(notation: NoteNotation) {}
}
