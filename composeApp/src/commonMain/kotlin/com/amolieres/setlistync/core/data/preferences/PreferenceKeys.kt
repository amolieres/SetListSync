package com.amolieres.setlistync.core.data.preferences

import androidx.datastore.preferences.core.stringPreferencesKey

object PreferenceKeys {
    val USER_ID = stringPreferencesKey("user_id")
    val USER_PASSWORD = stringPreferencesKey("user_password")
    val NOTE_NOTATION = stringPreferencesKey("note_notation")
}