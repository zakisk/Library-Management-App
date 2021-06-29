package com.example.iqrabedlibrary.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [User::class, History::class], version = 3)
abstract class UserDatabase : RoomDatabase() {

    abstract fun getDao() : LibraryDao

    companion object {
        @Volatile
        private var INSTANCE: UserDatabase? = null

        fun getDatabase(context: Context) : UserDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    UserDatabase::class.java,
                    "libraryDatabase"
                )
                .fallbackToDestructiveMigration()
                .build()

                INSTANCE = instance
                //return instance
                instance
            }
        }
    }

}