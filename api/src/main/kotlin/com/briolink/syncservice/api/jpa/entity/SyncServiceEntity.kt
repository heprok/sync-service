package com.briolink.syncservice.api.jpa.entity

import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
import javax.persistence.Table

@Entity
@Table(name = "sync_service")
class SyncServiceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    var id: Int? = null

    @ManyToOne(optional = false)
    @JoinColumn(name = "sync_id", nullable = false)
    lateinit var sync: SyncEntity

    @ManyToOne(optional = false)
    @JoinColumn(name = "service", nullable = false)
    lateinit var service: ServiceEntity

    @ManyToOne(optional = false)
    @JoinColumn(name = "updater", nullable = false)
    lateinit var updater: UpdaterEntity

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "syncService", orphanRemoval = true)
    var errors: MutableList<ErrorUpdaterEntity>? = null

    @Column(name = "completed", nullable = false)
    var completed: Boolean = false

    @CreationTimestamp
    @Column(name = "created", nullable = false)
    lateinit var created: Instant

    @UpdateTimestamp
    @Column(name = "changed")
    val changed: Instant? = null
}
