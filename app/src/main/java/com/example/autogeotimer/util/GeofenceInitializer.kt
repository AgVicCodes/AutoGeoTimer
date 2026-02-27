package com.example.autogeotimer.util

import android.util.Log

import android.content.Context
import android.content.pm.PackageManager

import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.CoroutineScope

import androidx.core.content.ContextCompat

import com.example.autogeotimer.data.db.AppDb
import com.example.autogeotimer.data.repo.LocationRepository
import com.example.autogeotimer.data.geofencing.GeofenceManagerClaude

/*
*   This registers all active geofence(s) from database in app launch or boot.
*   It also ensures old users automatically get geofence(s) registered for saved locations.
* */

object GeofenceInitializer {

    private const val TAG = "GeofenceInitializer"

    fun registerAllGeofences(context: Context) {
        if (ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.w(TAG, "Location permission not granted, skipping geofence registration")
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = AppDb.getInstance(context)
                val locationRepo = LocationRepository(db.locationDao())
                val geoManager = GeofenceManagerClaude(context)
                // Quick TODO: refactor all claude component as main and previous as "old".

            } catch (e: Exception) {

            }
        }
    }
}