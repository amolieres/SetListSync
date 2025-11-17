package com.amolieres.setlistync.core.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.map

class UserPreferencesRepositoryImpl(
    private val dataStore: DataStore<Preferences>
) : UserPreferencesRepository {

    override val userId = dataStore.data.map { prefs -> prefs[PreferenceKeys.USER_ID] }
    override val userPassword = dataStore.data.map { prefs -> prefs[PreferenceKeys.USER_PASSWORD] }

    override val noteNotation = dataStore.data.map { prefs ->
        when (prefs[PreferenceKeys.NOTE_NOTATION]) {
            "FR" -> NoteNotation.FR
            "EN" -> NoteNotation.EN
            else -> NoteNotation.FR
        }
    }

    override suspend fun saveUser(id: String, password: String) {
        dataStore.edit { prefs ->
            prefs[PreferenceKeys.USER_ID] = id
            prefs[PreferenceKeys.USER_PASSWORD] = password
        }
    }

    override suspend fun clearUser() {
        dataStore.edit { prefs ->
            prefs.remove(PreferenceKeys.USER_ID)
            prefs.remove(PreferenceKeys.USER_PASSWORD)
        }
    }

    override suspend fun setNotation(notation: NoteNotation) {
        dataStore.edit { prefs ->
            prefs[PreferenceKeys.NOTE_NOTATION] = notation.name
        }
    }
}