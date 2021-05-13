package com.borets.pfa.entity.account.appdata

import com.borets.pfa.entity.account.Account
import com.haulmont.chile.core.annotations.Composition
import com.haulmont.chile.core.annotations.MetaProperty
import com.haulmont.cuba.core.entity.StandardEntity
import com.haulmont.cuba.core.entity.annotation.OnDelete
import com.haulmont.cuba.core.global.DeletePolicy
import java.time.YearMonth
import javax.persistence.*

@Table(name = "PFA_APPLICATION_DATA")
@javax.persistence.Entity(name = "pfa_ApplicationData")
open class ApplicationData : StandardEntity() {

    @Column(name = "YEAR_")
    var year: Int? = null

    @Column(name = "MONTH_")
    var month: Int? = null

    @Transient
    @MetaProperty(related = ["month", "year"], datatype = "yearMonth")
    private var yearMonth: String? = null

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ACCOUNT_ID")
    var account: Account? = null

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "applicationData")
    var systemAllocations: MutableList<SystemAllocation>? = mutableListOf()

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "applicationData")
    var utilization: MutableList<EquipmentUtilization>? = mutableListOf()

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
        private const val serialVersionUID = 1467760829462089597L
    }
}