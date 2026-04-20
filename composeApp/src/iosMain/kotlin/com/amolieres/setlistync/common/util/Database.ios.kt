package com.amolieres.setlistync.common.util

import androidx.room.Room
import androidx.room.RoomDatabase
import com.amolieres.setlistync.core.data.local.SetListSyncDatabase
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

fun getDatabaseBuilder(): RoomDatabase.Builder<SetListSyncDatabase> {
    val dbFilePath = documentDirectory() + "/sls_room.db"
    return Room.databaseBuilder<SetListSyncDatabase>(
        name = dbFilePath,
    ).addMigrations(
        SetListSyncDatabase.MIGRATION_2_3,
        SetListSyncDatabase.MIGRATION_3_4,
        SetListSyncDatabase.MIGRATION_4_5,
        SetListSyncDatabase.MIGRATION_5_6
    )
}

@OptIn(ExperimentalForeignApi::class)
private fun documentDirectory(): String {
    val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null,
    )
    return requireNotNull(documentDirectory?.path)
}
