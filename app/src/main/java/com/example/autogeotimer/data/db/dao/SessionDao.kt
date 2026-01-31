package com.example.autogeotimer.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Insert
import com.example.autogeotimer.data.db.entities.SessionEntity

@Dao
interface SessionDao {
    @Insert
    suspend fun insert(entity: SessionEntity): Long

    @Query("UPDATE sessions SET start = :start WHERE id = :id")
    suspend fun updateStart(id: Long, start: Long)

    @Query("UPDATE sessions SET exit = :exit WHERE id = :id")
    suspend fun updateExit(id: Long, exit: Long)

    @Query("DELETE FROM sessions WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT * FROM sessions WHERE id = :id")
    suspend fun get(id: Long): SessionEntity?

    @Query("""
        SELECT * FROM sessions 
        WHERE placeId = :placeId 
        AND exit IS NULL 
        ORDER BY enter DESC LIMIT 1
    """)
    suspend fun latestOpenForPlace(placeId: String): SessionEntity?
}