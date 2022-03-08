package com.briolink.syncservice.api.jpa.repository

import com.briolink.syncservice.api.jpa.entity.SyncServiceEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.Optional

interface SyncServiceRepository : JpaRepository<SyncServiceEntity, Int> {

    @Query("SELECT s FROM SyncServiceEntity s WHERE s.sync.id = ?1 AND s.service.id = ?2 AND s.updater.id = ?3")
    fun findBySyncIdServiceAndUpdater(syncId: Int, serviceId: Int, updater: Int): Optional<SyncServiceEntity>

    @Query("SELECT (count(s) > 0) FROM SyncServiceEntity s WHERE s.completed = false AND s.sync.id = ?1")
    fun existsMotCompletedBySyncId(syncId: Int): Boolean

    @Query("SELECT s FROM SyncServiceEntity s WHERE s.completed = false AND s.sync.id = ?1")
    fun findAllNotCompleted(syncId: Int): List<SyncServiceEntity>

    @Query("SELECT (count(s) > 0) FROM SyncServiceEntity s WHERE s.completed = false AND s.service.id = ?1")
    fun existsMotCompletedByServiceId(serviceId: Int): Boolean

    @Query("SELECT s FROM SyncServiceEntity s WHERE s.sync.id = ?1 AND s.updater.id = ?2 AND s.service.id = ?3")
    fun findBySyncIdAndUpdaterIdAndServiceId(syncId: Int, updaterId: Int, serviceId: Int): Optional<SyncServiceEntity>
}
