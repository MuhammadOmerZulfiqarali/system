package com.example.system.Tasks

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Task::class], version = 2, exportSchema = false)
abstract class TaskDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile
        private var INSTANCE: TaskDatabase? = null

        fun getDatabase(context: Context): TaskDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TaskDatabase::class.java,
                    "task_database"
                )
                    .addMigrations(MIGRATION_1_2) // Add migration if needed
                    .build()
                INSTANCE = instance
                instance
            }
        }

        // Migration from version 1 to version 2 (example)
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create a new table with the updated schema
                database.execSQL("CREATE TABLE new_tasks_table (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, taskName TEXT NOT NULL, location TEXT NOT NULL, status INTEGER NOT NULL DEFAULT 0)")

                // Copy the data from the old table to the new table
                database.execSQL("INSERT INTO new_tasks_table (id, taskName, location) SELECT id, taskName, location FROM tasks_table")

                // Remove the old table
                database.execSQL("DROP TABLE tasks_table")

                // Rename the new table to the old table's name
                database.execSQL("ALTER TABLE new_tasks_table RENAME TO tasks_table")
            }
        }

    }
}
