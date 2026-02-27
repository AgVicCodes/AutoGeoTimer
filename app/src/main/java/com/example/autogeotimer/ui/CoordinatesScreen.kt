package com.example.autogeotimer.ui

import android.Manifest
import android.content.pm.PackageManager

import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview

import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.rememberCoroutineScope

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth

import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.CircularProgressIndicator

import androidx.core.content.ContextCompat

import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.rememberLauncherForActivityResult

import com.example.autogeotimer.domain.Location
import com.example.autogeotimer.AppDestinations
import com.example.autogeotimer.domain.Coordinates
import com.example.autogeotimer.domain.GeocodingResult
import com.example.autogeotimer.util.*
import com.example.autogeotimer.data.geofencing.GeofenceManagerClaude

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

import androidx.navigation.NavController
import androidx.navigation.ActivityNavigatorExtras
import androidx.navigation.compose.rememberNavController

@Preview(showBackground = true)
@Composable
fun ShowCoordinatesScreenPreview() {

    var navController = rememberNavController()

    MaterialTheme {
        ShowCoordinatesScreen(
            postcode = "LU1 0JB",
            navController = navController,
            onNavigateBack = {}
        )
    }
}

@Composable
fun ShowCoordinatesScreen(
    postcode: String,
    navController: NavController,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(true) }
    var sessionId by remember { mutableLongStateOf(0L) }
    var geofenceStatus by remember { mutableStateOf("") }
    var addressString by remember { mutableStateOf<Location?>(null) }
    var locationResult by remember { mutableStateOf<Coordinates?>(null) }
    var geocodingApiResult by remember { mutableStateOf<GeocodingResult?>(null) }

    // Perform geocoding when the screen is first composed with a postcode
    LaunchedEffect(key1 = postcode) {

        // Re-launch if postcode changes (though it won't in this nav setup)

        isLoading = true

        coroutineScope.launch {
            locationResult = convertPostcodeToCoordinates(context, postcode)
            addressString = convertPostcodeToAddress(context, postcode)
            isLoading = false
        }

        // geocodingApiResult = convertAddressToCoordinatesDetailed(context, postcode) // Your actual geocoding function
        // --- Placeholder for your geocoding function call ---

//        delay(1000) // Simulate network delay

        geocodingApiResult = if (postcode.equals("error", ignoreCase = true)) {
            GeocodingResult.Error("Simulated geocoding error for '$postcode'")
        } else if (postcode.equals("Not found", ignoreCase = true)) {
            GeocodingResult.NotFound
        } else {
            GeocodingResult.Success(Coordinates(51.5074, 0.1278)) // Dummy success
        }
        // --- End Placeholder ---
        isLoading = false
    }

    Column (
        Modifier
            .fillMaxSize()
            .padding(16.dp, 48.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Showing Coordinates for: $postcode", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))
        Button (
            onClick = {
                isLoading = true
                coroutineScope.launch {
                    locationResult = convertPostcodeToCoordinates(context, postcode)
                    addressString = convertPostcodeToAddress(context, postcode)
                    isLoading = false
                }
            },
            Modifier.padding(0.dp, 8.dp)
        ) {
            Text("Convert Address")
        }

        // Relaunch and change coordinates to correct results after
        if (isLoading) {
            CircularProgressIndicator(color = Color(0xFF6F009D))
        } else {
            when (val result = geocodingApiResult) {
                is GeocodingResult.Success -> {
                    Text("Latitude: ${result.coordinates.latitude}")
                    Text("Longitude: ${(result.coordinates.longitude)}")
                }
                is GeocodingResult.Error -> {
                    Text("Error: ${result.message}", color = MaterialTheme.colorScheme.error)
                }
                is GeocodingResult.NotFound -> {
                    Text("Address not found by geocoder.", color = MaterialTheme.colorScheme.error)
                }
                null -> {
                    Text("Geocoding result not yet available.") // Should not happen if isLoading is handled
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        val permissionLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) {
            granted ->
            geofenceStatus = if (granted) {
                "Permission granted. Try again."
            } else {
                "Location permission required"
            }
        }
        Button(
            onClick = {
                // Checking permission
                if (ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    return@Button
                }

                locationResult?.let { coords ->
                    val geoManager = GeofenceManagerClaude(context)
                    geoManager.add(
                        id = "place_${System.currentTimeMillis()}",
                        lat = coords.latitude,
                        lng = coords.longitude,
                        radiusM = 150f
                    ) {
                        ok, error ->
                        geofenceStatus = if (ok) "Geofence Added!"
                                         else "Failed: ${error?.message}"
                    }
                } ?: run {
                    geofenceStatus = "No coordinates available"
                }
            }
        ) {
            Text("Set Geofence Here")
        }

        if (geofenceStatus.isNotEmpty()) {
            Text(geofenceStatus, color = if (geofenceStatus.startsWith("G")) Color.Green else Color.Red)
        }

        Spacer(modifier = Modifier.height(32.dp))

        if (isLoading) {
            Text("Loading...")
        } else {
            locationResult?.let {
                Text("Main Logic:")
                Text("Latitude: ${it.latitude}")
                Text("Longitude: ${it.longitude}")
            } ?: Text("Coordinates couldn't be obtained")

            addressString?.let {
                Text("\n")
                Text("Address: ${it.address}")
            } ?: Text("Address not found")
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                val locationName = "Work"
                var locationId = System.currentTimeMillis()
                navController.navigate("${AppDestinations.TIMER_SCREEN}/$locationId/$locationName")
            }
        ) {
            Text("Go to timer screen")
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = onNavigateBack) {
            Text("Go Back")
        }
    }
}