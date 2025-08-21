package com.example.autogeotimer

import android.os.Bundle

import android.content.Context

import android.location.Address
import android.location.Geocoder

import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.TextField
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.platform.LocalContext

import androidx.activity.enableEdgeToEdge
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview

import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
//import com.example.autogeotimer.ui.theme.AutoGeoTimerTheme

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import java.io.IOException


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                MyAppNavigation()
            }
        }
    }
}


@Composable
fun InputAddress(
    modifier: Modifier = Modifier,
    navController: NavController
) {

    var postcodeState by remember { mutableStateOf("") }

    Column (
        modifier = modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp, top = 56.dp, bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text("Enter your Postcode")
        TextField(
            value = postcodeState,
            onValueChange = { postcodeState = it },
            label = { Text("Postcode") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 16.dp),
            singleLine = true
        )
        Button(
            onClick = {
                navController.navigate("${AppDestinations.SHOW_COORDINATES_SCREEN}/$postcodeState")
            },
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text("Submit")
        }
    }
}

data class Coordinates(val latitude: Double, val longitude: Double)
data class Location(val address: String)
sealed class GeocodingResult {
    data class Success(val coordinates: Coordinates) : GeocodingResult()
    data class Error(val message: String) : GeocodingResult()
    object NotFound : GeocodingResult()
}

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
        // geocodingApiResult = convertAddressToCoordinatesDetailed(context, postcode) // Your actual geocoding function
        // --- Placeholder for your geocoding function call ---
        kotlinx.coroutines.delay(1000) // Simulate network delay
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
        Spacer(modifier = Modifier.height(16.dp))
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

object AppDestinations {
    const val INPUT_ADDRESS_SCREEN = "InputAddress"
    const val SHOW_COORDINATES_SCREEN = "ShowCoordinatesScreen"
    // Add more routes for other features
}

@Preview
@Composable
fun MyAppNavigation() { // Renamed for clarity, could be in your main screen Composable
    val navController = rememberNavController()
    // val sharedViewModel: SharedViewModel = viewModel() // Or however you get your ViewModel

    // Use the NavHost from androidx.navigation.compose.NavHost
    NavHost(navController = navController, startDestination = AppDestinations.INPUT_ADDRESS_SCREEN) {
        // 'this' inside this lambda is a NavGraphBuilder

        // Pass the navController directly as InputAddress expects
        composable(AppDestinations.INPUT_ADDRESS_SCREEN) { backStackEntry ->
            InputAddress(navController = navController)
        }

        composable(
            route = "${AppDestinations.SHOW_COORDINATES_SCREEN}/{postcodeState}", // Define argument
            arguments = listOf(
                navArgument("postcodeState") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val recievedPostcode = backStackEntry.arguments?.getString("postcodeState") ?: "LU4 0JB"
            // val sharedViewModel = // ... get sharedViewModel, possibly scoped
            // Ensure postcode is not null, though navArgument nullable = false helps
            ShowCoordinatesScreen(
                postcode = recievedPostcode,
                onNavigateBack = { navController.popBackStack() }
                // sharedViewModel = sharedViewModel
            )
        }
        // Add other composable destinations here
    }
}
