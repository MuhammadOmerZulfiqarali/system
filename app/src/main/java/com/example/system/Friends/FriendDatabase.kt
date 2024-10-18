package com.example.system.Friends

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Friend::class], version = 3) // Increment the version to 3
abstract class FriendDatabase : RoomDatabase() {

    abstract fun friendDao(): FriendDao

    companion object {
        @Volatile
        private var INSTANCE: FriendDatabase? = null

        fun getDatabase(context: Context): FriendDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FriendDatabase::class.java,
                    "friend_database"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3) // Add all migrations
                    .build()
                INSTANCE = instance
                instance
            }
        }

        // Migration from version 1 to 2
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Migration logic (if needed)
            }
        }

        // Migration from version 2 to 3
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create a new table with the updated schema
                database.execSQL("""
                    CREATE TABLE new_friend_table (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        firstName TEXT NOT NULL,
                        lastName TEXT NOT NULL,
                        age INTEGER NOT NULL,
                        address TEXT NOT NULL,
                        gender TEXT NOT NULL,
                        imageUri TEXT
                    )
                """.trimIndent())
                // Copy the data from the old table to the new table
                database.execSQL("""
                    INSERT INTO new_friend_table (id, firstName, lastName, age, address, gender)
                    SELECT id, firstName, lastName, age, address, gender FROM friend_table
                """.trimIndent())
                // Remove the old table
                database.execSQL("DROP TABLE friend_table")
                // Rename the new table to the original table name
                database.execSQL("ALTER TABLE new_friend_table RENAME TO friend_table")
            }
        }
    }
}
