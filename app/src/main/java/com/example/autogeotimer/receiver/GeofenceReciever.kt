package com.example.autogeotimer.receiver

import android.app.PendingIntent
import android.util.Log

//import com.example.autogeotimer.R

import kotlinx.coroutines.runBlocking

import android.content.Intent
import android.content.Context
import android.content.BroadcastReceiver
//import androidx.compose.ui.text.input.KeyboardType.Companion.Uri
//import android.net.Uri
//import androidx.core.os.bundleOf

import com.example.autogeotimer.util.Notifier

import androidx.navigation.NavDeepLinkBuilder

import com.example.autogeotimer.data.db.AppDB
import com.example.autogeotimer.data.repo.SessionRepo

import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import androidx.core.net.toUri
import com.example.autogeotimer.MainActivity


class GeofenceReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val event = GeofencingEvent.fromIntent(intent) ?: return
        if (event.hasError()) {
            Log.e("GeofenceReceiver", "Error: ${event.errorCode}")
            return
        }
        val now = System.currentTimeMillis()
        val placeId = event.triggeringGeofences?.firstOrNull()?.requestId ?: return
        val transition = event.geofenceTransition
        val repo = SessionRepo(AppDB.get(context).sessionDao()) // Get SessionRepo (DI or ServiceLocator)
        // val ids = event.triggeringGeofences?.map { it.requestId }.orEmpty()

        when (transition) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> {
                // New Logic
                runBlocking { repo.openPending(placeId, now) }
                // val sid = runBlocking { repo.openPending(placeId, now) } // SessionId

                // Old Logic
                // Log.i("GeofenceReceiver", "ENTER: $ids")
                Notifier.notify(context, "Entered geofence", "Place: $placeId")
                // TODO: open session (Later persist to room)
            }
            Geofence.GEOFENCE_TRANSITION_DWELL -> {
                // Confirm dwell on the latest open session
                val open = runBlocking { repo.latestOpen(placeId) } ?: return
                runBlocking { repo.confirmDwell(open.id, enter = open.enter) }

                val intent = Intent(
                    Intent.ACTION_VIEW,
                    "autogeotimer://timer/${open.id}".toUri(),
                    context,
                    MainActivity::class.java
                ).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                }

                val pending = PendingIntent.getActivity(
                    context, 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                // this or attach pendingIntent to a built notification
                Notifier.notify(context, "Timing started", "Tap to view")

                // Deep link to timer screen with sessionID
                // val pending = NavDeepLinkBuilder(context)
                    // .setGraph(R.navigation.nav_graph)
                    // .setDestination(R.id.timerScreen)
                    // .setArguments(bundleOf("sessionId" to open.id))
                    // .createPendingIntent()
            // // New Logic
            // val sid = "" // Lookup pending session for placeId
            // val pending = NavDeepLinkBuilder(context)
            // .setGraph(R.navigation.nav_graph)
            // .setDestination(R.id.timerScreen)
            // .setArguments(bundleOf("sessionId" to sid))
            // .createPendingIntent()

            // if (sid != null) {
            // runBlocking { repo.confirmDwell(sid, enter = timeFromDB now - 5*60_000) }

            // Notifier.notify(context, "Timing started", "Tap to view")
            // }

            // // Old Logic
            // Log.i("GeofenceReceiver", "DWELL: $ids")
            // Notifier.notify(context, "Dwelling in geofence", "IDs: $ids")
            }
            Geofence.GEOFENCE_TRANSITION_EXIT  -> {
                val open = runBlocking { repo.latestOpen(placeId) } ?: return
                runBlocking {
                    if (open.start == null) repo.discard(open.id) // pass-by
                    else repo.close(open.id, now)
                }
                Notifier.notify(context, "Timing stopped", "Saved duration")

                // // New Logic
                // val sid = "" // lookup latest session for placeId
                // if (sid != null) {
                //     val session = repo.get(sid)
                //     runBlocking {
                //         if ( session.start == null ) repo.discard(sid)
                //         else repo.close(sid, now)
                //     }
                //     Notifier.notify(context, "Timing Stopped", "Duration saved")
                // }

                // // Old Logic
                // Log.i("GeofenceReceiver", "EXIT: $ids")
                // Notifier.notify(context, "Exited geofence", "IDs: $ids")
                // TODO: open session (Later persist to room)
            }
            else -> Log.w("GeofenceReceiver", "Unknown transition: $transition")
        }
    }
}
