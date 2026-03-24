package com.amolieres.setlistync.core.data.preferences

import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    val userId: Flow<String?>
    val userPassword: Flow<String?>
    val noteNotation: Flow<NoteNotation>

    suspend fun saveUser(id: String, password: String)
    suspend fun clearUser()

    suspend fun setNotation(notation: NoteNotation)
}