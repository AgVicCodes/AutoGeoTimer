package com.example.autogeotimer.ui


import android.Manifest
import android.content.pm.PackageManager

import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.Arrangement
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.rememberLauncherForActivityResult

import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope

import androidx.compose.material3.Card
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth

import kotlinx.coroutines.launch
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import com.example.autogeotimer.domain.Coordinates
import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.autogeotimer.data.db.AppDb
import com.example.autogeotimer.data.geocoding.GeocodingService
import com.example.autogeotimer.domain.Location
import com.example.autogeotimer.util.*
import com.example.autogeotimer.data.location.LocationProvider
import com.example.autogeotimer.data.geofencing.GeofenceManagerClaude
import com.example.autogeotimer.data.repo.LocationRepository

@Preview
@Composable
fun ConfirmLocationScreenPreview() {

    val context = LocalContext.current
    val navController = NavController(context)

    MaterialTheme {
        ConfirmLocationScreen(
            navController = navController,
            onBackNavigation = {}
        )
    }
}

private fun fetchCurrentLocation(
    context: android.content.Context,
    scope: kotlinx.coroutines.CoroutineScope,
    onLoading: (Boolean) -> Unit,
    onSuccess: (Coordinates) -> Unit,
    onError: (String) -> Unit
) {
    scope.launch {
        onLoading(true)
        LocationProvider(context)
            .getCurrentLocation()
            .onSuccess { coords -> onSuccess(coords) }
            .onFailure { error -> onError(error.message ?: "Location error") }
        onLoading(false)
    }
}

@Composable
fun ConfirmLocationScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    onBackNavigation: () -> Unit
) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackBar = remember { SnackbarHostState() }

    // State
    var geofenceStatus by remember { mutableStateOf("") }
    var isLoadingAddress by remember { mutableStateOf(false) }
    var isLoadingLocation by remember { mutableStateOf(false) }
    var address by remember { mutableStateOf<Location?>(null) }
    var coords by remember { mutableStateOf<Coordinates?>(null) }
    var geofenceReady by remember { mutableStateOf<Boolean>(false) }

    // Would likely remove
    var locationName by remember { mutableStateOf<String>("") }
    var placeId by remember { mutableStateOf<String>("place_${System.currentTimeMillis()}") }

    // Database
    val db = remember { AppDb.getInstance(context) }
    val locationRepo = remember { LocationRepository(db.locationDao()) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            fetchCurrentLocation(
                scope = scope,
                context = context,
                onSuccess = { coords = it },
                onLoading = { isLoadingLocation = it },
                onError = { scope.launch { snackBar.showSnackbar(it) } }
            )
        } else {
            scope.launch { snackBar.showSnackbar("Location permission denied.") }
        }
    }

    Scaffold (snackbarHost = { SnackbarHost(snackBar) }) { padding ->
        Column (
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text("Confirm your location")

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = { locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION) },
                enabled = !isLoadingLocation,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isLoadingLocation) "Retrieving Location..." else "Get my Current Location")
            }

            Spacer(Modifier.height(12.dp))

            if (isLoadingLocation) {
                CircularProgressIndicator()
            } else {
                coords?.let {
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Coordinates",
                            )
                            Spacer(Modifier.height(8.dp))
                            Text("Latitude: ${it.latitude}")
                            Text("Longitude: ${it.longitude}")
                        }
                    }
                } ?: Text("No location fetched yet")
            }

            Spacer(Modifier.height(16.dp))

            // Input location name
            OutlinedTextField(
                value = locationName,
                label = { Text("Location Name") },
                onValueChange = { locationName = it },
                placeholder = { Text("Home, Gym, Work...") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    coords ?. let {
                        scope.launch {
                            isLoadingAddress = true
                            address = GeocodingService.reverseGeocode(it.latitude, it.longitude)
                            isLoadingAddress = false
                        }
                    }
                },
                enabled = coords != null && !isLoadingAddress,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Get address from coordinates")
            }

            Spacer(Modifier.height(8.dp))

            if (isLoadingAddress) {
                CircularProgressIndicator()
            } else {
                address ?. let { nonNullAddress ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Address: ${nonNullAddress.address}")
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    if (locationName.isBlank()) {
                        geofenceStatus = "Please enter a location name..."
                        // Terminates the Button function early
                        return@Button
                    }

                    // Checks if permission has been given
                    if (ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED) {
                        geofenceStatus = "Location Permission needed"
                        return@Button
                    }

                    coords ?. let { coordinate ->

                        geofenceStatus = "Setting geofence..."
                        placeId = "place_${System.currentTimeMillis()}"

                        scope.launch {
                            try {
                                // CRITICAL: Save to Database FIRST

                                val location = com.example.autogeotimer.data.db.entities.Location(
                                        id = placeId,
                                        name = locationName,
                                        address = address?.address,
                                        latitude = coordinate.latitude,
                                        longitude = coordinate.longitude
                                    )

                                android.util.Log.d("ConfirmLocation", "Saving location to database...")
                                locationRepo.saveLocation(location)
                                android.util.Log.d("ConfirmLocation", "✓ Location saved to database")

                                // THEN register geofence
                                android.util.Log.d("ConfirmLocation", "Registering geofence...")
                                val geoManager = GeofenceManagerClaude(context)

                                geoManager.add(
                                    id = placeId,
                                    lat = coordinate.latitude,
                                    lng = coordinate.longitude,
                                    radiusM = 150f
                                ) { ok, error ->
                                    geofenceStatus = if (ok) {
                                        android.util.Log.d(
                                            "ConfirmLocation",
                                            "✓ Geofence registered successfully"
                                        )
                                        geofenceReady = true
                                        "✓ Location saved & geofence active!"
                                    } else {
                                        android.util.Log.e(
                                            "ConfirmLocation",
                                            "✗ Geofence registration failed: ${error?.message}"
                                        )
                                        "Failed: ${error?.message}"
                                    }
//                                "Geofence Added Successfully!"
//
//                                    else "Failed: ${error?.message}"
                                }
                            } catch (e: Exception) {
                                android.util.Log.e(
                                    "ConfirmLocation",
                                    "Exception during setup", e
                                )
                                geofenceStatus = "Error: ${e.message}"
                            }
                        }
                    }
                },
                enabled = coords != null,
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(width = 2.dp, color = Color(0xFFAA99FF)),
            ) {
                Text("Set geofence at this location")
            }

            if (geofenceStatus.isNotEmpty()) {
                Text(geofenceStatus)
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Debug to set test coordinates
            if (coords == null) {
                OutlinedButton(
                    onClick = { coords = Coordinates(51.5074, -0.1278) },
                    border = BorderStroke(width = 1.dp, color = Color.Green),
                    modifier = Modifier.fillMaxWidth()
                    //, shape = RoundedCornerShape(0)
                ) {
                    Text("Test with London Coordinates")
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    navController.navigate("TimerScreen/${placeId}/${locationName}")
                },
                enabled = coords != null && geofenceReady
            ) {
                Text("Navigate to timer screen")
            }

            Text("TimerScreen/${placeId}/${locationName}")

            Spacer(modifier = Modifier.height(32.dp))

            Button(onClick = onBackNavigation ) {
                Text("Go Back")
            }
        }
    }
}