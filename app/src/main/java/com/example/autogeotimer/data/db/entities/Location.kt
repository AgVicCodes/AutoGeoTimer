package com.example.autogeotimer.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "locations")
data class Location(
    @PrimaryKey val id: String,                         // E.g place_463727320198
    val name: String,                                   // Home, Work, Gym
    val address: String,                                // Full address from geocoder
    val latitude: Double,
    val longitude: Double,
    val radiusMeters: Float = 150f,
    val createdAt: Long = System.currentTimeMillis(),
    val isActive: Boolean = true                        // Can disable without deleting
)