package com.example.autogeotimer

import android.util.Log
import android.os.Bundle
import androidx.compose.*
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

import androidx.compose.ui.unit.dp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.activity.enableEdgeToEdge
import androidx.activity.ComponentActivity
import androidx.compose.material3.TextField
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxWidth
import com.example.autogeotimer.ui.theme.AutoGeoTimerTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AutoGeoTimerTheme {
                PostCodeInput {
                    entered -> Log.d("MainActivity", "Postcode: $entered")
                }
            }
        }
    }
}

@Composable
fun PostCodeInput(
    modifier: Modifier = Modifier,
    label: String = "Enter Postcode",
    onPostcodeEntered: (String) -> Unit
)  {
    var postcode by remember { mutableStateOf("") }

    Column (
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
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
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Submit")
        }
        Text("Your Location is $postcode")
    }
}

//@Composable
//fun MyScreen() {
//    PostCodeInput { entered -> Log.d("MyScreen", "Postcode: $entered") }
//}

























//package com.example.autogeotimer
//
//import android.os.Bundle
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.activity.enableEdgeToEdge
//import androidx.compose.foundation.layout.*
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import kotlinx.coroutines.delay
//import com.example.autogeotimer.ui.theme.AutoGeoTimerTheme
//
//class MainActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//
//        setContent {
//            AutoGeoTimerTheme {
//                // Root of our “two-page” app:
//                TwoPageApp("Victor")
//            }
//        }
//    }
//}
//
//@Composable
//fun TwoPageApp(name: String) {
//    // isLoading = true → show spinner page
//    // isLoading = false → show button page
//    var isLoading by remember { mutableStateOf(true) }
//
//    // After first composition, wait 5 seconds then switch pages
//    LaunchedEffect(Unit) {
//        delay(5_000)
//        isLoading = false
//    }
//
//    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(innerPadding)
//        ) {
//            if (isLoading) {
//                // Page 1: Spinner in the center
//                LoadingPage()
//            } else {
//                // Page 2: Button → “Welcome Victor” when clicked
//                WelcomePage(name = name)
//            }
//        }
//    }
//}
//
//@Composable
//fun LoadingPage() {
//    // A full‐screen box with a centered CircularProgressIndicator
//    Box(
//        modifier = Modifier.fillMaxSize(),
//        contentAlignment = Alignment.Center
//    ) {
//        CircularProgressIndicator()
//    }
//}
//
//@Composable
//fun WelcomePage(name: String) {
//    // Track whether the welcome message has been shown
//    var showWelcome by remember { mutableStateOf(false) }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(32.dp),
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        if (!showWelcome) {
//            Button(onClick = { showWelcome = true }) {
//                Text(text = "Tap to show welcome")
//            }
//        }
//
//        if (showWelcome) {
//            Spacer(modifier = Modifier.height(16.dp))
//            Text(
//                text = "Welcome $name",
//                style = MaterialTheme.typography.headlineMedium
//            )
//        }
//    }
//}
//
//@Preview(showBackground = true)
//@Composable
//fun TwoPageAppPreview() {
//    AutoGeoTimerTheme {
//        TwoPageApp("Victor")
//    }
//}