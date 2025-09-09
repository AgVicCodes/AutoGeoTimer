package com.example.autogeotimer

import android.os.Bundle

import android.content.Context

import android.location.Address
import android.location.Geocoder

import androidx.activity.enableEdgeToEdge
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

import androidx.compose.material3.MaterialTheme

import com.example.autogeotimer.domain.Coordinates
import com.example.autogeotimer.ui.HomeScreen
//import com.example.autogeotimer.ui.MyAppNavigation

import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers

import java.io.IOException


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                // MyAppNavigation()
                HomeScreen()
            }
        }
    }
}


data class Location(val address: String)


suspend fun convertAddressToCoordinates (context: Context, address: String): Coordinates? {
    return withContext (Dispatchers.IO) {
        try {
            val geocoder = Geocoder(context)
            @Suppress("DEPRECATION")
            val addresses: MutableList<Address>? = geocoder.getFromLocationName(address, 1)
            addresses?.firstOrNull()?.let {
                Coordinates(it.latitude, it.longitude)
            }
        } catch (e: IOException) {
            println("Geocoding failed for '$address': ${e.message}")
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


object AppDestinations {
    const val INPUT_ADDRESS_SCREEN = "InputAddress"
    const val SHOW_COORDINATES_SCREEN = "ShowCoordinatesScreen"
//    const val HOME_SCREEN = "HomeScreen"
    // Add more routes for other features
}