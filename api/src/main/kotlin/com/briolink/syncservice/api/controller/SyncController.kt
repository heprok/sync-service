package com.briolink.syncservice.api.controller

import com.briolink.lib.sync.enumeration.PeriodSyncEnum
import com.briolink.lib.sync.enumeration.ServiceEnum
import com.briolink.lib.sync.enumeration.UpdaterEnum
import com.briolink.lib.sync.model.PeriodDateTime
import com.briolink.syncservice.api.dto.PeriodSyncDto
import com.briolink.syncservice.api.jpa.entity.SyncEntity
import com.briolink.syncservice.api.jpa.entity.SyncServiceEntity
import com.briolink.syncservice.api.service.SyncService
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import io.swagger.v3.oas.annotations.parameters.RequestBody
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid
import javax.validation.constraints.NotNull

@RestController
@Validated
@RequestMapping("/api/v1/sync")
@Api(description = "Sync manager")
@ApiResponses(
    ApiResponse(code = 200, message = "Sync started"),
    ApiResponse(code = 403, message = "Unauthorized"),
    ApiResponse(code = 409, message = "Sync is already running"),
)
class SyncController(private val syncService: SyncService) {
    @PostMapping("/start")
    @ApiOperation("Start sync")
    fun start(
        @Valid @RequestBody periodSync: PeriodSyncDto
    ): ResponseEntity<SyncEntity> {
        return ResponseEntity(
            syncService.startSync(
                periodSync.periodSync?.let {
                    if (it == PeriodSyncEnum.AllTime) null else PeriodDateTime.fromEnum(it)
                }
            ),
            HttpStatus.OK
        )
    }

    @PostMapping("/completed")
    @ApiOperation("Completed sync at updater")
    fun completed(
        @NotNull @RequestParam("updater") @ApiParam(value = "Updater name", required = true) updater: UpdaterEnum,
        @NotNull @RequestParam("service") @ApiParam(value = "Service name", required = true) service: ServiceEnum,
    ): ResponseEntity<SyncServiceEntity> {
        return ResponseEntity(syncService.completedUpdaterSync(service = service, updater = updater), HttpStatus.OK)
    }

    @PostMapping("/error")
    @ApiOperation("Create error and stop sync")
    fun error(
        @NotNull @RequestParam @ApiParam(value = "Sync id", required = true) syncId: Int,
        @NotNull @RequestParam @ApiParam(value = "Updater name", required = true) updater: UpdaterEnum,
        @NotNull @RequestParam @ApiParam(value = "Service name", required = true) service: ServiceEnum,
        @NotNull @RequestParam @ApiParam(value = "Text error") errorText: String,
    ): ResponseEntity<Any> {
        syncService.addErrorUpdater(syncId = syncId, updater = updater, service = service, errorText = errorText)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }

//    @PostMapping("/stop")
//    @ApiOperation("Stop sync")
//    fun stop(): ResponseEntity<Sync> {
//        return ResponseEntity(syncService.completedSync(), HttpStatus.OK)
//    }

    @GetMapping
    @ApiOperation("Info")
    fun info(
        @ApiParam(value = "Period", required = true) periodSync: PeriodSyncEnum
    ): String {
        return PeriodDateTime.fromEnum(periodSync)
            .let { it.startDateTime.toString() + " - " + it.endDateTime.toString() }
    }
}
