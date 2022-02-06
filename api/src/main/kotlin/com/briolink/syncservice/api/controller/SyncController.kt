package com.briolink.syncservice.api.controller

import com.briolink.syncservice.api.dto.PeriodSyncDto
import com.briolink.syncservice.api.enumeration.MicroServiceEnum
import com.briolink.syncservice.api.enumeration.PeriodSyncEnum
import com.briolink.syncservice.api.jpa.entity.SyncInfo
import com.briolink.syncservice.api.jpa.entity.SyncLog
import com.briolink.syncservice.api.service.SyncService
import com.briolink.syncservice.api.type.PeriodDateTime
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
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

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
    ): ResponseEntity<SyncInfo> {
        return ResponseEntity(syncService.startSync(), HttpStatus.OK)
    }

    @PostMapping("/completed")
    @ApiOperation("Completed sync at service")
    fun completed(
        @ApiParam(value = "Service name", required = true) service: MicroServiceEnum
    ): ResponseEntity<SyncLog> {
        return ResponseEntity(syncService.completedSyncLog(service), HttpStatus.OK)
    }

//    @PostMapping("/stop")
//    @ApiOperation("Stop sync")
//    fun stop(): ResponseEntity<SyncInfo> {
//        return ResponseEntity(syncService.completedSyncLog(), HttpStatus.OK)
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
