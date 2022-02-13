package com.briolink.syncservice.api.service

import com.briolink.lib.sync.enumeration.ServiceEnum
import com.briolink.lib.sync.enumeration.UpdaterEnum
import com.briolink.syncservice.api.config.AppEndpointsProperties
import com.briolink.syncservice.api.exception.ServiceNotCompletedException
import com.briolink.syncservice.api.exception.ServiceNotFoundException
import com.briolink.syncservice.api.exception.SyncAlreadyStartedException
import com.briolink.syncservice.api.exception.SyncNotStartedException
import com.briolink.syncservice.api.exception.SyncServiceNotFoundException
import com.briolink.syncservice.api.jpa.entity.ErrorUpdaterEntity
import com.briolink.syncservice.api.jpa.entity.SyncEntity
import com.briolink.syncservice.api.jpa.entity.SyncServiceEntity
import com.briolink.syncservice.api.jpa.repository.ErrorRepository
import com.briolink.syncservice.api.jpa.repository.ServiceRepository
import com.briolink.syncservice.api.jpa.repository.SyncRepository
import com.briolink.syncservice.api.jpa.repository.SyncServiceRepository
import com.briolink.syncservice.api.jpa.repository.UpdaterRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.reactive.function.client.WebClient
import javax.persistence.EntityNotFoundException

@Service
@Transactional
class SyncService(
    private val appEndpointsProperties: AppEndpointsProperties,
    private val syncRepository: SyncRepository,
    private val updaterRepository: UpdaterRepository,
    private val serviceRepository: ServiceRepository,
    private val syncServiceRepository: SyncServiceRepository,
    private val errorRepository: ErrorRepository,
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun startSync(): SyncEntity {
        val syncInfoNotCompleted = syncRepository.findLastNotCompleted()

        if (syncInfoNotCompleted.isPresent)
            throw SyncAlreadyStartedException(syncInfoNotCompleted.get().created)

        val sync = syncRepository.save(SyncEntity())
        nextSyncService()

        return sync
    }

    fun startSyncEventAtService(serviceEnum: ServiceEnum, syncEntity: SyncEntity? = null): List<SyncServiceEntity> {

        val sync =
            syncEntity ?: syncRepository.findLastNotCompleted().orElseThrow { throw SyncNotStartedException() }

        val service =
            serviceRepository.findById(serviceEnum.id).orElseThrow { throw ServiceNotFoundException(serviceEnum) }
        val listSyncService = mutableListOf<SyncServiceEntity>()
        updaterRepository.findAll().forEach {
            val syncService = syncServiceRepository.findLastNotCompletedByServiceAndUpdater(service.id!!, it.id!!)

            if (syncService.isPresent)
                throw SyncAlreadyStartedException(syncService.get().created, serviceEnum)

            SyncServiceEntity().apply {
                this.sync = sync
                this.service = service
                this.updater = it
                listSyncService.add(syncServiceRepository.save(this))
            }
        }

        val endpointService = when (serviceEnum) {
            ServiceEnum.User -> appEndpointsProperties.user
            ServiceEnum.Company -> appEndpointsProperties.company
            ServiceEnum.CompanyService -> appEndpointsProperties.companyService
            ServiceEnum.Connection -> appEndpointsProperties.connection
        }

        val webClient = WebClient.create(endpointService + "api/v1")
        webClient.post()
            .uri("/sync")
            .retrieve()
            .bodyToMono(Void::class.java)
            .block()

        return listSyncService
    }

    fun completedUpdaterSync(service: ServiceEnum, updater: UpdaterEnum): SyncServiceEntity {
        var syncService = syncServiceRepository.findLastNotCompletedByServiceAndUpdater(service.id, updater.id)
            .orElseThrow { throw EntityNotFoundException() }

        syncService.completed = true
        syncService = syncServiceRepository.save(syncService)
        nextSyncService(syncService)
        return syncService
    }

    fun addErrorUpdater(syncServiceId: Int, errorText: String) {
        syncServiceRepository.findById(syncServiceId).orElseThrow { SyncServiceNotFoundException(syncServiceId) }
            .apply {
                val error = ErrorUpdaterEntity().let {
                    it.syncService = this
                    it.error = errorText
                    errorRepository.save(it)
                }
                errors?.add(error) ?: mutableListOf(error)
                sync.completed = true
                sync.completedWithError = true
                completed = true

                syncServiceRepository.save(this)
            }
    }

    fun completedSync(): SyncEntity {
        val sync = syncRepository.findLastNotCompleted().orElseThrow { throw SyncNotStartedException() }
        if (syncServiceRepository.existsMotCompletedBySyncId(sync.id!!)) {
            syncServiceRepository.findAllNotCompleted(sync.id!!).forEach {
                logger.info(it.service.name + " updater " + it.updater.name + " not completed at created" + it.created)
            }
            throw ServiceNotCompletedException()
        }
        sync.completed = true

        return syncRepository.save(sync)
    }

    fun existsUpdaterNotCompleted(service: ServiceEnum): Boolean =
        syncServiceRepository.existsMotCompletedByServiceId(service.id)

    fun nextSyncService(prevSyncServiceEntity: SyncServiceEntity? = null) {
        if (prevSyncServiceEntity == null) {
            startSyncEventAtService(ServiceEnum.Company)
            return
        }

        if (prevSyncServiceEntity.sync.completedWithError || existsUpdaterNotCompleted(prevSyncServiceEntity.service.enum))
            return

        if (prevSyncServiceEntity.service.enum == ServiceEnum.Company) {
            startSyncEventAtService(ServiceEnum.User, prevSyncServiceEntity.sync)
            startSyncEventAtService(ServiceEnum.CompanyService, prevSyncServiceEntity.sync)
        }

        if (prevSyncServiceEntity.service.enum == ServiceEnum.User) {
            if (!syncServiceRepository.existsMotCompletedByServiceId(ServiceEnum.CompanyService.id))
                startSyncEventAtService(ServiceEnum.Connection)
        }

        if (prevSyncServiceEntity.service.enum == ServiceEnum.CompanyService) {
            if (!syncServiceRepository.existsMotCompletedByServiceId(ServiceEnum.User.id))
                startSyncEventAtService(ServiceEnum.Connection)
        }

        if (prevSyncServiceEntity.service.enum == ServiceEnum.Connection) {
            completedSync()
        }
    }
}
