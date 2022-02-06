package com.briolink.syncservice.api.jpa.repository

import com.briolink.syncservice.api.jpa.entity.SyncLog
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.Optional

interface SyncLogRepository : JpaRepository<SyncLog, Int> {

    @Query("SELECT (count(s) > 0) FROM SyncLog s WHERE s._nameService = ?1 AND s.completed = false")
    fun existsMotCompletedByNameService(nameService: String): Boolean

    @Query("SELECT s FROM SyncLog s WHERE s._nameService = ?1 AND s.completed = false")
    fun findLastNotCompletedByServiceName(nameService: String): Optional<SyncLog>

    @Query("SELECT (count(s) > 0) FROM SyncLog s WHERE s.completed = false")
    fun existsMotCompleted(): Boolean

    @Query("SELECT s FROM SyncLog s WHERE s.completed = false")
    fun findAllNotCompleted(): List<SyncLog>
}
