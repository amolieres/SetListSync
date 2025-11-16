package com.amolieres.setlistync

import androidx.room.Room
import androidx.room.RoomDatabase
import com.amolieres.setlistync.core.data.local.SetListSyncDatabase
import java.io.File

fun getDatabaseBuilder(): RoomDatabase.Builder<SetListSyncDatabase> {
    val dbFile = File(System.getProperty("java.io.tmpdir"), "sls_room.db")
    return Room.databaseBuilder<SetListSyncDatabase>(
        name = dbFile.absolutePath,
    )
}