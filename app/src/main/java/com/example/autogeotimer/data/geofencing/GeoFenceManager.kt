//package com.example.autogeotimer.data.geofencing
//
//import android.os.Build
//
//import android.annotation.SuppressLint
//import android.content.Intent
//import android.content.Context
//
//import android.app.PendingIntent
//
//import com.google.android.gms.location.Geofence
//import com.google.android.gms.location.LocationServices
//import com.google.android.gms.location.GeofencingRequest
//
//import com.example.autogeotimer.receiver.GeofenceReceiver
//
//
//class GeoFenceManager(private val context: Context) {
//
//    private val client = LocationServices.getGeofencingClient(context)
//
//    private fun pendingIntent(): PendingIntent {
//        val intent = Intent(context, GeofenceReceiver::class.java)
//        val flags =
//            PendingIntent.FLAG_UPDATE_CURRENT or
//                (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_MUTABLE else 0)
//
//        return PendingIntent.getBroadcast(context, 0, intent, flags)
//
//    }
//
//
//    fun buildRequest(id: String, lat: Double, lng: Double, radiusM: Float): GeofencingRequest {
//        val fence = Geofence.Builder()
//            .setRequestId(id)
//            .setCircularRegion(lat, lng, radiusM)
//            .setTransitionTypes(
//                Geofence.GEOFENCE_TRANSITION_ENTER or
//                Geofence.GEOFENCE_TRANSITION_EXIT or
//                Geofence.GEOFENCE_TRANSITION_DWELL
//            )
//            .setLoiteringDelay(5 * 60_000) // Dwell for at least 2 Minutes to trigger
//            .setExpirationDuration(Geofence.NEVER_EXPIRE)
//            .build()
//
//        return GeofencingRequest.Builder()
//            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
//            .addGeofence(fence)
//            .build()
//    }
//
//    @SuppressLint("MissingPermission") // Must check permissions before calling
//    fun add(
//        id: String,
//        lat: Double,
//        lng: Double,
//        radiusM: Float = 150f,
//        onResult: (ok: Boolean, error: Exception?) -> Unit
//    ) {
//        val request = buildRequest(id, lat, lng, radiusM)
//        client.addGeofences(request, pendingIntent())
//            .addOnSuccessListener { onResult(true, null) }
//            .addOnFailureListener { e -> onResult(false, e) }
//    }
//
//    fun removeById(
//        id: String,
//        onResult: (ok: Boolean, error: Exception?) -> Unit
//    ) {
//        client.removeGeofences(listOf(id))
//            .addOnSuccessListener { onResult(true, null) }
//            .addOnFailureListener { e -> onResult(false, e) }
//    }
//
//    fun removeAll(onResult: (ok: Boolean, error: Exception?) -> Unit) {
//        client.removeGeofences(pendingIntent())
//            .addOnSuccessListener { onResult(true, null) }
//            .addOnFailureListener { e -> onResult(false, e) }
//    }
//}