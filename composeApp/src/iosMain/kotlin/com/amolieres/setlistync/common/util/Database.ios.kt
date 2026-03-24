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