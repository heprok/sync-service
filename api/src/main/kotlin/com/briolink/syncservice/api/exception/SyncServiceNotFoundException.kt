package com.briolink.syncservice.api.exception

import org.springframework.http.HttpStatus
import javax.persistence.EntityNotFoundException

class SyncServiceNotFoundException(id: Int) :
    EntityNotFoundException("Sync service with id $id not found"), ExceptionInterface {
    override val httpsStatus: HttpStatus = HttpStatus.NOT_FOUND
    override val code: String = "sync-service.not-found"
}
