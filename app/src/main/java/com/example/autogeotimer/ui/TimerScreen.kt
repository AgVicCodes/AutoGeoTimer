package com.example.autogeotimer.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay


data class TimerUi(
    val exitMs: Long? = null,
    val startMs: Long? = null,      // Initially null until Dwell confirmed
    val elapsedSec: Long = 0L,      // UI counter
    val isRunning: Boolean = true,
    val isDwelling: Boolean = false // sets to true once startMs is set
)


@Preview(showBackground = true)
@Composable
fun TimerScreenPreview() {
    var ui by remember {
        mutableStateOf(TimerUi(isRunning = true, isDwelling = false, elapsedSec = 5579))
    }
    LaunchedEffect(ui.isRunning) {
        while (ui.isRunning) {
            delay(1000)
            ui = ui.copy(
                elapsedSec = ui.elapsedSec + 1,
                isDwelling = ui.isDwelling || ui.elapsedSec + 1 >= 300
            )
        }
    }
    TimerContent(ui = ui, onToggleRunning = { ui = ui.copy(isRunning = !ui.isRunning) })
}


// ViewModel-driven screen (real app)
@Composable
fun TimerScreen(
    sessionId: Long,
    vm: TimerViewModel
) {
    val ui by vm.state.collectAsState()

    // Load session (start=enter+5min) once
    LaunchedEffect(sessionId) { vm.load(sessionId) }

    // Tick while running and not exited
    LaunchedEffect(ui.isRunning, ui.startMs, ui.exitMs) {
        while (ui.isRunning && ui.startMs != null && ui.exitMs == null) {
            delay(1000)
            vm.tick() // increments elapsedSec from startMs
        }
    }

    TimerContent(
        ui = ui,
        onToggleRunning = { vm.toggleRunning() }
    )
}

@Composable
fun TimerContent(
    ui: TimerUi,
    onToggleRunning: () -> Unit,
    modifier: Modifier = Modifier
) {
    val remaining = (300 - ui.elapsedSec).coerceAtLeast(0)
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val status = if (ui.isDwelling) "✓ Stay confirmed - Timer active" else "Waiting for confirmation..."
        Text(
            text = status,
            style = MaterialTheme.typography.bodyLarge,
            color = if (ui.isDwelling) Color.Green else Color.Gray
        )
        Spacer(Modifier.height(32.dp))
        Text(
            text = formatTime(ui.elapsedSec),
            fontSize = 64.sp,
            fontWeight = FontWeight.Bold,
            color = if (ui.isDwelling) MaterialTheme.colorScheme.primary else Color.Gray
        )
        Spacer(Modifier.height(16.dp))
        if (!ui.isDwelling) {
            var progress = (ui.elapsedSec / 300f).coerceIn(0f, 1f)
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
            )
            Text(
                text = "${remaining}s until confirmed",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
        Spacer(Modifier.height(48.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(
                onClick = onToggleRunning,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (ui.isRunning) Color.Red else Color.Green
                )
            ) { Text(if (ui.isRunning) "Pause" else "Resume") }
        }
        Spacer(Modifier.height(32.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(Modifier.padding(16.dp)) {
                Text("Status: ${if (ui.isRunning) "Active" else "Paused"}", style = MaterialTheme.typography.bodySmall)
                Text("Dwelling: ${if (ui.isDwelling) "Yes" else "No (${ui.elapsedSec}/300s)"}", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

private fun formatTime(seconds: Long): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val secs = seconds % 60
    return "%02d:%02d:%02d".format(hours, minutes, secs)
}