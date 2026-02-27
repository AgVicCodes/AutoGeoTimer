package com.example.autogeotimer.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import com.example.autogeotimer.ui.TimerScreen

class TimerActivityClaude : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val placeId = intent.getStringExtra("PLACE_ID") ?: "unknown"
        val placeName = intent.getStringExtra("PLACE_NAME") ?: "Unknown Location"

        setContent {
            MaterialTheme {
                TimerScreen(
                    locationId = placeId,
                    placeName = placeName,
                    onNavigateBack = { finish() }
                )
            }
        }
    }
} // Work-WixamsSchool