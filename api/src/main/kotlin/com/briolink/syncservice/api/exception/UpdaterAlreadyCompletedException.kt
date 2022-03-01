package com.briolink.syncservice.api.exception

import org.springframework.http.HttpStatus

class UpdaterAlreadyCompletedException() : RuntimeException(), ExceptionInterface {
    override val httpsStatus: HttpStatus = HttpStatus.OK
    override val code: String = "updater.already-completed"
}
