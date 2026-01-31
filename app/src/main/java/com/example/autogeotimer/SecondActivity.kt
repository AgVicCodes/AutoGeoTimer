//import com.example.autogeotimer.ui.HomeScreen
//import com.example.autogeotimer.ui.InputAddressPage
//import com.example.autogeotimer.ui.InputChoice
//import com.example.autogeotimer.ui.ShowCoordinatesScreen
//import com.example.autogeotimer.ui.TestScreen
//
//AppNavigation:
//
//package com.example.autogeotimer.ui
//
//
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.tooling.preview.Preview
//
//import androidx.navigation.NavType
//import androidx.navigation.navArgument
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import androidx.navigation.compose.rememberNavController
//
//import com.example.autogeotimer.AppDestinations
//package com.example.autogeotimer.ui
////
////
////import android.Manifest
////import androidx.compose.ui.unit.dp
////import androidx.compose.ui.Modifier
////import androidx.compose.ui.platform.LocalContext
////import androidx.activity.result.contract.ActivityResultContracts
////import androidx.activity.compose.rememberLauncherForActivityResult
////
////import androidx.compose.runtime.remember
////import androidx.compose.runtime.setValue
////import androidx.compose.runtime.getValue
////import androidx.compose.runtime.Composable
////import androidx.compose.runtime.rememberCoroutineScope
////
////import androidx.compose.material3.Button
////import androidx.compose.material3.Scaffold
////import androidx.compose.material3.SnackbarHost
////import androidx.compose.foundation.layout.Column
////import androidx.compose.foundation.layout.Spacer
////import androidx.compose.foundation.layout.height
////import androidx.compose.foundation.layout.padding
////import androidx.compose.foundation.layout.fillMaxSize
////import androidx.compose.material3.CircularProgressIndicator
////
////import kotlinx.coroutines.launch
////import androidx.compose.material3.Text
////import androidx.compose.runtime.mutableStateOf
////import com.example.autogeotimer.domain.Coordinates
////import androidx.compose.material3.SnackbarHostState
////import com.example.autogeotimer.data.location.LocationProvider
//
//
////package com.example.autogeotimer.ui
////
////import androidx.compose.foundation.layout.*
////import androidx.compose.material3.*
////import androidx.compose.runtime.*
////import androidx.compose.ui.Alignment
////import androidx.compose.ui.Modifier
////import androidx.compose.ui.graphics.Color
////import androidx.compose.ui.text.font.FontWeight
////import androidx.compose.ui.tooling.preview.Preview
////import androidx.compose.ui.unit.dp
////import androidx.compose.ui.unit.sp
////import androidx.lifecycle.viewmodel.compose.viewModel
////import kotlinx.coroutines.delay
////import kotlin.time.Duration.Companion.seconds
//package com.example.autogeotimer.ui
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.ViewModelProvider
//import androidx.lifecycle.viewModelScope
//
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.flow.MutableStateFlow
//
//import com.example.autogeotimer.data.repo.SessionRepo
//import kotlinx.coroutines.flow.update

//@Preview
//@Composable
//fun MyAppNavigation() { // Renamed for clarity, could be in your main screen Composable
//    val navController = rememberNavController()
//    // val sharedViewModel: SharedViewModel = viewModel() // Or however you get your ViewModel
//
////    // Use the NavHost from androidx.navigation.compose.NavHost
////    NavHost(navController = navController, startDestination = AppDestinations.INPUT_ADDRESS_SCREEN) {
////        // 'this' inside this lambda is a NavGraphBuilder
////
////        // Pass the navController directly as InputAddress expects
////        composable(AppDestinations.INPUT_ADDRESS_SCREEN) { backStackEntry ->
////            InputAddressPage(navController = navController)
////        }
////
//    // Use the NavHost from androidx.navigation.compose.NavHost
//    NavHost(navController = navController, startDestination = AppDestinations.HOME_SCREEN) {
//        // 'this' inside this lambda is a NavGraphBuilder
//
//        // Pass the navController directly as InputAddress expects
//        composable(AppDestinations.HOME_SCREEN) { backStackEntry ->
//            HomeScreen(navController = navController)
//        }
//
//        composable(AppDestinations.INPUT_CHOICE_SCREEN) { backStackEntry ->
//            InputChoice(navController = navController)
//        }
//
//        // Pass the navController directly as InputAddress expects
//        composable(AppDestinations.INPUT_ADDRESS_SCREEN) { backStackEntry ->
//            InputAddressPage(
//                navController = navController,
//                onNavigateBack = { navController.popBackStack() }
//            )
//        }
//
//        composable(
//            route = "${AppDestinations.SHOW_COORDINATES_SCREEN}/{postcodeState}", // Define argument
//            arguments = listOf(
//                navArgument("postcodeState") {
//                    type = NavType.StringType
//                }
//            )
//        ) { backStackEntry ->
//            val receivedPostcode = backStackEntry.arguments?.getString("postcodeState") ?: "LU4 0JB"
//            // val sharedViewModel = // ... get sharedViewModel, possibly scoped
//            // Ensure postcode is not null, though navArgument nullable = false helps
//            ShowCoordinatesScreen(
//                postcode = receivedPostcode,
//                onNavigateBack = { navController.popBackStack() }
//                // sharedViewModel = sharedViewModel
//            )
//        }
//
//        // Add other composable destinations here
//        composable(AppDestinations.USE_LOCATION_SCREEN) { backStackEntry ->
//            TestScreen(onBackNavigation = { navController.popBackStack() })
//        }
//    }
//}
//
//ConfirmLocationScreen:
//
//
//
////@Preview
//@Composable
//fun TestScreen(
//    modifier: Modifier = Modifier,
//    onBackNavigation: () -> Unit
//) {
//
//    val context = LocalContext.current
//    val scope = rememberCoroutineScope()
//    val snackbar = remember { SnackbarHostState() }
//
//    var coords by remember { mutableStateOf<Coordinates?>(null) }
//    var isLoading by remember { mutableStateOf(false) }
//
//    val launcher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.RequestPermission()
//    ) {
//            granted ->
//        if (granted) {
//            scope.launch {
//                isLoading = true
//                val result = LocationProvider(context).getCurrentLocation()
//                result.
//                onSuccess { c -> coords = c }.
//                onFailure { e -> scope.launch { snackbar.showSnackbar(e.message ?: "Location error") } }
//                isLoading = false
//            }
//        } else {
//            scope.launch { snackbar.showSnackbar("Location permission denied.") }
//        }
//    }
//
//    Scaffold (snackbarHost = { SnackbarHost(snackbar) }) { padding ->
//        Column (
//            modifier = modifier
//                .fillMaxSize()
//                .padding(padding)
//                .padding(16.dp)
//        ) {
//            Text("Welcome to My Geocoding App")
//            Spacer(Modifier.height(12.dp))
//            Button(onClick = { launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION) }) {
//                when {
//                    isLoading -> Text("Retrieving Location...")
//                    else -> Text("Use my current location")
//                }
//            }
//            Spacer(Modifier.height(12.dp))
//            when {
//                isLoading -> CircularProgressIndicator()
//                coords != null -> Text("Your current location is: \nlatitude:${coords!!.latitude} \nlongitude:${coords!!.longitude}")
//                else -> Text("Tap the button to fetch your location")
//            }
//            Spacer(Modifier.height(12.dp))
//            Button(onClick = onBackNavigation ) {
//                Text("Go Back")
//            }
//
//        }
//
//    }
//}
//
//CoordinatesScreen:
//package com.example.autogeotimer.ui
//
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.platform.LocalContext
//
//import androidx.compose.runtime.setValue
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.rememberCoroutineScope
//
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.Arrangement
//
//import androidx.compose.material3.Text
//import androidx.compose.material3.Button
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.CircularProgressIndicator
//
//import com.example.autogeotimer.Location
//import com.example.autogeotimer.domain.Coordinates
//import com.example.autogeotimer.domain.GeocodingResult
//import com.example.autogeotimer.convertPostcodeToAddress
//import com.example.autogeotimer.convertAddressToCoordinates
//
//import kotlinx.coroutines.delay
//import kotlinx.coroutines.launch
//
//@Composable
//fun ShowCoordinatesScreen(
//    postcode: String,
//    onNavigateBack: () -> Unit
//) {
//    val context = LocalContext.current
//    var locationResult by remember { mutableStateOf<Coordinates?>(null) }
//    var addressString by remember { mutableStateOf<Location?>(null) }
//    var geocodingApiResult by remember { mutableStateOf<GeocodingResult?>(null) }
//    var isLoading by remember { mutableStateOf(true) }
//    val coroutineScope = rememberCoroutineScope()
//
//    // Perform geocoding when the screen is first composed with a postcode
//    LaunchedEffect(key1 = postcode) { // Re-launch if postcode changes (though it won't in this nav setup)
//
//        isLoading = true
//
//        coroutineScope.launch {
//            locationResult = convertAddressToCoordinates(context, postcode)
//            addressString = convertPostcodeToAddress(context, postcode)
//            isLoading = false
//        }
//
//        // geocodingApiResult = convertAddressToCoordinatesDetailed(context, postcode) // Your actual geocoding function
//        // --- Placeholder for your geocoding function call ---
//        delay(1000) // Simulate network delay
//        if (postcode.equals("error", ignoreCase = true)) {
//            geocodingApiResult = GeocodingResult.Error("Simulated geocoding error for '$postcode'")
//        } else if (postcode.equals("Not found", ignoreCase = true)) {
//            geocodingApiResult = GeocodingResult.NotFound
//        } else {
//            geocodingApiResult =
//                GeocodingResult.Success(Coordinates(51.5074, 0.1278)) // Dummy success
//        }
//        // --- End Placeholder ---
//        isLoading = false
//    }
//
//    Column (
//        Modifier
//            .fillMaxSize()
//            .padding(16.dp, 48.dp),
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Text("Showing Coordinates for: $postcode", style = MaterialTheme.typography.headlineSmall)
//        Spacer(modifier = Modifier.height(8.dp))
//        Button (
//            onClick = {
//                isLoading = true
//                coroutineScope.launch {
//                    locationResult = convertAddressToCoordinates(context, postcode)
//                    addressString = convertPostcodeToAddress(context, postcode)
//                    isLoading = false
//                }
//            },
//            Modifier.padding(0.dp, 8.dp)
//        ) {
//            Text("Convert Address")
//        }
//
//        if (isLoading) {
//            CircularProgressIndicator(color = Color(0xFF6F009D))
//        } else {
//            when (val result = geocodingApiResult) {
//                is GeocodingResult.Success -> {
//                    Text("Latitude: ${result.coordinates.latitude}")
//                    Text("Longitude: ${result.coordinates.longitude}")
//                }
//                is GeocodingResult.Error -> {
//                    Text("Error: ${result.message}", color = MaterialTheme.colorScheme.error)
//                }
//                is GeocodingResult.NotFound -> {
//                    Text("Address not found by geocoder.", color = MaterialTheme.colorScheme.error)
//                }
//                null -> {
//                    Text("Geocoding result not yet available.") // Should not happen if isLoading is handled
//                }
//            }
//        }
//        Spacer(modifier = Modifier.height(32.dp))
//        Button(onClick = onNavigateBack) {
//            Text("Go Back")
//        }
//
//        if (isLoading) {
//            Text("Loading...")
//        } else {
//            locationResult?.let {
//                Text("Main Logic:")
//                Text("Latitude: ${it.latitude}")
//                Text("Longitude: ${it.longitude}")
//            } ?: Text("Coordinates couldn't be obtained")
//
//            addressString?.let {
//                Text("\n")
//                Text("Address: ${it.address}")
//            } ?: Text("Address not found")
//        }
//    }
//}
//
//CurrentLocationScreen:
//
//package com.example.autogeotimer.ui
//
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.Arrangement
//
//import androidx.compose.material3.Text
//import androidx.compose.material3.Button
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.CircularProgressIndicator
//
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.setValue
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.rememberCoroutineScope
//
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.platform.LocalContext
//
//import com.example.autogeotimer.Location
//import com.example.autogeotimer.domain.Coordinates
//import com.example.autogeotimer.domain.GeocodingResult
//import com.example.autogeotimer.convertPostcodeToAddress
//import com.example.autogeotimer.convertAddressToCoordinates
//
//import kotlinx.coroutines.delay
//import kotlinx.coroutines.launch
//
//@Composable
//fun CurrentLocationScreen(
//    postcode: String,
//    onNavigateBack: () -> Unit
//) {
//    val context = LocalContext.current
//    var locationResult by remember { mutableStateOf<Coordinates?>(null) }
//    var addressString by remember { mutableStateOf<Location?>(null) }
//    var geocodingApiResult by remember { mutableStateOf<GeocodingResult?>(null) }
//    var isLoading by remember { mutableStateOf(true) }
//    val coroutineScope = rememberCoroutineScope()
//
//    // Perform geocoding when the screen is first composed with a postcode
//    LaunchedEffect(key1 = postcode) { // Re-launch if postcode changes (though it won't in this nav setup)
//        isLoading = true
//        // geocodingApiResult = convertAddressToCoordinatesDetailed(context, postcode) // Your actual geocoding function
//        // --- Placeholder for your geocoding function call ---
//        delay(1000) // Simulate network delay
//        if (postcode.equals("error", ignoreCase = true)) {
//            geocodingApiResult = GeocodingResult.Error("Simulated geocoding error for '$postcode'")
//        } else if (postcode.equals("Not found", ignoreCase = true)) {
//            geocodingApiResult = GeocodingResult.NotFound
//        } else {
//            geocodingApiResult =
//                GeocodingResult.Success(Coordinates(51.5074, 0.1278)) // Dummy success
//        }
//        // --- End Placeholder ---
//        isLoading = false
//    }
//
//    Column (
//        Modifier
//            .fillMaxSize()
//            .padding(16.dp, 48.dp),
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Text("Showing Coordinates for: $postcode", style = MaterialTheme.typography.headlineSmall)
//        Spacer(modifier = Modifier.height(16.dp))
//        Button (
//            onClick = {
//                isLoading = true
//                coroutineScope.launch {
//                    locationResult = convertAddressToCoordinates(context, postcode)
//                    addressString = convertPostcodeToAddress(context, postcode)
//                    isLoading = false
//                }
//            },
//            Modifier.padding(0.dp, 8.dp)
//        ) {
//            Text("Convert Address")
//        }
//
//        if (isLoading) {
//            CircularProgressIndicator(color = Color(0xFF6F009D))
//        } else {
//            when (val result = geocodingApiResult) {
//                is GeocodingResult.Success -> {
//                    Text("Latitude: ${result.coordinates.latitude}")
//                    Text("Longitude: ${result.coordinates.longitude}")
//                }
//                is GeocodingResult.Error -> {
//                    Text("Error: ${result.message}", color = MaterialTheme.colorScheme.error)
//                }
//                is GeocodingResult.NotFound -> {
//                    Text("Address not found by geocoder.", color = MaterialTheme.colorScheme.error)
//                }
//                null -> {
//                    Text("Geocoding result not yet available.") // Should not happen if isLoading is handled
//                }
//            }
//        }
//        Spacer(modifier = Modifier.height(32.dp))
//        Button(onClick = onNavigateBack) {
//            Text("Go Back")
//        }
//
//        if (isLoading) {
//            Text("Loading...")
//        } else {
//            locationResult?.let {
//                Text("Main Logic:")
//                Text("Latitude: ${it.latitude}")
//                Text("Longitude: ${it.longitude}")
//            } ?: Text("Coordinates couldn't be obtained")
//
//            addressString?.let {
//                Text("\n")
//                Text("Address: ${it.address}")
//            } ?: Text("Address not found")
//        }
//    }
//}
//
//HomeScreen:
//
//package com.example.autogeotimer.ui
//
//
//import androidx.compose.ui.Modifier
//
//import androidx.compose.runtime.Composable
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.fillMaxSize
//
//import androidx.compose.material3.Text
//import androidx.compose.runtime.LaunchedEffect
//
//import androidx.compose.ui.unit.em
//import androidx.compose.ui.unit.sp
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.graphics.Brush
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.font.FontFamily
//import androidx.compose.ui.text.style.TextAlign
//
//import androidx.navigation.NavController
//
//import com.example.autogeotimer.AppDestinations
//
//import kotlinx.coroutines.delay
//
//
//@Composable
//fun HomeScreen(
//    modifier: Modifier = Modifier,
//    navController: NavController,
//) {
//
//    LaunchedEffect(Unit) {
//        delay(5_000)
//        navController.navigate(AppDestinations.INPUT_CHOICE_SCREEN) {
//            popUpTo(AppDestinations.HOME_SCREEN) { inclusive = true }
//            launchSingleTop = true
//        }
//    }
//
//
//    Column(
//        modifier.fillMaxSize().background(
//            brush = Brush.verticalGradient(
//                colors = listOf(Color(0xFF0f0f0f), Color(0xFF3d3d3d))
////                colors = listOf(Color(0xFF171123), Color(0xFF33305E))
//            )
//        ),
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally,
//    ) {
//
//        Text(
//            text = "Welcome to My \nGeocoding App✨",
//            fontSize = 32.sp,
//            lineHeight = 1.5.em,
//            color = Color(0xFFF7F7F7),
//            fontWeight = FontWeight.Bold,
//            textAlign = TextAlign.Center,
//            fontFamily = FontFamily.Cursive
//        )
//    }
//}
//
//InputChoice:
//
//package com.example.autogeotimer.ui
//
//
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.graphics.Color
//
//import androidx.compose.material3.Text
//import androidx.compose.material3.Button
//import androidx.compose.runtime.Composable
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.navigation.NavController
//import com.example.autogeotimer.AppDestinations
//
//
//@Composable
//fun InputChoice(
//    modifier: Modifier = Modifier,
//    navController: NavController
//) {
//
//
//    Column (
//        modifier
//            .fillMaxSize()
//            .fillMaxWidth()
//            .background(color = Color(0xFF0f0f0f)),
////            .background(color = Color(0xFF171123)),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center
//    ) {
//        Text(
//            text = "Select location method",
//            fontSize = (24.sp),
//            color = Color(0xFFFFFFFF)
//        )
//        Spacer( modifier.height(16.dp) )
//        Button(
//            onClick = { navController.navigate(AppDestinations.INPUT_ADDRESS_SCREEN) }) {
//            Text("Postcode")
//        }
//        Spacer( modifier.height(8.dp) )
//        Button(
//            onClick = { navController.navigate(AppDestinations.USE_LOCATION_SCREEN) }) {
//            Text("Use Current Location")
//        }
//    }
//}
//
//InputPage:
//
//package com.example.autogeotimer.ui
//
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.padding
//import androidx.compose.material3.Button
//import androidx.compose.material3.Text
//import androidx.compose.material3.TextField
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import androidx.navigation.NavController
//import com.example.autogeotimer.AppDestinations
//
//
//@Composable
//fun InputAddressPage(
//    modifier: Modifier = Modifier,
//    navController: NavController,
//    onNavigateBack: () -> Unit
//) {
//
//    var postcodeState by remember { mutableStateOf("") }
//
//    Column (
//        modifier = modifier
//            .fillMaxSize()
//            .padding(start = 16.dp, end = 16.dp, top = 56.dp, bottom = 16.dp),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//
//        Text("Enter your Postcode")
//        TextField(
//            value = postcodeState,
//            onValueChange = { postcodeState = it },
//            label = { Text("Postcode") },
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(0.dp, 16.dp),
//            singleLine = true
//        )
//        Button(
//            onClick = {
//                navController.navigate("${AppDestinations.SHOW_COORDINATES_SCREEN}/$postcodeState")
//            },
//            modifier = Modifier
//                .fillMaxWidth()
//        ) {
//            Text("Submit")
//        }
//
//        Button(onClick = onNavigateBack) {
//            Text("Go Back!")
//        }
//    }
//}
//
//Notifier:
//
//package com.example.autogeotimer.util
//
//import android.Manifest
//import android.os.Build
//import android.content.Context
//import com.example.autogeotimer.R
//import android.app.NotificationManager
//import android.app.NotificationChannel
//import android.util.Log
//import androidx.core.app.NotificationCompat
//import androidx.annotation.RequiresPermission
//import androidx.core.app.NotificationManagerCompat
//
//
//object Notifier {
//    private const val CH_ID = "geofence_events"
//
//    fun ensureChannel(context: Context) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val ch = NotificationChannel(CH_ID, "Geofence Events", NotificationManager.IMPORTANCE_DEFAULT)
//            (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
//                .createNotificationChannel(ch)
//        }
//    }
//
//    //    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
//    fun notify(context: Context, title: String, text: String) {
//        ensureChannel(context)
//
//        val n = NotificationCompat.Builder(context, CH_ID)
////            .setSmallIcon(R.drawable.ic_notification) // App icon
//            .setContentTitle(title)
//            .setContentText(text)
//            .setAutoCancel(true)
//            .build()
//
//        try {
//            NotificationManagerCompat.from(context).notify(
//                (System.currentTimeMillis() % Int.MAX_VALUE)
//                    .toInt(), n)
//        } catch (se: SecurityException) {
////            In case OEMs still throw despite the check
//            Log.e("Notifier", "Notify failed: ${se.message}", se)
//        }
//    }
//}
//
//MainActivity:
//
//package com.example.autogeotimer
//
//
//import android.os.Bundle
//
//import android.content.Context
//
//import android.location.Address
//import android.location.Geocoder
//
//import androidx.activity.enableEdgeToEdge
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//
//import androidx.compose.material3.MaterialTheme
//
//import com.example.autogeotimer.domain.Coordinates
//import com.example.autogeotimer.ui.MyAppNavigation
//
//import kotlinx.coroutines.withContext
//import kotlinx.coroutines.Dispatchers
//
//import java.io.IOException
//
//
//class MainActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContent {
//            MaterialTheme {
//                MyAppNavigation()
//            }
//        }
//    }
//}
//
//
//data class Location(val address: String)
//
//
//suspend fun convertAddressToCoordinates (context: Context, address: String): Coordinates? {
//    return withContext (Dispatchers.IO) {
//        try {
//            val geocoder = Geocoder(context)
//            @Suppress("DEPRECATION")
//            val addresses: MutableList<Address>? = geocoder.getFromLocationName(address, 1)
//            addresses?.firstOrNull()?.let {
//                Coordinates(it.latitude, it.longitude)
//            }
//        } catch (e: IOException) {
//            println("Geocoding failed for '$address': ${e.message}")
//            e.printStackTrace()
//            null
//        }
//    }
//}
//
//suspend fun convertPostcodeToAddress (context: Context, postcode: String): Location? {
//    return withContext (Dispatchers.IO) {
//        try {
//            val geocoder = Geocoder(context)
//            @Suppress("DEPRECATION")
//            val addresses: MutableList<Address>? = geocoder.getFromLocationName(postcode, 1)
//            addresses?.firstOrNull()?.let { androidAddress ->
//                val fullAddress = (0..androidAddress.maxAddressLineIndex)
//                    .mapNotNull { i -> androidAddress.getAddressLine(i) }
//                    .joinToString(separator = "\n")
//
//                if (fullAddress.isNotBlank()) {
//                    Location(fullAddress)
//                } else {
//                    null
//                }
//
//            }
//        } catch (e: IOException) {
//            println("Geocoding failed for '$postcode': ${e.message}")
//            e.printStackTrace()
//            null
//        }
//    }
//}
//
//
//object AppDestinations {
//    const val HOME_SCREEN = "Home"
//    const val USE_LOCATION_SCREEN = "UseLocation"
//    const val INPUT_CHOICE_SCREEN = "InputChoice"
//    const val INPUT_ADDRESS_SCREEN = "InputAddress"
//    const val SHOW_COORDINATES_SCREEN = "ShowCoordinates"
//    // Add more routes for other features
//}
//
//GeofenceManager:
//
//package com.example.autogeotimer.data.geofencing
//
//import android.annotation.SuppressLint
//import android.content.Intent
//import android.content.Context
//
//import android.app.PendingIntent
//
//import com.google.android.gms.location.Geofence
//import com.google.android.gms.location.LocationServices
//import com.google.android.gms.location.GeofencingRequest
//
//import com.example.autogeotimer.receiver.GeofenceReceiver
//
//
//class GeoFenceManager(private val context: Context) {
//
//    private val client = LocationServices.getGeofencingClient(context)
//
//    private fun pendingIntent(): PendingIntent =
//        PendingIntent.getBroadcast(
//            context, 0,
//            Intent(context, GeofenceReceiver::class.java),
//            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//        )
//
//    fun buildRequest(id: String, lat: Double, lng: Double, radiusM: Float): GeofencingRequest {
//        val fence = Geofence.Builder()
//            .setRequestId(id)
//            .setCircularRegion(lat, lng, radiusM)
//            .setTransitionTypes(
//                Geofence.GEOFENCE_TRANSITION_ENTER or
//                        Geofence.GEOFENCE_TRANSITION_EXIT or
//                        Geofence.GEOFENCE_TRANSITION_DWELL
//            )
//            .setLoiteringDelay(2 * 60_000) // Dwell for at least 2 Minutes to trigger
//            .setExpirationDuration(Geofence.NEVER_EXPIRE)
//            .build()
//
//        return GeofencingRequest.Builder()
//            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
//            .addGeofence(fence)
//            .build()
//    }
//
//    @SuppressLint("MissingPermission") // Must check permissions before calling
//    fun add(
//        id: String,
//        lat: Double,
//        lng: Double,
//        radiusM: Float = 150f,
//        onResult: (ok: Boolean, error: Exception?) -> Unit
//    ) {
//        val request = buildRequest(id, lat, lng, radiusM)
//        client.addGeofences(request, pendingIntent())
//            .addOnSuccessListener { onResult(true, null) }
//            .addOnFailureListener { e -> onResult(false, e) }
//    }
//
//    fun removeById(
//        id: String,
//        onResult: (ok: Boolean, error: Exception?) -> Unit
//    ) {
//        client.removeGeofences(listOf(id))
//            .addOnSuccessListener { onResult(true, null) }
//            .addOnFailureListener { e -> onResult(false, e) }
//    }
//
//    fun removeAll(onResult: (ok: Boolean, error: Exception?) -> Unit) {
//        client.removeGeofences(pendingIntent())
//            .addOnSuccessListener { onResult(true, null) }
//            .addOnFailureListener { e -> onResult(false, e) }
//    }
//}
//
//LocationProvider:
//
//package com.example.autogeotimer.data.location
//
//import android.content.Context
//
//import com.example.autogeotimer.domain.Coordinates
//
//import com.google.android.gms.location.Priority
//import com.google.android.gms.location.LocationServices
//import com.google.android.gms.tasks.CancellationTokenSource
//
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.tasks.await
//import kotlinx.coroutines.withContext
//
//
////class LocationProvider(private val context: Context) {
//class LocationProvider(context: Context) {
//
//    private val fused = LocationServices.getFusedLocationProviderClient(context)
//
//    suspend fun getCurrentLocation(): Result<Coordinates> = withContext(Dispatchers.Main) {
//        try {
//            val priority = Priority.PRIORITY_HIGH_ACCURACY
//            val location = fused.getCurrentLocation(priority, CancellationTokenSource().token).await()
//            if (location != null) Result.success(Coordinates(location.latitude, location.longitude))
//            else Result.failure(IllegalStateException("No location fix. Ensure GPS is on."))
//        }
//
//        catch (e: SecurityException) {
//            Result.failure(IllegalStateException("Location permission denied.", e))
//        }
//
//        catch (e: Exception) {
//            Result.failure(IllegalStateException("Failed to get location: ${e.message}", e))
//        }
//    }
//
//}
//
//Coordinates:
//
//package com.example.autogeotimer.domain
//
//
//data class Coordinates(val latitude: Double, val longitude: Double)
//
//GeocodingResult:
//
//package com.example.autogeotimer.domain
//
//
//sealed class GeocodingResult {
//    data class Success(val coordinates: Coordinates) : GeocodingResult()
//    data class Error(val message: String) : GeocodingResult()
//    object NotFound : GeocodingResult()
//}
//
//TrackingState:
//
//package com.example.autogeotimer.domain
//
//// domain/TrackingState.kt
//sealed interface TrackingState {
//    object Idle : TrackingState
//    object Locating : TrackingState
//    object RequestingPermission : TrackingState
//    data class Error(val msg: String) : TrackingState
//    data class Ready(val coords: Coordinates) : TrackingState
//}
//
//GeofenceReciever:
//
//package com.example.autogeotimer.receiver
//
//import android.util.Log
//import android.content.Intent
//import android.content.Context
//import android.content.BroadcastReceiver
//import com.example.autogeotimer.util.Notifier
//import com.google.android.gms.location.Geofence
//import com.google.android.gms.location.GeofencingEvent
//
//
//class GeofenceReceiver : BroadcastReceiver() {
//    override fun onReceive(context: Context, intent: Intent) {
//        val event = GeofencingEvent.fromIntent(intent) ?: return
//        if (event.hasError()) {
//            Log.e("GeofenceReceiver", "Error: ${event.errorCode}")
//            return
//        }
//        val transition = event.geofenceTransition
//        val ids = event.triggeringGeofences?.map { it.requestId }.orEmpty()
//
//        when (transition) {
//            Geofence.GEOFENCE_TRANSITION_ENTER -> {
//                Log.i("GeofenceReceiver", "ENTER: $ids")
//                Notifier.notify(context, "Entered geofence", "IDs: $ids")
////                TODO: open session (Later persist to room)
//            }
//            Geofence.GEOFENCE_TRANSITION_DWELL -> {
//                Log.i("GeofenceReceiver", "DWELL: $ids")
//                Notifier.notify(context, "Dwelling in geofence", "IDs: $ids")
//            }
//            Geofence.GEOFENCE_TRANSITION_EXIT  -> {
//                Log.i("GeofenceReceiver", "EXIT: $ids")
//                Notifier.notify(context, "Exited geofence", "IDs: $ids")
////                TODO: open session (Later persist to room)
//            }
//            else -> Log.w("GeofenceReceiver", "Unknown transition: $transition")
//        }
//    }
//}

//
////@Preview(showBackground = true)
//
//data class TimerUi(
//    val startMs: Long? = null,      // Initially null until Dwell confirmed
//    val exitMs: Long? = null,
//    val isRunning: Boolean = true,
//    val elapsed: Long = 0L,         // UI counter
//    val isDwelling: Boolean = false // sets to true once startMs is set
//)
//
//@Preview
//@Composable
//fun TimerScreen (
//    ui: TimerUi,
//    onToggleRunning: () -> Unit,
//    modifier: Modifier = Modifier
//) {
//
//    val ui by vm.state.collectAsState()
//
//    var isRunning by remember { mutableStateOf(true) }
//    var isDwelling by remember { mutableStateOf(false) }
//    var elapsedSeconds by remember { mutableLongStateOf(0L) }
//    var status by remember { mutableStateOf("Waiting for confirmation...") }
//
//    LaunchedEffect(sessionId) { vm.load(sessionId) }
//
//    // Timer effect
//    LaunchedEffect(isRunning) {
//        while (isRunning) {
//            delay(1.seconds)
//            elapsedSeconds++
//
//            // Auto-confirm dwelling after 5 minutes
//            if (elapsedSeconds >= 300 && !isDwelling) {
//                isDwelling = true
//                status = "✓ Stay confirmed - Timer active"
//            }
//        }
//    }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(24.dp),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center
//    ) {
////        Text(
////            text = placeName,
////            style = MaterialTheme.typography.headlineMedium,
////            fontWeight = FontWeight.Bold
////        )
//
//        Spacer(Modifier.height(8.dp))
//
//        Text(
//            text = status,
//            style = MaterialTheme.typography.bodyLarge,
//            color = if (isDwelling) Color.Green else Color.Gray
//        )
//
//        Spacer(Modifier.height(32.dp))
//
//        // Timer display
//        Text(
//            text = formatTime(elapsedSeconds),
//            fontSize = 64.sp,
//            fontWeight = FontWeight.Bold,
//            color = if (isDwelling) MaterialTheme.colorScheme.primary else Color.Gray
//        )
//
//        Spacer(Modifier.height(16.dp))
//
//        // Progress indicator for first 5 minutes
//        if (!isDwelling) {
//            LinearProgressIndicator(
//                progress = (elapsedSeconds / 300f).coerceIn(0f, 1f),
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(8.dp)
//            )
//            Text(
//                text = "${300 - elapsedSeconds}s until confirmed",
//                style = MaterialTheme.typography.bodySmall,
//                color = Color.Gray,
//                modifier = Modifier.padding(top = 4.dp)
//            )
//        }
//
//        Spacer(Modifier.height(48.dp))
//
//        // Controls
//        Row(
//            horizontalArrangement = Arrangement.spacedBy(16.dp)
//        ) {
//            Button(
//                onClick = { isRunning = !isRunning },
//                colors = ButtonDefaults.buttonColors(
//                    containerColor = if (isRunning) Color.Red else Color.Green
//                )
//            ) {
//                Text(if (isRunning) "Pause" else "Resume")
//            }
//
////            OutlinedButton(onClick = onNavigateBack) {
////                Text("End Session")
////            }
//        }
//
//        Spacer(Modifier.height(32.dp))
//
//        // Debug info
//        Card(
//            modifier = Modifier.fillMaxWidth(),
//            colors = CardDefaults.cardColors(
//                containerColor = MaterialTheme.colorScheme.surfaceVariant
//            )
//        ) {
//            Column(Modifier.padding(16.dp)) {
////                Text("Place ID: $placeId", style = MaterialTheme.typography.bodySmall)
//                Text("Status: ${if (isRunning) "Active" else "Paused"}",
//                    style = MaterialTheme.typography.bodySmall)
//                Text("Dwelling: ${if (isDwelling) "Yes" else "No (${elapsedSeconds}/300s)"}",
//                    style = MaterialTheme.typography.bodySmall)
//            }
//        }
//    }
//}
//
//private fun formatTime(seconds: Long): String {
//    val hours = seconds / 3600
//    val minutes = (seconds % 3600) / 60
//    val secs = seconds % 60
//    return "%02d:%02d:%02d".format(hours, minutes, secs)
//}

//private fun formatTime(s: Long): String {
//    val h = s / 3600; val m = (s % 3600) / 60; val sec = s % 60
//    return "%02d:%02d:%02d".format(h, m, sec)
//}

//
//
//class TimerViewModel() : ViewModel() {
//    data class Ui(val start: Long? = null, val exit: Long? = null, val elapsedMs: Long = 0) {
//        val formattedElapsed: String
//            get() {
//                val total = if (exit != null && start != null) exit - start else elapsedMs
//                val s = total / 1000
//                return "%02d:%02d:%02d".format(s / 3600, (s % 3600) / 60, s % 60)
//            }
//    }
//
//    private val _state = MutableStateFlow(Ui())
//    val state = _state.asStateFlow()
//
//    private lateinit var repo: SessionRepo
//    fun setRepo(r: SessionRepo) { repo = r }
//
//    // (For production, provide the SessionRepo via DI/Hilt or a ViewModelProvider.Factory
//    // and in load(sessionId) read the session from DB, then call applySession(start, exit).)
//
//    fun tick() {
//        val start = _state.value.start ?: return
//        _state.value = _state.value.copy(elapsedMs = System.currentTimeMillis() - start)
//    }
//
//    fun load(id: Long) = viewModelScope.launch(Dispatchers.IO) {
//        val s = repo.get(id) ?: return@launch
//        _state.update { it.copy(start = s.start, exit = s.exit) }
//        // viewModelScope.launch(Dispatchers.IO) {
//        //     val ctx = coroutineContext
//        // }
//    }
//
//    fun applySession(start: Long?, exit: Long?) {
//        _state.value = _state.value.copy(start = start, exit = exit)
//    }
//}
//
////class TimerVmFactory(private val repo: SessionRepo) : ViewModelProvider.Factory {
////    override fun <T : ViewModel> create(c: Class<T>): T = TimerViewModel(repo) as T
////}
