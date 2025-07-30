package com.example.autogeotimer

//import android.util.Log

import android.os.Bundle
//import android.os.Build

import android.content.Context

import android.location.Address
import android.location.Geocoder

import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

import androidx.compose.ui.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.material3.*
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ButtonColors
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview

import androidx.activity.enableEdgeToEdge
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

//import com.example.autogeotimer.ui.theme.AutoGeoTimerTheme
import java.io.IOException

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyScreen()
//            AutoGeoTimerTheme {
//                PostCodeInput {
//                    entered -> Log.d("MainActivity", "Postcode: $entered")
//                }
//            }
        }
    }
}

@Preview(showBackground = true, name = "Greeting Preview")
@Composable
fun PostCodeInput(
    modifier: Modifier = Modifier,
    label: String = "Enter Postcode",
    onPostcodeEntered: (String) -> Unit = {}
)  {
    var postcode by remember { mutableStateOf("") }

    Column (
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp, 100.dp)
    ) {
        TextField(
            value = postcode,
            onValueChange = { postcode = it },
            label = { Text(label) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        Button(
            onClick = { onPostcodeEntered(postcode) },
            modifier = Modifier
                .border(width = 2.dp, color = Color.Black)
                .align(Alignment.End),
            colors = ButtonColors(
                containerColor = Color(0xFF06402B),
                contentColor = Color.White,
                disabledContainerColor = Color.Gray,
                disabledContentColor = Color.Red,
                )
        ) {
            Text("Submit")
        }
        Text("Your Location is $postcode")
    }
}

data class Coordinates(val latitude: Double, val longitude: Double)

suspend fun postcodeToLocation(
    context: Context,
    addressString: String
): Coordinates? {
    return withContext(Dispatchers.IO) {
        val geocoder = Geocoder(context)
        try {
            @Suppress("DEPRECATION")
            val addresses: MutableList<Address>? = geocoder.getFromLocationName(addressString, 1)
            addresses?.firstOrNull()?.let {
                Coordinates(it.latitude, it.longitude)
            }
//            if (Build .VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            } else {
//                @Suppress("DEPRECATION")
//                val addresses: MutableList<Address>? = geocoder.getFromLocationName(addressString, 1)
//                addresses?.firstOrNull()?.let {
//                    Coordinates(it.latitude, it.longitude)
//                }
//            }
        } catch (e: IOException) {
            println("Geocoding failed for '$addressString': ${e.message}")
            e.printStackTrace()
            null
        } catch (e: IllegalArgumentException) {
            println("Invalid address format for geocoding '$addressString': ${e.message}")
            e.printStackTrace()
            null
        }
    }
}

@Composable
fun MyScreen() {
    val context = LocalContext.current
    var locationResult by remember { mutableStateOf<Coordinates?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Column (modifier = Modifier.padding(16.dp, 100.dp)) {
        Button(
            onClick = {
                isLoading = true
                coroutineScope.launch {
                    locationResult = postcodeToLocation(context, "LU3 3QN")
                    isLoading = false
                }
            },
            modifier = Modifier.padding(16.dp, 100.dp)
        ) {
            Text("Find Location")
        }

        if (isLoading) {
            Text("Loading...")
        } else {
            locationResult?.let {
                Text("Lat: ${it.latitude}, Lng: ${it.longitude}")
            } ?: Text("Location not found or error occurred.")
        }
    }
}