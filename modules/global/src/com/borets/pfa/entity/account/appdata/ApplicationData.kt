package com.borets.pfa.entity.account.appdata

import com.borets.pfa.entity.account.Account
import com.borets.pfa.entity.account.system.SystemStd
import com.haulmont.chile.core.annotations.MetaProperty
import com.haulmont.cuba.core.entity.StandardEntity
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

    @JoinTable(
        name = "PFA_APPLICATION_DATA_SYSTEM_STD_LINK",
        joinColumns = [JoinColumn(name = "APPLICATION_DATA_ID")],
        inverseJoinColumns = [JoinColumn(name = "SYSTEM_STD_ID")]
    )
    @ManyToMany
    var systems: MutableList<SystemStd>? = mutableListOf()

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