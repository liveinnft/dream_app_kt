package com.lionido.dream_app.storage

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context

/**
 * Room database для хранения снов
 */
@Database(
    entities = [DreamEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(DreamTypeConverters::class)
abstract class DreamDatabase : RoomDatabase() {

    abstract fun dreamDao(): DreamDao

    companion object {
        @Volatile
        private var INSTANCE: DreamDatabase? = null

        fun getDatabase(context: Context): DreamDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DreamDatabase::class.java,
                    "dream_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}