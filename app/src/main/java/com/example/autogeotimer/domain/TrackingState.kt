package com.example.autogeotimer.domain

// domain/TrackingState.kt
sealed interface TrackingState {
    object Idle : TrackingState
    object Locating : TrackingState
    object RequestingPermission : TrackingState
    data class Error(val msg: String) : TrackingState
    data class Ready(val coords: Coordinates) : TrackingState
}