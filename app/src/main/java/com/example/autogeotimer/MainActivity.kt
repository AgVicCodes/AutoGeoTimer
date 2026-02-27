package com.example.autogeotimer


import android.os.Bundle

import android.content.Context

import android.location.Address
import android.location.Geocoder

import androidx.activity.enableEdgeToEdge
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

import androidx.compose.material3.MaterialTheme

import com.example.autogeotimer.domain.Coordinates
import com.example.autogeotimer.domain.Location
import com.example.autogeotimer.ui.MyAppNavigation

import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers

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

// USE LOCATION MK42 6HF
// Set a timer on Tiktok


object AppDestinations {
    const val HOME_SCREEN = "Home"
    const val INPUT_CHOICE_SCREEN = "InputChoice"
    const val USE_LOCATION_SCREEN = "UseLocation"
    const val INPUT_ADDRESS_SCREEN = "InputAddress"
    const val SHOW_COORDINATES_SCREEN = "ShowCoordinates"
    const val TIMER_SCREEN = "TimerScreen"
    // Add more routes for other features
}