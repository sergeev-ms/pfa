package com.borets.pfa.entity.account.utilization

import com.borets.pfa.entity.account.Account
import com.borets.pfa.entity.activity.RecordType
import com.haulmont.chile.core.annotations.Composition
import com.haulmont.chile.core.annotations.MetaProperty
import com.haulmont.chile.core.annotations.NumberFormat
import com.haulmont.cuba.core.entity.StandardEntity
import com.haulmont.cuba.core.entity.annotation.OnDelete
import com.haulmont.cuba.core.global.DeletePolicy
import java.time.LocalDate
import java.time.YearMonth
import javax.persistence.*

@Table(name = "PFA_EQUIPMENT_UTILIZATION")
@javax.persistence.Entity(name = "pfa_EquipmentUtilization")
open class EquipmentUtilization : StandardEntity() {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ACCOUNT_ID")
    var account: Account? = null

    @Column(name = "RECORD_TYPE")
    private var recordType: String? = null

    @Column(name = "VALID_FROM")
    var validFrom: LocalDate? = null

    @NumberFormat(pattern = "####")
    @Column(name = "YEAR_")
    var year: Int? = null

    @Column(name = "MONTH_")
    var month: Int? = null

    @Transient
    @MetaProperty(related = ["month", "year"], datatype = "yearMonth")
    var yearMonth: String? = null

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "equipmentUtilization")
    var details: MutableList<EquipmentUtilizationDetail>? = mutableListOf()

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

    @PrePersist
    open fun prePersist() {
        setYearMonth(YearMonth.from(validFrom))
    }

    @PreUpdate
    open fun preUpdate() {
        setYearMonth(YearMonth.from(validFrom))
    }

    companion object {
        private const val serialVersionUID = -8862484810945346623L
    }
}