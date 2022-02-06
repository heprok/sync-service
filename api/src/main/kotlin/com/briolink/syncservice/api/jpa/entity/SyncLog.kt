package com.briolink.syncservice.api.jpa.entity

import com.briolink.syncservice.api.enumeration.MicroServiceEnum
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
import javax.persistence.Table

@Entity
@Table(name = "sync_log")
class SyncLog {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null

    @Column(name = "name_service", nullable = false)
    private lateinit var _nameService: String

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sync_info_id", nullable = false)
    lateinit var syncInfo: SyncInfo

    @Column(name = "completed", nullable = false)
    var completed: Boolean = false

    @CreationTimestamp
    @Column(name = "created", nullable = false)
    lateinit var created: Instant

    @UpdateTimestamp
    @Column(name = "changed")
    val changed: Instant? = null

    var service: MicroServiceEnum
        get() = MicroServiceEnum.valueOf(_nameService)
        set(value) {
            _nameService = value.name
        }
}
