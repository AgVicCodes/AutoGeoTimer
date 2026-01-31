package com.example.autogeotimer.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

private fun formatTime(seconds: Long): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val secs = seconds % 60
    return "%02d:%02d:%02d".format(hours, minutes, secs)
}

@Composable
fun TimerScreen(
    placeId: String,
    placeName: String,
    onNavigateBack: () -> Unit
) {
    var elapsedSeconds: Long by remember { mutableLongStateOf(0L) }
    var isRunning: Boolean by remember { mutableStateOf(true) }
    var status: String by remember { mutableStateOf("Waiting for confirmation...") }
    var isDwelling: Boolean by remember { mutableStateOf(false) }

    // Timer effect
    LaunchedEffect(isRunning) {
        while (isRunning) {
            delay(1.seconds)
            elapsedSeconds++

            // Auto-confirm dwelling after 5 minutes
            if (elapsedSeconds >= 300 && !isDwelling) {
                isDwelling = true
                status = "Stay confirmed - Timer active"
            }
        }
    }

    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = placeName,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = status,
            style = MaterialTheme.typography.bodyLarge,
            color = if (isDwelling) Color.Green else Color.Gray
        )

        Spacer(Modifier.height(32.dp))

        // Timer display
        Text(
            text = formatTime(elapsedSeconds),
            fontSize = 64.sp,
            fontWeight = FontWeight.Bold,
            color = if (isDwelling) MaterialTheme.colorScheme.primary else Color.Gray
        )

        Spacer(Modifier.height(16.dp))

        if (!isDwelling) {
            LinearProgressIndicator(
                progress = { (elapsedSeconds / 300f).coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
            )
            Text(
                text = "${300 - elapsedSeconds}s until stay confirmed",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Spacer(Modifier.height(16.dp))

        Row (
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = { isRunning = !isRunning },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isRunning) Color.Red else Color.Green
                )
            ) {
                Text(if (isRunning) "Pause" else "Resume")
            }

            OutlinedButton(onClick = onNavigateBack) {
                Text("End Session")
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
                    "Place ID: $placeId",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    "Status: ${if (isRunning) "Active" else "Paused"}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    "Dwelling: ${if (isDwelling) "Yes" else "No (${elapsedSeconds}/300s)"}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }


}