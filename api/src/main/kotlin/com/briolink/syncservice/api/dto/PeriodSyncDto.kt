package com.briolink.syncservice.api.dto

import com.briolink.lib.sync.enumeration.PeriodSyncEnum
import io.swagger.annotations.ApiModelProperty
import javax.validation.constraints.NotNull

data class PeriodSyncDto(
    @get:NotNull(message = "period-sync-enum.not-null")
    @ApiModelProperty(value = "Period sync", required = true)
    val periodSync: PeriodSyncEnum?,
)
