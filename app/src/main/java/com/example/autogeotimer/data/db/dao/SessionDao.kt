package com.example.autogeotimer.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Insert
import androidx.room.Update
import com.example.autogeotimer.data.db.entities.Session
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {
    @Insert
    suspend fun insert(session: Session): Long

    @Update
    suspend fun update(session: Session)

    @Query("SELECT * FROM sessions WHERE id = :id")
    suspend fun getById(id: Long): Session?

    @Query("SELECT * FROM sessions ORDER BY enterTime DESC LIMIT :limit")
    suspend fun getRecent(limit: Int = 50): List<Session>

    @Query("""
        SELECT * FROM sessions 
        WHERE locationId = :locationId 
        ORDER BY enterTime DESC
    """)
    suspend fun getByLocation(locationId: String): List<Session>

    @Query("""
        SELECT * FROM sessions
        WHERE enterTime >= :startTime
        AND enterTime <= :endTime
        ORDER BY enterTime DESC
    """)
    suspend fun getInRange(startTime: Long, endTime: Long): List<Session>

    @Query("SELECT * FROM sessions WHERE exitTime IS NULL")
    suspend fun getActiveSessions(): List<Session>

    @Query("SELECT * FROM sessions WHERE locationId = :locationId AND exitTime IS NULL LIMIT 1")
    fun getActiveSessionFlow(locationId: String): Flow<Session?>

    @Query("""
        UPDATE sessions
        SET dwellConfirmed = 1
        WHERE locationId  = :locationId
        AND exitTime IS NULL
    """)
    suspend fun markDwelling(locationId: String)

    // Might have to remove set dwellConfirmed = 0
    // from the list of set later for room update error fix
    @Query("""
        UPDATE sessions
        SET dwellConfirmed = 0, 
            exitTime = :exitTime, 
            durationSeconds = :durationSeconds
        WHERE locationId = :locationId 
        AND exitTime IS NULL
    """)
    suspend fun closeSession(locationId: String, exitTime: Long, durationSeconds: Long)

    @Query("UPDATE sessions SET enterTime = :start WHERE locationId = :locationId")
    suspend fun updateStart(locationId: String, start: Long)

    @Query("UPDATE sessions SET exitTime = :exitTime WHERE locationId = :locationId")
    suspend fun updateExit(locationId: String, exitTime: Long)

    // User's shouldn't be able to delete sessions
    // @Query("DELETE FROM sessions WHERE id = :id")
    // suspend fun deleteById(id: Long)

    @Query("""
        SELECT * FROM sessions 
        WHERE locationId = :locationId 
        AND exitTime IS NULL 
        ORDER BY enterTime DESC LIMIT 1
    """)
    suspend fun latestOpenForPlace(locationId: String): Session?
}