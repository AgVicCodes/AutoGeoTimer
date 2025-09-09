package com.example.autogeotimer.domain

// domain/TrackingState.kt
sealed interface TrackingState {
    object Idle : TrackingState
    object RequestingPermission : TrackingState
    object Locating : TrackingState
    data class Ready(val coords: Coordinates) : TrackingState
    data class Error(val msg: String) : TrackingState
}