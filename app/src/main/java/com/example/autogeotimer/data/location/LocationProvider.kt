package com.example.autogeotimer.data.location

import android.content.Context

import com.example.autogeotimer.domain.Coordinates

import com.google.android.gms.location.Priority
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext



class LocationProvider(private val context: Context) {

    private val fused = LocationServices.getFusedLocationProviderClient(context)

    suspend fun getCurrentLocation(): Result<Coordinates> = withContext(Dispatchers.Main) {
        try {
            val priority_ = Priority.PRIORITY_HIGH_ACCURACY
            val location = fused.getCurrentLocation(priority_, CancellationTokenSource().token).await()
            if (location != null) Result.success(Coordinates(location.latitude, location.longitude))
            else Result.failure(IllegalStateException("No location fix. Ensure GPS is on."))
        }

        catch (e: SecurityException) {
            Result.failure(IllegalStateException("Location permission denied.", e))
        }

        catch (e: Exception) {
            Result.failure(IllegalStateException("Failed to get location: ${e.message}", e))
        }
    }

}