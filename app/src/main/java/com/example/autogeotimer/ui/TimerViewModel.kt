package com.example.autogeotimer.ui


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.ViewModelProvider

import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableLongStateOf

import com.example.autogeotimer.data.db.entities.Session
import com.example.autogeotimer.data.repo.SessionRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

suspend fun SessionRepository.getActiveSession(locationId: String): Session? {
    return getLocationSession(locationId).firstOrNull { it.exitTime == null }
}

class TimerViewModel(
    private val repo: SessionRepository,
    private val locationId: String
) : ViewModel() {

    private val _state = MutableStateFlow(TimerUi())
    val state: StateFlow<TimerUi> = _state

    private var timerJob: Job? = null

    /**
     * To Load active sessions for this location and start live timer updates
     * */

    val activeSession: StateFlow<Session?> =
        repo.getActiveSessionFlow(locationId) // Ensure your repo provides a Flow
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = null
            )

// //    Method 2 for calling repo.getActiveSessions(locationId)
//    private val _activeSession = MutableStateFlow<Session?>(null)
//    val activeSession: StateFlow<Session?> = _activeSession
//
//    private fun fetchSession() {
//        viewModelScope.launch {
//            // Safe to call suspend functions here inside a coroutine scope
//            _activeSession.value = repo.getActiveSession(locationId)
//        }
//    }
//
//    init {
//        fetchSession()
//    }


    fun load(locationId: String, locationName: String) = viewModelScope.launch(Dispatchers.IO) {
        val sessions = repo.getLocationSession(locationId)
        val activeSession = sessions.firstOrNull { it.exitTime == null }

        if (activeSession == null) {
            _state.update { it.copy(error = "No active session found") }
            return@launch
        }

        // Updating state with session data
        _state.update {
            it.copy(
                locationName = locationName,
                startMs = activeSession.enterTime,
                exitMs = activeSession.exitTime,
                isDwelling = activeSession.dwellConfirmed,
                elapsedSec = (System.currentTimeMillis() - activeSession.enterTime) / 1000
            )
        }

        // Start live timer updates
        startLiveTimer()
    }

    fun tick() = _state.update { st ->
        st.copy(elapsedSec = st.elapsedSec + 1)
    }

    private fun startLiveTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_state.value.isRunning) {
                delay(1000) // Update every second
                _state.update { current ->
                    current.copy(
                        elapsedSec = current.elapsedSec + 1
//                        elapsedSec = current.startMs?.let {
//                            start -> (System.currentTimeMillis() - start) / 1000
//                        } ?: 0
                    )
                }
            }
        }
    }

    /*
    Pause/Resume timer (Doesn't affect session - just UI)
     */
    fun togglePause() {
        _state.update { it.copy(isRunning = !it.isRunning) }
        if (_state.value.isRunning) {
            startLiveTimer()
        } else {
            timerJob?.cancel()
        }
    }

    // Same as toggle pause basically
    // fun toggleRunning() = _state.update { st -> st.copy(isRunning = !st.isRunning) }

    fun endSession(locationId: String) = viewModelScope.launch(Dispatchers.IO) {
        repo.endSession(locationId, System.currentTimeMillis())
        _state.update { it.copy(isRunning = false, exitMs = System.currentTimeMillis()) }
        timerJob?.cancel()
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }

}

/**
 * Extension function to get active session for a location
 */

//suspend fun SessionRepository.getActiveSession(locationId: String): Session? {
//    return getLocationSession(locationId).firstOrNull { it.exitTime == null }
//}

class TimerVmFactory(
    private val repo: SessionRepository,
    private val locationId: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TimerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TimerViewModel(repo, locationId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}