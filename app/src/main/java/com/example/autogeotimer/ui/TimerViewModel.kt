package com.example.autogeotimer.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope

import kotlinx.coroutines.flow.MutableStateFlow

import com.example.autogeotimer.data.repo.SessionRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class TimerViewModel(private val repo: SessionRepo) : ViewModel() {
    private val _state = MutableStateFlow(TimerUi())
    val state: StateFlow<TimerUi> = _state

    fun load(sessionId: Long) = viewModelScope.launch(Dispatchers.IO) {
        val s = repo.get(sessionId) ?: return@launch

        _state.update {
            it.copy(
                startMs = s.start,
                exitMs = s.exit,
                isDwelling = s.start != null,
                elapsedSec = if (s.start != null) (System.currentTimeMillis() - s.start) / 1000 else 0
            )
        }
    }

    fun tick() = _state.update { st ->
        st.copy(elapsedSec = st.elapsedSec + 1)
    }

    fun toggleRunning() = _state.update { st -> st.copy(isRunning = !st.isRunning) }
}

class TimerVmFactory(private val repo: SessionRepo) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(c: Class<T>): T = TimerViewModel(repo) as T
}