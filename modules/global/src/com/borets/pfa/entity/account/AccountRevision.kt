package com.borets.pfa.entity.account

import com.borets.pfa.entity.Employee
import com.haulmont.chile.core.annotations.MetaProperty
import com.haulmont.cuba.core.entity.StandardEntity
import java.time.YearMonth
import javax.persistence.*

@Table(name = "PFA_ACCOUNT_REVISION")
@javax.persistence.Entity(name = "pfa_AccountRevision")
open class AccountRevision : StandardEntity() {
    @Column(name = "YEAR_")
    var year: Int? = null

    @Column(name = "MONTH_")
    var month: Int? = null

    @Transient
    @MetaProperty(related = ["month", "year"], datatype = "yearMonth")
    private var yearMonth: String? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MANAGER_ID")
    var manager: Employee? = null

    @Column(name = "TYPE_")
    private var type: String? = null

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ACCOUNT_ID")
    var account: Account? = null

    @Column(name = "ACTIVE")
    var active: Boolean? = true

    fun getType(): Type? = type?.let { Type.fromId(it) }

    fun setType(type: Type?) {
        this.type = type?.id
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
        private const val serialVersionUID = 3250456937844512270L
    }
}