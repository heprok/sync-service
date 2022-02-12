package com.briolink.syncservice.api.exception

import com.briolink.syncservice.api.enumeration.ServiceEnum
import com.briolink.syncservice.api.util.LocaleMessage
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RequestMapping
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

@ControllerAdvice
@RequestMapping(produces = ["application/json"])
class RestGlobalExceptionHandler(private val localeMessage: LocaleMessage) {

    private fun getResponseEntityWithTranslateMessage(
        ex: ExceptionInterface,
        instant: Instant? = null,
        service: ServiceEnum? = null
    ) =
        ResponseEntity<ErrorResponse>(
            ex.errorResponse.apply {
                message = localeMessage.getMessage(ex.code) +
                    service?.let { " " + it.name } +
                    instant?.let { " " + LocalDateTime.ofInstant(instant, ZoneId.systemDefault()) }
            },
            ex.httpsStatus
        )

    @ExceptionHandler(value = [BindException::class])
    fun validationBindException(ex: BindException): ResponseEntity<Any> {

        val msg = ex.fieldErrors.joinToString("\n") {
            it.field + ": " + it.defaultMessage?.let { key -> localeMessage.getMessage(key) }
        }
        return ResponseEntity(msg, HttpStatus.NOT_ACCEPTABLE)
    }

    @ExceptionHandler(value = [SyncAlreadyStartedException::class])
    fun syncAlreadyStarted(ex: SyncAlreadyStartedException): ResponseEntity<ErrorResponse> {
        return getResponseEntityWithTranslateMessage(ex, ex.startDate)
    }

    @ExceptionHandler(SyncNotStartedException::class)
    fun syncNotStarted(ex: SyncNotStartedException): ResponseEntity<ErrorResponse> {
        return getResponseEntityWithTranslateMessage(ex)
    }
}
