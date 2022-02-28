package com.briolink.syncservice.api.jpa.repository

import com.briolink.syncservice.api.jpa.entity.SyncEntity
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.Optional

interface SyncRepository : JpaRepository<SyncEntity, Int> {
    @Query("SELECT s FROM SyncEntity s WHERE s.completed = false ORDER BY s.id DESC")
    fun findLastNotCompleted(pageRequest: PageRequest = PageRequest.of(0, 1)): Optional<SyncEntity>
}
