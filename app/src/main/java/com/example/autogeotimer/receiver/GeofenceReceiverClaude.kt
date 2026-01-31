package com.example.autogeotimer.receiver

import android.content.BroadcastReceiver
import android.util.Log
import android.content.Intent
import android.content.Context
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import com.example.autogeotimer.MainActivity
import com.example.autogeotimer.util.Notifier
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent

class GeofenceReceiverClaude : BroadcastReceiver() {

    companion object {
        private const val TAG = "GeofenceReceiver"
        private const val PREFS_NAME = "geofence_sessions"
        private const val KEY_ENTER_TIME = "enter_time_"
        private const val KEY_DWELLING = "is_dwelling_"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val event = GeofencingEvent.fromIntent(intent) ?: return

        if (event.hasError()) {
            Log.e(TAG, "Geofence error: ${event.errorCode}")
            return
        }

        val transition = event.geofenceTransition
        val geofences =  event.triggeringGeofences ?: return

        geofences.forEach { geofence ->
            handleTransition(context, geofence.requestId, transition)
        }
    }

    private fun handleTransition(context: Context, placeId: String, transition: Int) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val enterTime = prefs.getLong(KEY_ENTER_TIME + placeId, 0L)
        val isDwelling = prefs.getBoolean(KEY_DWELLING + placeId, false)

        when (transition) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> {
                Log.i(TAG, "ENTER: $placeId")

                // // Save enter time: Old code
                //prefs.edit()
                //    .putLong(KEY_ENTER_TIME + placeId, System.currentTimeMillis())
                //    .putBoolean(KEY_DWELLING + placeId, false)
                //    .apply()

                // New format
                prefs.edit {
                    putLong(KEY_ENTER_TIME + placeId, System.currentTimeMillis())
                        .putBoolean(KEY_DWELLING + placeId, false)
                        .apply()
                }

                Notifier.notify(
                    context,
                    "Entered Location",
                    "Wait 5 min to confirm stay"
                )

                // Uncomment \/
                launchTimerActivity(context, placeId, "Location")

            }

            Geofence.GEOFENCE_TRANSITION_DWELL -> {
                Log.i(TAG, "DWELL: $placeId")

                // Mark as dwelling - stay confirmed.
                prefs.edit {
                    putBoolean(KEY_DWELLING + placeId, true)
                        .apply()
                }

                Notifier.notify(
                    context,
                    "Stay Confirmed",
                    "Stayed over 5 minutes, timing started"
                )

                // TODO: Update session on Room DB to mark dwelling confirmed
            }

            Geofence.GEOFENCE_TRANSITION_EXIT -> {
                Log.i(TAG, "EXIT: $placeId")

                if (!isDwelling) {
                    val durationMs = System.currentTimeMillis() - enterTime
                    // Change to 60000
                    val durationMin = durationMs / 60_000

                    // Less than 5 minutes stay
                    Log.i(TAG, "Pass-by detected: $durationMin minutes")

                    Notifier.notify(
                        context,
                        "Quick visit",
                        "You left after $durationMin minutes. Stay wasn't tracked!"
                    )
                } else {
                    val durationMs = System.currentTimeMillis() - enterTime
                    // Change to 60000
                    val durationMin = durationMs / 60_000

                    Log.i(TAG, "Session over: lasting $durationMin minutes")

                    Notifier.notify(
                        context,
                        "Session ended",
                        "Total time: $durationMin minutes"
                    )

                    // TODO: Close session in Room DB with exit time
                }

                // Mark as exit.
                // TODO: Check if this correct or to remove periods (.)
                prefs.edit {
                    remove(KEY_ENTER_TIME + placeId)
                        .remove(KEY_DWELLING + placeId)
                        .apply()
                }
            }
        }
    }

    private fun launchTimerActivity(context: Context, placeId: String, placeName: String) {
        // Uncomment
        val intent = Intent(context, MainActivity::class.java).apply{
            putExtra("PLACE_ID", placeId)
            putExtra("PLACE_NAME", placeName)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        ContextCompat.startActivity(context, intent, null)
    }
}