package com.example.autogeotimer.data.geofencing

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.autogeotimer.receiver.GeofenceReceiverClaude
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices

class GeofenceManagerClaude(private val context: Context) {

    private val client = LocationServices.getGeofencingClient(context)

    private fun pendingIntent(): PendingIntent =
        PendingIntent.getBroadcast(
            context, 0,
            Intent(context, GeofenceReceiverClaude::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

    fun buildRequest(id: String, lat: Double, lng:Double, radiusM: Float): GeofencingRequest {
        val fence = Geofence.Builder()
            .setRequestId(id)
            .setCircularRegion(lat, lng, radiusM)
            .setTransitionTypes(
                Geofence.GEOFENCE_TRANSITION_ENTER or
                Geofence.GEOFENCE_TRANSITION_DWELL or
                Geofence.GEOFENCE_TRANSITION_EXIT
            )
            // // CRITICAL: 5 minutes loitering or dwell delay
            // // Try:
            //.setLoiteringDelay(5 * 60000)
            .setLoiteringDelay(5 * 60_000)
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .build()

        return GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofence(fence)
            .build()
    }

    @SuppressLint("MissingPermission")
    fun add(
        id: String,
        lat: Double,
        lng: Double,
        radiusM: Float = 150f,
        onResult: (ok: Boolean, error: Exception?) -> Unit
    ) {
        val request = buildRequest(id, lat, lng, radiusM)
        client.addGeofences(request, pendingIntent())
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { e -> onResult(false, e) }
    }

    fun removeById(
        id: String,
        onResult: (ok: Boolean, error: Exception?) -> Unit
    ) {
        client.removeGeofences(listOf(id))
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { e -> onResult(false, e) }
    }

    fun removeAll(onResult: (ok: Boolean, error: Exception?) -> Unit) {
        client.removeGeofences(pendingIntent())
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { e -> onResult(false, e) }
    }
}