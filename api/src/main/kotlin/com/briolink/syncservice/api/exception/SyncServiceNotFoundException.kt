package com.briolink.syncservice.api.exception

import org.springframework.http.HttpStatus
import javax.persistence.EntityNotFoundException

class SyncServiceNotFoundException :
    EntityNotFoundException("Sync service not found"), ExceptionInterface {
    override val httpsStatus: HttpStatus = HttpStatus.NOT_FOUND
    override val code: String = "sync-service.not-found"
}
