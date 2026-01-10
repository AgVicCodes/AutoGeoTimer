package com.example.autogeotimer.receiver

import android.util.Log
import android.content.Intent
import android.content.Context
import android.content.BroadcastReceiver
import com.example.autogeotimer.util.Notifier
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent


class GeofenceReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val event = GeofencingEvent.fromIntent(intent) ?: return
        if (event.hasError()) {
            Log.e("GeofenceReceiver", "Error: ${event.errorCode}")
            return
        }
        val transition = event.geofenceTransition
        val ids = event.triggeringGeofences?.map { it.requestId }.orEmpty()

        when (transition) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> {
                Log.i("GeofenceReceiver", "ENTER: $ids")
                Notifier.notify(context, "Entered geofence", "IDs: $ids")
//                TODO: open session (Later persist to room)
            }
            Geofence.GEOFENCE_TRANSITION_DWELL -> {
                Log.i("GeofenceReceiver", "DWELL: $ids")
                Notifier.notify(context, "Dwelling in geofence", "IDs: $ids")
            }
            Geofence.GEOFENCE_TRANSITION_EXIT  -> {
                Log.i("GeofenceReceiver", "EXIT: $ids")
                Notifier.notify(context, "Exited geofence", "IDs: $ids")
//                TODO: open session (Later persist to room)
            }
            else -> Log.w("GeofenceReceiver", "Unknown transition: $transition")
        }
    }
}
