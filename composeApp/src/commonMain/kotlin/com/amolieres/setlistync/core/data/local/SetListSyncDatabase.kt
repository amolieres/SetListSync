package com.amolieres.setlistync.core.data.local

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import com.amolieres.setlistync.core.data.local.converter.Converters
import com.amolieres.setlistync.core.data.local.dao.*
import com.amolieres.setlistync.core.data.local.entity.*

@Database(
    entities = [
        UserEntity::class,
        BandEntity::class,
        BandMemberEntity::class,
        SongEntity::class,
        SongNoteEntity::class,
        SetListEntity::class,
        GigEntity::class,
    ],
    version = 2,
    exportSchema = true
)
@TypeConverters(Converters::class)
@ConstructedBy(SetListSyncDatabaseConstructor::class)
abstract class SetListSyncDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun bandDao(): BandDao
    abstract fun bandMemberDao(): BandMemberDao
    abstract fun songDao(): SongDao
    abstract fun songNoteDao(): SongNoteDao
    abstract fun setListDao(): SetListDao
    abstract fun gigDao(): GigDao
}

@Suppress("KotlinNoActualForExpect")
expect object SetListSyncDatabaseConstructor : RoomDatabaseConstructor<SetListSyncDatabase> {
    override fun initialize(): SetListSyncDatabase
}
