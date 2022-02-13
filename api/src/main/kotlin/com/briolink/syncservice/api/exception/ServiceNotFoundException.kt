package com.briolink.syncservice.api.exception

import com.briolink.lib.sync.enumeration.ServiceEnum
import org.springframework.http.HttpStatus
import javax.persistence.EntityNotFoundException

class ServiceNotFoundException(serviceEnum: ServiceEnum) :
    EntityNotFoundException("Service  ${serviceEnum.name} with id ${serviceEnum.id} not found"), ExceptionInterface {
    override val httpsStatus: HttpStatus = HttpStatus.NOT_FOUND
    override val code: String = "service.not-found"
}
