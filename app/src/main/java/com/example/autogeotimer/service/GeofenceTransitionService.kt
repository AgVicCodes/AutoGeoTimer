package com.example.autogeotimer.service

import android.app.IntentService
import android.content.Intent
import android.util.Log
import com.example.autogeotimer.data.db.AppDb
import com.example.autogeotimer.data.repo.SessionRepository
import com.example.autogeotimer.util.Notifier
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import kotlinx.coroutines.runBlocking

/**
 * Alternative to BroadcastReceiver - more reliable on Android 12+
 */
class GeofenceTransitionsIntentService : IntentService("GeofenceTransitionsIntentService") {

    companion object {
        private const val TAG = "GeofenceService"
    }

    override fun onHandleIntent(intent: Intent?) {
        val event = GeofencingEvent.fromIntent(intent ?: return) ?: return

        if (event.hasError()) {
            Log.e(TAG, "Geofence error: ${event.errorCode}")
            return
        }

        val transition = event.geofenceTransition
        val geofences = event.triggeringGeofences ?: return

        val db = AppDb.getInstance(applicationContext)
        val sessionRepo = SessionRepository(db.sessionDao())
        val locationDao = db.locationDao()

        geofences.forEach { geofence ->
            runBlocking {
                handleTransition(geofence.requestId, transition, sessionRepo, locationDao)
            }
        }
    }

    private suspend fun handleTransition(
        locationId: String,
        transition: Int,
        sessionRepo: SessionRepository,
        locationDao: com.example.autogeotimer.data.db.dao.LocationDao
    ) {
        val location = locationDao.getById(locationId)
        val locationName = location?.name ?: "Unknown Location"

        when (transition) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> {
                Log.i(TAG, "ENTER: $locationId ($locationName)")
                sessionRepo.startSession(locationId)
                Notifier.notify(applicationContext, "Entered $locationName", "Wait 5 min to confirm")
            }

            Geofence.GEOFENCE_TRANSITION_DWELL -> {
                Log.i(TAG, "DWELL confirmed: $locationId ($locationName)")
                sessionRepo.confirmDwell(locationId)
                Notifier.notify(applicationContext, "✓ Stay confirmed at $locationName", "Timer active")
            }

            Geofence.GEOFENCE_TRANSITION_EXIT -> {
                Log.i(TAG, "EXIT: $locationId ($locationName)")
                val exitTime = System.currentTimeMillis()
                sessionRepo.endSession(locationId, exitTime)
                Notifier.notify(applicationContext, "Left $locationName", "Session ended")
            }
        }
    }
}