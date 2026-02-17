package com.example.autogeotimer.data.db.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "sessions",
    foreignKeys = [
        ForeignKey(
            entity = Location::class,
            parentColumns = ["id"],
            childColumns = ["locationId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("locationId")] // To speed up queries
)

data class Session (
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val locationId: String,
    val enterTime: Long,
    // I had to uncomment for compatibility with TimerVM
    val dwellConfirmed : Boolean = false, // set at DWELL (true after enter + 5min)
    val exitTime: Long? = null,
    // Calculated on exit
    val durationSeconds: Long? = null // get() = exit?.let { ex -> start?.let { ex - it } }.
)