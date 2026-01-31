package com.example.autogeotimer.data.repo

import com.example.autogeotimer.data.db.dao.SessionDao
import com.example.autogeotimer.data.db.entities.SessionEntity

class SessionRepo (private val dao: SessionDao) {
    suspend fun openPending(placeId: String, enter: Long): Long =
        dao.insert(SessionEntity(placeId = placeId, enter = enter))

    suspend fun confirmDwell(sessionId: Long, enter: Long, dwellMs: Long = 5 * 60_000) =
        dao.updateStart(sessionId, enter + dwellMs) // Timer starts to count from enter + 5min

    suspend fun close(sessionId: Long, exit: Long) =
        dao.updateExit(sessionId, exit)

    suspend fun discard(sessionId: Long) =
        dao.deleteById(sessionId)

    suspend fun latestOpen(placeId: String) =
        dao.latestOpenForPlace(placeId)

    suspend fun get(id: Long) =
        dao.get(id)
}