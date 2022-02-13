package com.briolink.syncservice.api.jpa.entity

import com.briolink.lib.sync.enumeration.UpdaterEnum
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "updater")
class UpdaterEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    var id: Int? = null

    @Column(name = "name", nullable = false)
    lateinit var name: String

    val enum: UpdaterEnum
        get() = UpdaterEnum.ofId(id!!)
}
