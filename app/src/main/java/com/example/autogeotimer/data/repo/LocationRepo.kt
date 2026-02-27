package com.example.autogeotimer.data.repo

import com.example.autogeotimer.data.db.dao.LocationDao
import com.example.autogeotimer.data.db.entities.Location

// For passing database query operations to functions usable in composable files

class LocationRepository(private val locationDao: LocationDao) {
    suspend fun getLocation(id: String) = locationDao.getById(id)
    suspend fun getActiveLocations() = locationDao.getAllActive()
    suspend fun saveLocation(location: Location) = locationDao.insert(location)
    suspend fun updateLocation(location: Location) = locationDao.update(location)
    suspend fun deleteLocation(id: String) = locationDao.deactivate(id)
}
