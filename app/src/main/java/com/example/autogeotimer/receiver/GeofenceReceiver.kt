package com.example.autogeotimer.receiver


import android.util.Log

import android.content.Intent
import android.content.Context
import android.content.BroadcastReceiver
import androidx.core.app.NotificationManagerCompat

import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.CoroutineScope

import androidx.core.content.ContextCompat

import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent

import com.example.autogeotimer.MainActivity
import com.example.autogeotimer.util.Notifier
import com.example.autogeotimer.data.db.AppDb
import com.example.autogeotimer.data.db.dao.LocationDao
import com.example.autogeotimer.data.repo.SessionRepository
import com.example.autogeotimer.ui.TimerActivityClaude

class GeofenceReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "GeofenceReceiver"
        private const val PREFS_NAME = "geofence_sessions"
        private const val KEY_ENTER_TIME = "enter_time_"
        private const val KEY_DWELLING = "is_dwelling_"
    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onReceive(context: Context, intent: Intent) {
        val event = GeofencingEvent.fromIntent(intent) ?: return

        if (event.hasError()) {
            Log.e(TAG, "Geofence error: ${event.errorCode}")
            return
        }

        val transition = event.geofenceTransition
        val geofences =  event.triggeringGeofences ?: return

        val db = AppDb.getInstance(context)
        val sessionRepo = SessionRepository(db.sessionDao())
        val locationDao = db.locationDao()

        val pending = goAsync()
            scope.launch {
                try {
                    geofences.forEach { geofence ->
                        handleTransition(context, geofence.requestId, transition, sessionRepo, locationDao)
                    }
                } finally {
                    pending.finish()
                }
            }

    }

    private suspend fun handleTransition(
        context: Context,
        locationId: String,
        transition: Int,
        sessionRepo: SessionRepository,
        locationDao: LocationDao
    ) {
        val location = locationDao.getById(locationId)
        val locationName = location?.name ?: "Unknown Location"

        when (transition) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> {

                Log.i(TAG, "ENTER: $locationId ($locationName)")

                val timerIntent = Intent(context, TimerActivityClaude::class.java).apply {
                    putExtra("PLACE_ID", locationId)
                    putExtra("PLACE_NAME", locationName)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                }

                val sessionId = sessionRepo.startSession(locationId)

                Log.i(TAG, "Started session #$sessionId for $locationName")

                Log.d("Notifier", "enabled=${NotificationManagerCompat.from(context).areNotificationsEnabled()}")

                Notifier.notify(
                    context,
                    "Entered $locationName",
                    "Timer starting... Wait 5 min to confirm stay",
//                    "Tap to open timer",
                    tapIntent = timerIntent
                )

//                Commented for ConfirmLocation Geofence registration
            }

            Geofence.GEOFENCE_TRANSITION_DWELL -> {
                Log.i(TAG, "Dwell Confirmed: $locationId ($locationName)")

                sessionRepo.confirmDwell(locationId)

                Notifier.notify(
                    context,
                    "Stay Confirmed at $locationName",
                    "Stayed over 5 minutes, timing now active"
                )
            }

            Geofence.GEOFENCE_TRANSITION_EXIT -> {
                Log.i(TAG, "EXIT: $locationId ($locationName)")

                val exitTime = System.currentTimeMillis()

                // Get active session to check if dwelling was confirmed
                val activeSessions = sessionRepo.getLocationSession(locationId)
                val lastSession = activeSessions.firstOrNull { it.exitTime == null }

                if (lastSession?.dwellConfirmed == false) {
                    // Change to 60000
                    val durationMin = (exitTime - lastSession.enterTime) / 60000
                    // Less than 5 minutes stay
                    Log.i(TAG, "Pass-by detected: $durationMin minutes")

                    Notifier.notify(
                        context,
                        "Quick visit to $locationName",
                        "You left after $durationMin minutes. Stay wasn't tracked!"
                    )
                    sessionRepo.endSession(locationId, exitTime)
                } else {
                    // Normal exit after dwelling
                    sessionRepo.endSession(locationId, exitTime)

                    // Change to 60000
                    val durationMin = lastSession?.let {
                        (exitTime - it.enterTime) / 60000
                    } ?: 0

                    Log.i(TAG, "Session over: lasting $durationMin minutes")

                    Notifier.notify(
                        context,
                        "Left $locationName",
                        "Session ended! Total time: ${formatTime(durationMin)} minutes"
                    )
                    // TODO: Close session in Room DB with exit time
                }

                // Mark as exit.
                // Check if this correct or to remove periods (.)
                // Not using this anymore :(
//                prefs.edit {
//                    remove(KEY_ENTER_TIME + locationId)
//                        .remove(KEY_DWELLING + locationId)
//                        .apply()
//                }
            }
        }
    }

    private fun formatTime(minutes: Long): String {
        val hours = minutes / 60
        val minutes = minutes % 60
        return if (hours > 0) "${hours}h ${minutes}m" else "${minutes}m"
    }

    private fun launchTimerActivity(context: Context, locationId: String, locationName: String) {
        // Uncomment and change to TimerActivity
        val intent = Intent(context, MainActivity::class.java).apply {
            putExtra("PLACE_ID", locationId)
            putExtra("PLACE_NAME", locationName)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        ContextCompat.startActivity(context, intent, null)
    }
}