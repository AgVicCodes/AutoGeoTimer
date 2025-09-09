package com.example.autogeotimer.ui

import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Arrangement

import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.CircularProgressIndicator

import com.example.autogeotimer.Location
import com.example.autogeotimer.domain.Coordinates
import com.example.autogeotimer.domain.GeocodingResult
import com.example.autogeotimer.convertPostcodeToAddress
import com.example.autogeotimer.convertAddressToCoordinates

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ShowCoordinatesScreen(
    postcode: String,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    var locationResult by remember { mutableStateOf<Coordinates?>(null) }
    var addressString by remember { mutableStateOf<Location?>(null) }
    var geocodingApiResult by remember { mutableStateOf<GeocodingResult?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()

    // Perform geocoding when the screen is first composed with a postcode
    LaunchedEffect(key1 = postcode) { // Re-launch if postcode changes (though it won't in this nav setup)

        isLoading = true

        coroutineScope.launch {
            locationResult = convertAddressToCoordinates(context, postcode)
            addressString = convertPostcodeToAddress(context, postcode)
            isLoading = false
        }

        // geocodingApiResult = convertAddressToCoordinatesDetailed(context, postcode) // Your actual geocoding function
        // --- Placeholder for your geocoding function call ---
        delay(1000) // Simulate network delay
        if (postcode.equals("error", ignoreCase = true)) {
            geocodingApiResult = GeocodingResult.Error("Simulated geocoding error for '$postcode'")
        } else if (postcode.equals("Not found", ignoreCase = true)) {
            geocodingApiResult = GeocodingResult.NotFound
        } else {
            geocodingApiResult =
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
                    locationResult = convertAddressToCoordinates(context, postcode)
                    addressString = convertPostcodeToAddress(context, postcode)
                    isLoading = false
                }
            },
            Modifier.padding(0.dp, 8.dp)
        ) {
            Text("Convert Address")
        }

        if (isLoading) {
            CircularProgressIndicator(color = Color(0xFF6F009D))
        } else {
            when (val result = geocodingApiResult) {
                is GeocodingResult.Success -> {
                    Text("Latitude: ${result.coordinates.latitude}")
                    Text("Longitude: ${result.coordinates.longitude}")
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
        Button(onClick = onNavigateBack) {
            Text("Go Back")
        }

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
    }
}