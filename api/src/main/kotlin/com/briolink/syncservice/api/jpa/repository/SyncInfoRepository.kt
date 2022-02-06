package com.briolink.syncservice.api.jpa.repository

import com.briolink.syncservice.api.jpa.entity.SyncInfo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.Optional

interface SyncInfoRepository : JpaRepository<SyncInfo, Int> {
    @Query("SELECT s FROM SyncInfo s WHERE s.completed = false ORDER BY s.id DESC")
    fun findLastNotCompleted(): Optional<SyncInfo>

    @Query("SELECT (count(s) > 0) FROM SyncInfo s WHERE s.completed = false")
    fun existsNotCompleted(): Boolean
}
