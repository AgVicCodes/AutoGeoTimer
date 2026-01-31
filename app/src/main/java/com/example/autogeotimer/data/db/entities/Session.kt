package com.example.autogeotimer.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sessions")
data class SessionEntity (
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val placeId: String,
    val enter: Long,
    val start : Long? = null, // set at DWELL (enter + 5min)
    val exit: Long? = null
) {
    val durationMs: Long? get() = exit?.let { ex -> start?.let { ex - it } }
}