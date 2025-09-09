package com.example.autogeotimer.ui


import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview

import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope

import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator

import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import com.example.autogeotimer.domain.Coordinates
import androidx.compose.material3.SnackbarHostState
import com.example.autogeotimer.data.location.LocationProvider
import kotlinx.coroutines.launch


@Preview
@Composable
fun HomeScreen(modifier: Modifier = Modifier) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbar = remember { SnackbarHostState() }

    var coords by remember { mutableStateOf<Coordinates?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) {
        granted ->
        if (granted) {
            scope.launch {
                isLoading = true
                val result = LocationProvider(context).getCurrentLocation()
                result.
                    onSuccess { c -> coords = c }.
                    onFailure { e -> scope.launch { snackbar.showSnackbar(e.message ?: "Location error") } }
                isLoading = false
            }
        } else {
            scope.launch { snackbar.showSnackbar("Location permission denied.") }
        }
    }

    Scaffold (snackbarHost = { SnackbarHost(snackbar) }) { padding ->
        Column (
            modifier = modifier.
                fillMaxSize().
                padding(padding).
                padding(16.dp)
        ) {
            Text("Welcome to My Geocoding App")
            Spacer(Modifier.height(12.dp))
            Button(onClick = { launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION) }) {
                when {
                    isLoading -> Text("Retrieving Location...")
                    else -> Text("Use my current location")
                }
            }
            Spacer(Modifier.height(12.dp))
            when {
                isLoading -> CircularProgressIndicator()
                coords != null -> Text("Your current location is: \nlatitude:${coords!!.latitude} \nlongitude:${coords!!.longitude}")
                else -> Text("Tap the button to fetch your location")
            }
        }
    }

//    Column (
//        Modifier.
//            fillMaxWidth(1f).
//            fillMaxHeight(1f).
//            padding(16.dp, 32.dp)
//    ) {
//        Text("Welcome to My Geocoding App")
//        Text("Your current location is: ")
//    }
}
