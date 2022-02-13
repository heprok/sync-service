package com.briolink.syncservice.api.jpa.entity

import com.briolink.lib.sync.enumeration.ServiceEnum
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "service")
class ServiceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    var id: Int? = null

    @Column(name = "name", nullable = false)
    lateinit var name: String

    val enum: ServiceEnum
        get() = ServiceEnum.ofId(id!!)
}
