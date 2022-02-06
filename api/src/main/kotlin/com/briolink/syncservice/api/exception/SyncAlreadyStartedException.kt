package com.briolink.syncservice.api.exception

import com.briolink.syncservice.api.enumeration.MicroServiceEnum
import org.springframework.http.HttpStatus
import java.time.Instant

class SyncAlreadyStartedException(val startDate: Instant, val service: MicroServiceEnum? = null) :
    RuntimeException(),
    ExceptionInterface {
    override val httpsStatus: HttpStatus = HttpStatus.CONFLICT
    override val code: String = "sync-info.already-started"
}
