package com.example.autogeotimer.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.autogeotimer.data.db.dao.SessionDao
import com.example.autogeotimer.data.db.dao.LocationDao
import com.example.autogeotimer.data.db.entities.Session
import com.example.autogeotimer.data.db.entities.Location

@Database(entities = [Session::class, Location::class], version = 1, exportSchema = false)
abstract class AppDb: RoomDatabase() {
    abstract fun sessionDao(): SessionDao
    abstract fun locationDao(): LocationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDb? = null

        fun getInstance (context: Context): AppDb {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDb::class.java,
                    "autogeotimer.db"
                    // Make sure to remove 👇🏽in production
                ).fallbackToDestructiveMigration().build()

                INSTANCE = instance
                instance
            }
        }

//        fun getInstance(context: Context): AppDb {
//            return INSTANCE ?: synchronized(this) {
//                val instance = Room.databaseBuilder(
//                    context.applicationContext,
//                    AppDb::class.java,
//                    "autogeotimer_db"
//                )
//                    .fallbackToDestructiveMigration() // Dev only - remove in production
//                    .build()
//
//                INSTANCE = instance
//                instance
//            }
//        }
    }
}