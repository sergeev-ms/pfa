package com.borets.pfa.entity.activity

import com.borets.pfa.entity.account.Account
import com.haulmont.chile.core.annotations.Composition
import com.haulmont.chile.core.annotations.MetaProperty
import com.haulmont.chile.core.annotations.NamePattern
import com.haulmont.chile.core.annotations.NumberFormat
import com.haulmont.cuba.core.entity.StandardEntity
import com.haulmont.cuba.core.entity.annotation.OnDelete
import com.haulmont.cuba.core.global.DeletePolicy
import java.time.LocalDate
import java.time.YearMonth
import javax.persistence.*

@NamePattern(value = "%s - %s|account,year")
@Table(name = "PFA_ACTIVITY")
@Entity(name = "pfa_Activity")
open class Activity : StandardEntity() {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ACCOUNT_ID")
    var account: Account? = null

    @Column(name = "RECORD_TYPE")
    private var recordType: String? = null

    @NumberFormat(pattern = "####")
    @Column(name = "YEAR_")
    var year: Int? = null

    @Column(name = "MONTH_")
    var month: Int? = null

    @Transient
    @MetaProperty(related = ["month", "year"], datatype = "yearMonth")
    var yearMonth: String? = null

    @Column(name = "PERIOD_FROM")
    var periodFrom: LocalDate? = null

    @Column(name = "PERIOD_TO")
    var periodTo: LocalDate? = null

    @Lob
    @Column(name = "COMMENT_")
    var comment: String? = null

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "activity")
    var details: MutableList<ActivityDetail>? = mutableListOf()

    fun getRecordType(): RecordType? = recordType?.let { RecordType.fromId(it) }

    fun setRecordType(recordType: RecordType?) {
        this.recordType = recordType?.id
    }

    fun getYearMonth(): YearMonth? {
        return if (year != null && month != null) {
            YearMonth.of(year!!, month!!)
        } else null
    }
    fun setYearMonth(yearMonth: YearMonth?) {
        year = yearMonth?.year
        month = yearMonth?.monthValue
    }

    companion object {
        private const val serialVersionUID = -6451509841681104317L
    }

    @PrePersist
    open fun prePersist() {
        setYearMonth(YearMonth.from(periodFrom))
    }

    @PreUpdate
    open fun preUpdate() {
        setYearMonth(YearMonth.from(periodFrom))
    }
}