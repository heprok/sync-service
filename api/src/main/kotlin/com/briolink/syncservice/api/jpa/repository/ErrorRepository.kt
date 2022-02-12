package com.briolink.syncservice.api.jpa.repository

import com.briolink.syncservice.api.jpa.entity.ErrorUpdaterEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ErrorRepository : JpaRepository<ErrorUpdaterEntity, Int>
