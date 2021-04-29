package com.borets.pfa.entity.activity

import com.borets.pfa.entity.analytic.AnalyticSet
import com.haulmont.chile.core.annotations.MetaProperty
import com.haulmont.chile.core.annotations.NumberFormat
import com.haulmont.cuba.core.entity.StandardEntity
import java.time.YearMonth
import javax.persistence.*

@Table(name = "PFA_ACTIVITY_DETAIL")
@javax.persistence.Entity(name = "pfa_ActivityDetail")
open class ActivityDetail : StandardEntity() {
    @Column(name = "RECORD_TYPE")
    private var recordType: String? = null

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


    fun getRecordType(): RecordType? = recordType?.let { RecordType.fromId(it) }

    fun setRecordType(recordType: RecordType?) {
        this.recordType = recordType?.id
    }

    companion object {
        private const val serialVersionUID = 5034053048652285158L
    }
}