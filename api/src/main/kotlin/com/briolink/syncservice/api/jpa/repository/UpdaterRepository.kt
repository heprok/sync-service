package com.briolink.syncservice.api.jpa.repository

import com.briolink.syncservice.api.jpa.entity.UpdaterEntity
import org.springframework.data.jpa.repository.JpaRepository

interface UpdaterRepository : JpaRepository<UpdaterEntity, Int>
