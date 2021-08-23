package com.borets.pfa.entity.account.supplementary

import com.haulmont.chile.core.annotations.MetaProperty
import com.haulmont.chile.core.annotations.NumberFormat
import com.haulmont.cuba.core.entity.StandardEntity
import java.math.BigDecimal
import java.time.YearMonth
import javax.persistence.*

@Table(name = "PFA_SUPPLEMENTARY_DETAIL")
@javax.persistence.Entity(name = "pfa_SupplementaryDetail")
open class SupplementaryDetail : StandardEntity() {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TYPE_ID")
    var type: SupplementaryDetailType? = null

    @Column(name = "VALUE_")
    var value: BigDecimal? = null

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "SUPPLEMENTARY_ID")
    var supplementary: Supplementary? = null

    @NumberFormat(pattern = "####")
    @Column(name = "YEAR_")
    var year: Int? = null

    //    @NumberFormat(pattern = "0#")
    @Column(name = "MONTH_")
    var month: Int? = null

    @Transient
    @MetaProperty(related = ["month", "year"], datatype = "yearMonth")
    var yearMonth: String? = null

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
        private const val serialVersionUID = 3121937638378122295L
    }
}