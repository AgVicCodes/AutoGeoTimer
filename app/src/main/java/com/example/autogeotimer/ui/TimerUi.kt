package com.example.autogeotimer.ui


data class TimerUi(
    val locationName: String = "",
    val startMs: Long? = null,
    val exitMs: Long? = null,
    val elapsedSec: Long = 0,
    val isDwelling: Boolean = false,
    val isRunning: Boolean = false,
    val error: String? = null
)