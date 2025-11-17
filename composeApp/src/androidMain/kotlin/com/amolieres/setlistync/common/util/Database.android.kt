package com.amolieres.setlistync.common.util

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.amolieres.setlistync.core.data.local.SetListSyncDatabase


fun getDatabaseBuilder(context: Context): RoomDatabase.Builder<SetListSyncDatabase> {
    val appContext = context.applicationContext
    val dbFile = appContext.getDatabasePath("sls_room.db")
    return Room.databaseBuilder<SetListSyncDatabase>(
        context = appContext,
        name = dbFile.absolutePath
    )
}