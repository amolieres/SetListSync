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
        GigEntity::class,
    ],
    version = 6,
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
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(connection: SQLiteConnection) {
                connection.execSQL("ALTER TABLE songs ADD COLUMN originalArtist TEXT")
            }
        }
        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(connection: SQLiteConnection) {
                // Drop the separate set_lists table — setlist is now embedded in Gig
                connection.execSQL("DROP TABLE IF EXISTS set_lists")
                // Recreate gigs replacing setListIds with orderedSongIds
                connection.execSQL(
                    "CREATE TABLE gigs_new (" +
                    "id TEXT NOT NULL PRIMARY KEY, " +
                    "bandId TEXT NOT NULL, " +
                    "venue TEXT, " +
                    "dateEpochMs INTEGER, " +
                    "expectedDurationMinutes INTEGER, " +
                    "orderedSongIds TEXT NOT NULL DEFAULT '[]')"
                )
                connection.execSQL(
                    "INSERT INTO gigs_new (id, bandId, venue, dateEpochMs, expectedDurationMinutes, orderedSongIds) " +
                    "SELECT id, bandId, venue, dateEpochMs, expectedDurationMinutes, '[]' FROM gigs"
                )
                connection.execSQL("DROP TABLE gigs")
                connection.execSQL("ALTER TABLE gigs_new RENAME TO gigs")
            }
        }
        val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(connection: SQLiteConnection) {
                // Replace orderedSongIds column with sets column.
                // Each existing gig is migrated to a single default set containing its songs.
                connection.execSQL(
                    "CREATE TABLE gigs_new (" +
                    "id TEXT NOT NULL PRIMARY KEY, " +
                    "bandId TEXT NOT NULL, " +
                    "venue TEXT, " +
                    "dateEpochMs INTEGER, " +
                    "expectedDurationMinutes INTEGER, " +
                    "sets TEXT NOT NULL DEFAULT '[]')"
                )
                // Migrate data: wrap each gig's orderedSongIds in a default GigSet JSON structure.
                // Each set gets id="set_"||gig.id, title=null, orderedSongIds=<existing song ids>.
                connection.execSQL(
                    "INSERT INTO gigs_new (id, bandId, venue, dateEpochMs, expectedDurationMinutes, sets) " +
                    "SELECT id, bandId, venue, dateEpochMs, expectedDurationMinutes, " +
                    "'[{\"id\":\"set_'||id||'\",\"title\":null,\"orderedSongIds\":'||orderedSongIds||'}]' " +
                    "FROM gigs"
                )
                connection.execSQL("DROP TABLE gigs")
                connection.execSQL("ALTER TABLE gigs_new RENAME TO gigs")
            }
        }
    }
}

@Suppress("KotlinNoActualForExpect")
expect object SetListSyncDatabaseConstructor : RoomDatabaseConstructor<SetListSyncDatabase> {
    override fun initialize(): SetListSyncDatabase
}
