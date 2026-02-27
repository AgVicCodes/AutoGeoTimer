package com.example.autogeotimer.ui


import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight

import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableLongStateOf

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth

import androidx.compose.material3.Text
import androidx.compose.material3.Card
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.autogeotimer.data.db.AppDb
import com.example.autogeotimer.data.repo.SessionRepository

import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

private fun formatTime(seconds: Long): String {
    val secs = seconds % 60
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    //// alternatively
    return "%02d:%02d:%02d".format(hours, minutes, secs)
    // return "${hours}:${minutes}:${secs}"
}

// LU3 3QN MK42 6HF
@Preview(showBackground = true)
@Composable
fun TimerScreenClaudePreview() {
    val placeId = "place_92589032240"
    val placeName = "Work"

    MaterialTheme {
        TimerScreen(
            locationId = placeId,
            placeName = placeName,
            onNavigateBack = {}
        )
    }
}
// Hoodie kid 15:04

@Composable
fun TimerScreen(
    locationId: String,
    placeName: String,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current

    // Initialising ViewModel with factory
    val viewModel: TimerViewModel = viewModel(
        factory = TimerVmFactory(
            repo = SessionRepository(AppDb.getInstance(context).sessionDao()),
            locationId = locationId
        )
    )

    // Collect state from ViewModel
    val state by viewModel.state.collectAsState()

    // Timer effect
    // Fix timer screen tick
    LaunchedEffect(state.isRunning) {
//        while (state.isRunning) {
//            delay(1.seconds)
//            viewModel.tick()
//        }
        viewModel.load(
            locationId = locationId,
            locationName = placeName
        )
    }

    // To determine actual status messages
    val status = when {
        state.error != null -> state.error!!
        state.isDwelling -> "Stay confirmed - Timer active"
        else -> "Waiting for confirmation..."
    }

    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = state.locationName.ifEmpty { placeName },
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = status,
            style = MaterialTheme.typography.bodyLarge,
            color = if (state.isDwelling) Color.Green else Color.Gray
        )

        Spacer(Modifier.height(32.dp))

        // Timer display
        Text(
            text = formatTime(state.elapsedSec),
            fontSize = 64.sp,
            fontWeight = FontWeight.Bold,
            color = if (state.isDwelling) MaterialTheme.colorScheme.primary else Color.Gray
        )

        Spacer(Modifier.height(16.dp))

        if (!state.isDwelling) {
            LinearProgressIndicator(
                progress = { (state.elapsedSec / 300f).coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
            )
            Text(
                text = "${(300 - state.elapsedSec).coerceAtLeast(0)}s until stay confirmed",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp),
                color = Color.Gray
            )
        }

        Spacer(Modifier.height(16.dp))

        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { viewModel.togglePause() },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (state.isRunning) Color.Red else Color(0XFF237D1F)
                )
            ) {
                Text(if (state.isRunning) "Pause" else "Resume")
            }

            OutlinedButton(
                onClick = {
                    // TODO: Fix Broken Process
                    viewModel.endSession(locationId)
                    onNavigateBack()
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.Red
                ),
                border = BorderStroke(
                    color = Color.Red,
                    width = 1.dp
                )
            ) {
                Text("Terminate Session")
            }
        }

        Spacer(Modifier.height(16.dp))

        Card (
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(Modifier.padding(16.dp)) {
                Text(
                    "Place ID: $locationId",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    "Status: ${if (state.isRunning) "Active" else "Inactive"}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    "Dwelling: ${if (state.isDwelling) "Yes" else "No (${state.elapsedSec}/300s)"}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}