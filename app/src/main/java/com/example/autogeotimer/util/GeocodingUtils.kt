package com.example.autogeotimer.util

import android.content.Context
import android.location.Address
import android.location.Geocoder
import com.example.autogeotimer.domain.Coordinates
import com.example.autogeotimer.domain.Location
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

suspend fun convertCoordinatesToAddress(
    context: Context,
    coords: Coordinates
): Location? {
    return withContext(Dispatchers.IO) {
        try {
            val geocoder = Geocoder(context)

            @Suppress("DEPRECATION")
            val addresses: MutableList<Address>? = geocoder.getFromLocation(
                coords.latitude,
                coords.longitude,
                1 // Max results
            )

            addresses?.firstOrNull()?.let { androidAddress ->
                val fullAddress = (0..androidAddress.maxAddressLineIndex)
                    .mapNotNull { i -> androidAddress.getAddressLine(i) }
                    .joinToString(separator = "\n")

                if (fullAddress.isNotBlank()) {
                    Location(fullAddress)
                } else {
                    // Fallback: Construct from individual components
                    val parts = listOfNotNull(
                        androidAddress.featureName,
                        androidAddress.thoroughfare,
                        androidAddress.locality,
                        androidAddress.adminArea,
                        androidAddress.postalCode,
                        androidAddress.countryCode,
                        androidAddress.countryName
                    )

                    if (parts.isNotEmpty()) {
                        Location(parts.joinToString(", "))
                    } else {
                        null
                    }
                }
            }
        } catch (e: IOException) {
            println("Reverse geocoding failed for coordinates (${coords.latitude}, ${coords.longitude}): ${e.message}")
            e.printStackTrace()
            null
        }
    }
}

suspend fun convertPostcodeToCoordinates (context: Context, postcode: String): Coordinates? {
    return withContext (Dispatchers.IO) {
        try {
            val geocoder = Geocoder(context)
            @Suppress("DEPRECATION")
            val addresses: MutableList<Address>? = geocoder.getFromLocationName(postcode, 1)
            addresses?.firstOrNull()?.let {
                Coordinates(it.latitude, it.longitude)
            }
        } catch (e: IOException) {
            println("Geocoding failed for '$postcode': ${e.message}")
            e.printStackTrace()
            null
        }
    }
}

suspend fun convertPostcodeToAddress (context: Context, postcode: String): Location? {
    return withContext (Dispatchers.IO) {
        try {
            val geocoder = Geocoder(context)
            @Suppress("DEPRECATION")
            val addresses: MutableList<Address>? = geocoder.getFromLocationName(postcode, 1)
            addresses?.firstOrNull()?.let { androidAddress ->
                val fullAddress = (0..androidAddress.maxAddressLineIndex)
                    .mapNotNull { i -> androidAddress.getAddressLine(i) }
                    .joinToString(separator = "\n")

                if (fullAddress.isNotBlank()) {
                    Location(fullAddress)
                } else {
                    null
                }

            }
        } catch (e: IOException) {
            println("Geocoding failed for '$postcode': ${e.message}")
            e.printStackTrace()
            null
        }
    }
}
