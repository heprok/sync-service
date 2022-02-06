package com.briolink.syncservice.api.exception

import org.springframework.http.HttpStatus

class SyncLogNotCompletedException() : RuntimeException(), ExceptionInterface {
    override val httpsStatus: HttpStatus = HttpStatus.BAD_REQUEST
    override val code: String = "sync-log.not-completed"
}
