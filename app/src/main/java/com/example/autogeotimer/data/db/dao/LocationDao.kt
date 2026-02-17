package com.example.autogeotimer.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update
import com.example.autogeotimer.data.db.entities.Location

@Dao
interface LocationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(location: Location)

    @Query("SELECT * FROM locations WHERE isActive = 1 ORDER BY createdAt DESC")
    suspend fun getAllActive(): List<Location>

    @Query("SELECT * FROM locations WHERE id = :id")
    suspend fun getById(id: String): Location?

    @Update
    suspend fun update(location: Location)

    @Query("UPDATE locations SET isActive = 0 WHERE id = :id")
    suspend fun deactivate(id: String)

    @Delete
    suspend fun delete(location: Location)

    companion object
}