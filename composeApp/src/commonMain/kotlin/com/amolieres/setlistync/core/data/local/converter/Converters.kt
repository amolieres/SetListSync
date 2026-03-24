package com.amolieres.setlistync.core.data.local.converter

import androidx.room.TypeConverter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

internal val json = Json { ignoreUnknownKeys = true }

class Converters {

    @TypeConverter
    fun fromStringList(value: String): List<String> =
        json.decodeFromString(value)

    @TypeConverter
    fun toStringList(list: List<String>): String =
        json.encodeToString(list)

    @TypeConverter
    fun fromNullableStringList(value: String?): List<String>? =
        value?.let { json.decodeFromString(it) }

    @TypeConverter
    fun toNullableStringList(list: List<String>?): String? =
        list?.let { json.encodeToString(it) }
}
