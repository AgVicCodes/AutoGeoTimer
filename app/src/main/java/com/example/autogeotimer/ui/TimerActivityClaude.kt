//package com.example.autogeotimer.ui
//
//import android.os.Bundle
//import androidx.core.app.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.compose.material3.MaterialTheme
//
//class TimerActivityClaude : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        val placeId = intent.getStringExtra("PLACE_ID") ?: "unknown"
//        val placeName = intent.getStringExtra("PLACE_NAME") ?: "Unknown Location"
//
//        setContent {
//            MaterialTheme {
//                TimerScreenClaude(
//                    placeId = placeId,
//                    placeName = placeName,
//                    onNavigateBack = { finish() }
//                )
//            }
//        }
//    }
//}