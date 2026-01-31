package com.example.autogeotimer.domain
//
//import com.example.autogeotimer.data.repo.SessionRepo
//import kotlinx.coroutines.Dispatchers
//
//class TimerViewModel (private val repo: SessionRepo) : ViewModel() {
//    data class Ui(val start: Long? = null, val exit: Long? = null, val elapsedMs: Long = 0) {
//        val formattedElapsed: String = "" // mm:ss from elapsedMs
//    }
//    private val _state = MutableStateFlow(Ui());
//    val state = _state.asStateFlow()
//    private var baseStart: Long? = null
//
//    fun load(id: Long) = viewModelScope.launch(Dispatchers.IO) {
//        val s = repo.get(id)
//        baseStart = s.start
//        _state.update { it.copy(start = s.start, exit = s.exit, elapsedMs = s.durationMs ?: 0) }
//    }
//
//    fun tick() {
//        val start = baseStart ?: return
//        _state.update { it.copy(elapsedMs = System.currentTimeMillis() - start) }
//    }
//}