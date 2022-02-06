package com.briolink.syncservice.api.service

import com.briolink.syncservice.api.config.AppEndpointsProperties
import com.briolink.syncservice.api.enumeration.MicroServiceEnum
import com.briolink.syncservice.api.exception.SyncAlreadyStartedException
import com.briolink.syncservice.api.exception.SyncLogNotCompletedException
import com.briolink.syncservice.api.exception.SyncNotStartedException
import com.briolink.syncservice.api.jpa.entity.SyncInfo
import com.briolink.syncservice.api.jpa.entity.SyncLog
import com.briolink.syncservice.api.jpa.repository.SyncInfoRepository
import com.briolink.syncservice.api.jpa.repository.SyncLogRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.reactive.function.client.WebClient
import javax.persistence.EntityNotFoundException

@Service
@Transactional
class SyncService(
    private val appEndpointsProperties: AppEndpointsProperties,
    private val syncInfoRepository: SyncInfoRepository,
    private val syncLogRepository: SyncLogRepository,
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun startSync(): SyncInfo {
        val syncInfoNotCompleted = syncInfoRepository.findLastNotCompleted()

        if (syncInfoNotCompleted.isPresent)
            throw SyncAlreadyStartedException(syncInfoNotCompleted.get().created)

        nextSyncLog()

        return syncInfoRepository.save(SyncInfo())
    }

    fun startSyncEventAtService(service: MicroServiceEnum, syncInfo: SyncInfo? = null): SyncLog {

        val syncInfo =
            syncInfo ?: syncInfoRepository.findLastNotCompleted().orElseThrow { throw SyncNotStartedException() }

        val syncLogNotCompleted = syncLogRepository.findLastNotCompletedByServiceName(service.name)

        if (syncLogNotCompleted.isPresent)
            throw SyncAlreadyStartedException(syncLogNotCompleted.get().created, service)

        val endpointService = when (service) {
            MicroServiceEnum.User -> appEndpointsProperties.user
            MicroServiceEnum.Company -> appEndpointsProperties.company
            MicroServiceEnum.CompanyService -> appEndpointsProperties.companyService
            MicroServiceEnum.Connection -> appEndpointsProperties.connection
        }

        val webClient = WebClient.create(endpointService + "api/v1")
        webClient.post()
            .uri("/sync")
            .retrieve()
            .bodyToMono(Void::class.java)
            .block()

        val syncLog = SyncLog().apply {
            this.service = service
            this.syncInfo = syncInfo
        }
        return syncLogRepository.save(syncLog)
    }

    fun completedSyncLog(service: MicroServiceEnum): SyncLog {
        var syncLog = syncLogRepository.findLastNotCompletedByServiceName(service.name)
            .orElseThrow { throw EntityNotFoundException() }

        syncLog.completed = true
        syncLog = syncLogRepository.save(syncLog)
        nextSyncLog(syncLog)
        return syncLog
    }

    fun completedSyncInfo(): SyncInfo {
        val syncInfo = syncInfoRepository.findLastNotCompleted().orElseThrow { throw SyncNotStartedException() }
        if (syncLogRepository.existsMotCompleted()) {
            syncLogRepository.findAllNotCompleted().forEach {
                logger.info(it.service.name + " at started " + it.created)
            }
            throw SyncLogNotCompletedException()
        }
        syncInfo.completed = true

        return syncInfoRepository.save(syncInfo)
    }

    fun nextSyncLog(prevSyncLog: SyncLog? = null) {
        if (prevSyncLog == null) {
            startSyncEventAtService(MicroServiceEnum.Company)
            return
        }

        if (prevSyncLog.service == MicroServiceEnum.Company) {
            startSyncEventAtService(MicroServiceEnum.User, prevSyncLog.syncInfo)
            startSyncEventAtService(MicroServiceEnum.CompanyService, prevSyncLog.syncInfo)
        }

        if (prevSyncLog.service == MicroServiceEnum.User) {
            if (!syncLogRepository.existsMotCompletedByNameService(MicroServiceEnum.CompanyService.name))
                startSyncEventAtService(MicroServiceEnum.Connection)
        }

        if (prevSyncLog.service == MicroServiceEnum.CompanyService) {
            if (!syncLogRepository.existsMotCompletedByNameService(MicroServiceEnum.User.name))
                startSyncEventAtService(MicroServiceEnum.Connection)
        }

        if (prevSyncLog.service == MicroServiceEnum.Connection) {
            completedSyncInfo()
        }
    }
}
