package com.example.autogeotimer.ui

import androidx.compose.runtime.*
import androidx.compose.material3.*

import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyColumn

import com.example.autogeotimer.data.db.AppDb
import com.example.autogeotimer.data.db.entities.Location
import com.example.autogeotimer.data.repo.LocationRepository
import com.example.autogeotimer.data.geofencing.GeofenceManagerClaude

import kotlinx.coroutines.launch

@Preview
@Composable
fun GeofenceHistoryPreview() {
    MaterialTheme {
        GeofenceHistoryScreen ( onNavigateBack = {} )
    }
}

@Composable
fun GeofenceHistoryScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var locations by remember { mutableStateOf<List<Location>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var statusMessage by remember { mutableStateOf("") }

    val db = remember { AppDb.getInstance(context) }
    val geoManager = remember { GeofenceManagerClaude(context) }
    val locationRepo = remember { LocationRepository(db.locationDao()) }

    // Load locations on screen open
    LaunchedEffect(Unit) {
        isLoading = true
        locations = locationRepo.getActiveLocations()
        isLoading = false
    }

    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("Saved Locations") },
//                colors = TopAppBarDefaults.topAppBarColors(
//                    containerColor = MaterialTheme.colorScheme.primaryContainer
//                )
//            )
//        }
    ) { padding ->
        Text("Saved Locations")
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Debug: Clear all geofences button
            OutlinedButton(
                onClick = {
                    statusMessage = "Clearing all geofences..."
                    val geoManager = GeofenceManagerClaude(context)
                    geoManager.clearAllGeofences { ok, error ->
                        statusMessage = if (ok) {
                            "✓ All geofences cleared!"
                        } else {
                            "Failed: ${error?.message}"
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("🧹 Clear All Geofences (Debug)")
            }

            if (statusMessage.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = when {
                            statusMessage.startsWith("✓") -> Color(0xFF4CAF50).copy(alpha = 0.1f)
                            else -> MaterialTheme.colorScheme.errorContainer
                        }
                    )
                ) {
                    Text(
                        text = statusMessage,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Location list
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                locations.isEmpty() -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "No saved locations",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "Add a location to start tracking",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                else -> {
                    Text(
                        "Saved Locations (${locations.size})",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(Modifier.height(8.dp))

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(locations) { location ->
                            LocationCard(
                                location = location,
                                onDelete = {
                                    scope.launch {
                                        geoManager.removeById(location.id) { ok, _ ->
                                            if (ok) {
                                                scope.launch {
                                                    locationRepo.deleteLocation(location.id)
                                                    locations = locationRepo.getActiveLocations()
                                                    statusMessage = "Deleted ${location.name}"
                                                }
                                            }
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = onNavigateBack,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Go Back")
            }
        }
    }
}

/*  TODO: onclick of any of these location cards,
    navigate to a page to see more stats like duration
    per week, duration this week and so on
    TODO: Fix non scrollable "go back" section
*/

@Composable
fun LocationCard(
    location: Location,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = location.name,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "ID: ${location.id}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(onClick = onDelete) {
                    Text("🗑️")
                }
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = "📍 ${location.latitude}, ${location.longitude}",
                style = MaterialTheme.typography.bodySmall
            )

            if (location.address != null) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = location.address,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(4.dp))
            Text(
                text = "Radius: ${location.radiusMeters}m",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}