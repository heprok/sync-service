package com.briolink.syncservice.api.dto

import com.briolink.lib.sync.enumeration.ServiceEnum
import com.briolink.lib.sync.enumeration.UpdaterEnum
import com.briolink.syncservice.api.jpa.entity.ErrorUpdaterEntity
import java.time.Instant

data class SyncInfo(
    var syncId: Int? = null,
    var completed: Boolean = false,
    var completedWithError: Boolean = false,
    var syncStartDateTime: Instant? = null,
    var syncEndDateTime: Instant? = null,
    val notCompletedServices: List<SyncServiceInfo>,
    var completedServices: List<SyncServiceInfo>,
    var created: Instant,
    var changed: Instant? = null
)

data class SyncServiceInfo(
    val service: String,
    val completedUpdaters: List<SyncUpdaterInfo>,
    val notCompletedUpdaters: List<SyncUpdaterInfo>,
    var completed: Instant? = null,
)

data class SyncUpdaterInfo(
    val updater: String,
    val completed: Instant? = null,
    val errors: List<ErrorUpdaterEntity> = listOf(),
)

