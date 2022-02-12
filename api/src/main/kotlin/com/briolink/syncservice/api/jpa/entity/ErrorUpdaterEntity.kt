package com.briolink.syncservice.api.jpa.entity

import org.hibernate.annotations.CreationTimestamp
import java.time.Instant
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.Lob
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(name = "error_updater")
class ErrorUpdaterEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    var id: Int? = null

    @Lob
    @Column(name = "error", nullable = false)
    var error: String? = null

    @CreationTimestamp
    @Column(name = "created")
    var created: Instant? = null

    @ManyToOne
    @JoinColumn(name = "sync_service_id")
    lateinit var syncService: SyncServiceEntity
}
