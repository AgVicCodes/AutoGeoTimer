// Chatgpt:

/*

package com.example.autogeotimer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import com.example.autogeotimer.ui.theme.AutoGeoTimerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AutoGeoTimerTheme {
                // Root of our “two-page” app:
                TwoPageApp("Victor")
            }
        }
    }
}

@Composable
fun TwoPageApp(name: String) {
    // isLoading = true → show spinner page
    // isLoading = false → show button page
    var isLoading by remember { mutableStateOf(true) }

    // After first composition, wait 5 seconds then switch pages
    LaunchedEffect(Unit) {
        delay(5_000)
        isLoading = false
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (isLoading) {
                // Page 1: Spinner in the center
                LoadingPage()
            } else {
                // Page 2: Button → “Welcome Victor” when clicked
                WelcomePage(name = name)
            }
        }
    }
}

@Composable
fun LoadingPage() {
    // A full‐screen box with a centered CircularProgressIndicator
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun WelcomePage(name: String) {
    // Track whether the welcome message has been shown
    var showWelcome by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (!showWelcome) {
            Button(onClick = { showWelcome = true }) {
                Text(text = "Tap to show welcome")
            }
        }

        if (showWelcome) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Welcome $name",
                style = MaterialTheme.typography.headlineMedium
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TwoPageAppPreview() {
    AutoGeoTimerTheme {
        TwoPageApp("Victor")
    }
}

*/

// Grok:

/*

package com.example.autogeotimer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.autogeotimer.ui.theme.AutoGeoTimerTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AutoGeoTimerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppNavigation(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "loading") {
        composable("loading") {
            LoadingScreen(
                onTimeout = { navController.navigate("welcome") },
                modifier = modifier
            )
        }
        composable("welcome") {
            WelcomeScreen(modifier = modifier)
        }
    }
}

@Composable
fun LoadingScreen(onTimeout: () -> Unit, modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition()
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    LaunchedEffect(Unit) {
        delay(5000)
        onTimeout()
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .size(60.dp)
                .graphicsLayer { rotationZ = rotation },
            strokeWidth = 6.dp
        )
    }
}

@Composable
fun WelcomeScreen(modifier: Modifier = Modifier) {
    var showMessage by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = { showMessage = true },
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Show Welcome Message")
        }
        if (showMessage) {
            Text(
                text = "Welcome Victor!",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoadingScreenPreview() {
    AutoGeoTimerTheme {
        LoadingScreen(onTimeout = {})
    }
}

@Preview(showBackground = true)
@Composable
fun WelcomeScreenPreview() {
    AutoGeoTimerTheme {
        WelcomeScreen()
    }
}

*/