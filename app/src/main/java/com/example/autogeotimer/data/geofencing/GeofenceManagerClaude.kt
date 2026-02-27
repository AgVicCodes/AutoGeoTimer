package com.example.autogeotimer.data.geofencing

import android.os.Build
import android.util.Log

import android.content.Intent
import android.content.Context

import android.app.PendingIntent

import android.annotation.SuppressLint

import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices

import com.example.autogeotimer.receiver.GeofenceReceiver

class GeofenceManagerClaude(private val context: Context) {

    private val client = LocationServices.getGeofencingClient(context)

    private fun pendingIntent(): PendingIntent {
        val intent = Intent(context, GeofenceReceiver::class.java).apply {
            action = "ACTION_GEOFENCE_EVENT" // Use a custom action string
            setPackage(context.packageName)  // <--- ADD THIS LINE
        }
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        return PendingIntent.getBroadcast(context, 0, intent, flags)

//        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            PendingIntent.getBroadcast(
//                context, 0,
//                Intent(context, GeofenceReceiver::class.java),
//                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
//            )
//        } else {
//            PendingIntent.getBroadcast(
//                context, 0,
//                Intent(context, GeofenceReceiver::class.java),
//                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//            )
//        }
    }

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

    fun clearAllGeofences(onResult: (ok: Boolean, error: Exception?) -> Unit) {
        client.removeGeofences(pendingIntent())
            .addOnSuccessListener {
                Log.d("GeoFenceManager", "✓ Cleared all old geofences")
                onResult(true, null)
            }
            .addOnFailureListener { error ->
                Log.e("GeoFenceManager", "Failed to clear geofences: ${error.message}")
                onResult(false, error)
            }
    }
}