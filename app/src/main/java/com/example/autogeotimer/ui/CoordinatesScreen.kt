package com.example.autogeotimer.ui

//import android.Manifest
//import android.app.Activity
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
//import androidx.compose.runtime.mutableLongStateOf
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

import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedTextField
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.rememberLauncherForActivityResult

//import com.example.autogeotimer.util.*
import com.example.autogeotimer.domain.Location
import com.example.autogeotimer.domain.Coordinates
import com.example.autogeotimer.domain.GeocodingResult
import com.example.autogeotimer.data.geofencing.GeofenceManagerClaude

import kotlinx.coroutines.launch

//import androidx.navigation.NavController
import com.example.autogeotimer.data.db.AppDb
//import androidx.navigation.compose.rememberNavController
import com.example.autogeotimer.data.geocoding.GeocodingService
import com.example.autogeotimer.data.repo.LocationRepository

// LU10JB
@Preview(showBackground = true)
@Composable
fun ShowCoordinatesScreenPreview() {

    MaterialTheme {
        ShowCoordinatesScreen(
            postcode = "LU1 0JB",
            onNavigateBack = {}
        )
    }
}


@Composable
fun ShowCoordinatesScreen(
    postcode: String,
//    navController: NavController,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var addressString by remember { mutableStateOf<Location?>(null) }
    var locationResult by remember { mutableStateOf<Coordinates?>(null) }

    var isLoading by remember { mutableStateOf(false) }
    var locationName by remember { mutableStateOf("") }
    var geofenceStatus by remember { mutableStateOf("") }

//    var sessionId by remember { mutableLongStateOf(0L) }
    var geocodingApiResult by remember { mutableStateOf<GeocodingResult?>(null) }

    // Database
    val db = remember { AppDb.getInstance(context) }
    val locationRepo = remember { LocationRepository(db.locationDao()) }

    // Permission Launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        granted ->
        geofenceStatus = if (granted) "Permission granted. Try again!"
            else "Location permission required"
    }

    Column (
        Modifier
            .fillMaxSize()
            .padding(16.dp, 48.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Perform geocoding when the screen is first composed with a postcode
        LaunchedEffect(postcode) {

            // Re-launch if postcode changes (though it won't in this nav setup)
            isLoading = true
            locationResult = GeocodingService.convertAddressToCoordinates(postcode)
            addressString = GeocodingService.convertPostcodeToAddress(postcode)
            isLoading = false

        }

        Text("Location details")
        Spacer(Modifier.height(16.dp))

        if (isLoading) {
            CircularProgressIndicator(color = Color(0xFF6F009D))
        } else {
            locationResult?.let {
                Text("Latitude: ${it.latitude}")
                Text("Longitude: ${it.longitude}")
            } ?: Text("Coordinates not found")

            Spacer(Modifier.height(16.dp))

            addressString?.let {
                Text("Address: ${it.address}")
            }

            Spacer(Modifier.height(16.dp))

            // Location name input
            OutlinedTextField(
                value = locationName,
                onValueChange = { locationName = it },
                // // Also works
                // onValueChange = { inputLocationName -> locationName = inputLocationName },
                label = { Text("Location name") },
                placeholder = { Text("e.g., Home, Work, Gym") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            // Fixed crash after set location on use postcode
            // Save & set Geofence button
            Button(
                onClick = {
                    // Validate
                    if (locationName.isBlank()) {
                        geofenceStatus = "Enter location name"
                        return@Button
                    }

                    if (locationResult == null) {
                        geofenceStatus = "No Coordinates Available"
                        return@Button
                    }

                    // Check permission
                    if (ContextCompat.checkSelfPermission(
                        context,
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED) {
                        permissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
                        return@Button
                    }

                    geofenceStatus = "Saving location..."

                    coroutineScope.launch {
                        val coords = locationResult!!
                        val locationId = "place_${System.currentTimeMillis()}"

                        // Save to Database
                        val location = com.example.autogeotimer.data.db.entities.Location(
                            id = locationId,
                            name = locationName,
                            address = addressString?.address.toString(),
                            latitude = coords.latitude,
                            longitude = coords.longitude
                        )

                        locationRepo.saveLocation(location)

                        // Register geofence
                        val geoManager = GeofenceManagerClaude(context)
                        geoManager.add(
                            id = locationId,
                            lat = coords.latitude,
                            lng = coords.longitude
                        ) {
                            ok, error ->
                            geofenceStatus = if (ok) {
                                "Location saved & geofence active!"
                            } else {
                                "Location saved but geofence registration failed: ${error?.message}"
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                Text("Set location")
            }

            Spacer(Modifier.height(8.dp))

            // Status message
            if (geofenceStatus.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = geofenceStatus,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            Button(onClick = onNavigateBack) {
                Text("Go Back!")
            }
        }



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

}