package com.amolieres.setlistync.core.data.local

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import com.amolieres.setlistync.core.data.local.dao.UserDao
import com.amolieres.setlistync.core.data.local.entity.UserEntity

@Database(
    entities = [UserEntity::class],
    version = 1,
    exportSchema = true
)
@ConstructedBy(SetListSyncDatabaseConstructor::class)
abstract class SetListSyncDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}

@Suppress("KotlinNoActualForExpect")
expect object SetListSyncDatabaseConstructor : RoomDatabaseConstructor<SetListSyncDatabase> {
    override fun initialize(): SetListSyncDatabase
}