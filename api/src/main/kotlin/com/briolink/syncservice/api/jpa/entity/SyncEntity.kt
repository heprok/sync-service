package com.briolink.syncservice.api.jpa.entity

import com.vladmihalcea.hibernate.type.range.PostgreSQLRangeType
import com.vladmihalcea.hibernate.type.range.Range
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.TypeDef
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "sync")
@TypeDef(typeClass = PostgreSQLRangeType::class, defaultForType = Range::class)
class SyncEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    var id: Int? = null

    @Column(name = "completed", nullable = false)
    var completed: Boolean = false

    @Column(name = "completed_with_error", nullable = false)
    var completedWithError: Boolean = false

    @Column(name = "period", columnDefinition = "tsrange")
    var period: Range<LocalDateTime>? = null

    @CreationTimestamp
    @Column(name = "created", nullable = false)
    lateinit var created: Instant

    @UpdateTimestamp
    @Column(name = "changed")
    var changed: Instant? = null
}
