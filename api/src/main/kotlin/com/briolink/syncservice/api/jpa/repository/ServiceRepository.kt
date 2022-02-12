package com.briolink.syncservice.api.jpa.repository

import com.briolink.syncservice.api.jpa.entity.ServiceEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ServiceRepository : JpaRepository<ServiceEntity, Int>
