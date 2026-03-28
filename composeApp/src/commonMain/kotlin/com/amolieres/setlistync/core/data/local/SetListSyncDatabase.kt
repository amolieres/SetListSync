package com.amolieres.setlistync.core.data.local

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
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
    version = 3,
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

    companion object {
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(connection: SQLiteConnection) {
                connection.execSQL("ALTER TABLE bands ADD COLUMN email TEXT")
                connection.execSQL("ALTER TABLE bands ADD COLUMN instagramUrl TEXT")
                connection.execSQL("ALTER TABLE bands ADD COLUMN facebookUrl TEXT")
                connection.execSQL("ALTER TABLE bands ADD COLUMN tiktokUrl TEXT")
                connection.execSQL("ALTER TABLE bands ADD COLUMN genres TEXT NOT NULL DEFAULT '[]'")
            }
        }
    }
}

@Suppress("KotlinNoActualForExpect")
expect object SetListSyncDatabaseConstructor : RoomDatabaseConstructor<SetListSyncDatabase> {
    override fun initialize(): SetListSyncDatabase
}
