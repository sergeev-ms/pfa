package com.borets.pfa.entity.activity

import com.borets.pfa.entity.analytic.AnalyticSet
import com.haulmont.chile.core.annotations.MetaProperty
import com.haulmont.chile.core.annotations.NumberFormat
import com.haulmont.cuba.core.entity.StandardEntity
import java.time.LocalDate
import java.time.YearMonth
import javax.persistence.*

@Table(name = "PFA_ACTIVITY_DETAIL")
@Entity(name = "pfa_ActivityDetail")
open class ActivityDetail : StandardEntity() {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ANALYTIC_ID")
    var analytic: AnalyticSet? = null

    @NumberFormat(pattern = "####")
    @Column(name = "YEAR_")
    var year: Int? = null

//    @NumberFormat(pattern = "0#")
    @Column(name = "MONTH_")
    var month: Int? = null

    @Transient
    @MetaProperty(related = ["month", "year"], datatype = "yearMonth")
    private var yearMonth: String? = null


    @Column(name = "VALUE_")
    var value: Int? = null

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ACTIVITY_ID")
    var activity: Activity? = null

    fun getYearMonth(): YearMonth? {
        return if (year != null && month != null) {
            YearMonth.of(year!!, month!!)
        } else null
    }
    fun setYearMonth(yearMonth: YearMonth?) {
        year = yearMonth?.year
        month = yearMonth?.monthValue
    }

    @Transient
    @MetaProperty(related = ["month", "year"])
    fun getLocalDate() : LocalDate? {
        var localDate : LocalDate? = null
        if (month != null && year != null) {
            localDate = LocalDate.of(year!!, month!!, 1)
        }
        return localDate
    }


    companion object {
        private const val serialVersionUID = 5034053048652285158L
    }
}