package com.example.autogeotimer.data.repo


import com.example.autogeotimer.data.db.dao.SessionDao
import com.example.autogeotimer.data.db.entities.Session
import kotlinx.coroutines.flow.Flow

class SessionRepository (private val sessionDao: SessionDao) {

    suspend fun startSession(locationId: String): Long {
        return sessionDao.insert(Session(
            locationId = locationId,
            enterTime = System.currentTimeMillis()
        ))
    }

    suspend fun confirmDwell(
        locationId: String
    ) = sessionDao.markDwelling(locationId)

    suspend fun endSession(locationId: String, exitTime: Long) {
        val sessions = sessionDao.getActiveSessions()
        val activeSession = sessions.firstOrNull{
            it.locationId == locationId
        }

        activeSession?.let {
            val durationSeconds: Long = (exitTime - it.enterTime) / 1000
            sessionDao.closeSession(locationId, exitTime, durationSeconds)
        }
    }

    fun getActiveSessionFlow(locationId: String): Flow<Session?> {
        return sessionDao.getActiveSessionFlow(locationId)
    }

    suspend fun getLocationSession(locationId: String) = sessionDao.getByLocation(locationId)
    suspend fun getRecentSessions(limit: Int = 50) = sessionDao.getRecent(limit)
    suspend fun getSessionsInRange(start: Long, end: Long) = sessionDao.getInRange(start, end)

    // Extra functions
    suspend fun openPending(locationId: String, enter: Long): Long =
        sessionDao.insert(Session(locationId = locationId, enterTime = enter))

}
    // suspend fun latestOpen(placeId: String) =
    //    sessionDao.latestOpenForPlace(placeId)

    // Function already initialised
    // suspend fun confirmDwell(sessionId: Long, enter: Long, dwellMs: Long = 5 * 60_000) =
    //      sessionDao.updateStart(sessionId, enter + dwellMs) // Timer starts to count from enter + 5min

    // Function already initialised
    // suspend fun close(sessionId: Long, exit: Long) =
    //     sessionDao.updateExit(sessionId, exit)

    // Shouldn't delete sessions
    // suspend fun discard(sessionId: Long) =
    //     sessionDao.delete(sessionId)

    // Shouldn't delete sessions
    // suspend fun get(id: Long) =
    //     sessionDao.get(id)
