package com.example.autogeotimer.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.autogeotimer.data.db.dao.SessionDao
import com.example.autogeotimer.data.db.entities.SessionEntity

@Database(entities = [SessionEntity::class], version = 1, exportSchema = true)
abstract class AppDB: RoomDatabase() {
    abstract fun sessionDao(): SessionDao

    companion object {
        @Volatile private var INSTANCE: AppDB? = null
        fun get (context: Context): AppDB =
            INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(context, AppDB::class.java, "autogeotimer.db")
                    .build().also { INSTANCE = it }
            }
    }
}