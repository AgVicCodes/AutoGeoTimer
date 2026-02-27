package com.example.autogeotimer


import android.os.Bundle

import android.content.Context

import android.location.Address
import android.location.Geocoder
import android.util.Log

import androidx.activity.enableEdgeToEdge
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

import androidx.compose.material3.MaterialTheme
import com.example.autogeotimer.data.db.AppDb
import com.example.autogeotimer.data.geofencing.GeofenceManagerClaude

import com.example.autogeotimer.domain.Coordinates
import com.example.autogeotimer.domain.Location
import com.example.autogeotimer.ui.MyAppNavigation
import com.example.autogeotimer.util.GeofenceInitializer

import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers

import java.io.IOException

/*  TODO: Complete geofence activation
*   complete the logic for entering geofence and starting timing
*   also fix tick glitch
* */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ⚠️ TEMPORARY - Clear all old geofences
        clearAllGeofences()

        // Then register fresh ones
        GeofenceInitializer.registerAllGeofences(this)

        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                MyAppNavigation()
            }
        }
    }

    private fun clearAllGeofences() {
        val geoManager = GeofenceManagerClaude(this)
        geoManager.clearAllGeofences { ok, error ->
            if (ok) {
                Log.d("MainActivity", "✓ Cleared all old geofences")
                GeofenceInitializer.registerAllGeofences(this)
            } else {
                Log.e("MainActivity", "Failed to clear geofences: ${error?.message}")
            }
        }
    }
}

//class MainActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        // Temp
//        AppDb.clearInstance(this)
//
//        GeofenceInitializer.registerAllGeofences(this)
//        enableEdgeToEdge()
//        setContent {
//            MaterialTheme {
//                MyAppNavigation()
//            }
//        }
//    }
//}

// USE LOCATION MK42 6HF
// Set a timer on Tiktok