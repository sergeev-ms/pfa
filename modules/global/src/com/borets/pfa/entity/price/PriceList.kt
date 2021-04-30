package com.borets.pfa.entity.price

import com.borets.pfa.entity.account.Account
import com.borets.pfa.entity.activity.RecordType
import com.haulmont.chile.core.annotations.Composition
import com.haulmont.chile.core.annotations.MetaProperty
import com.haulmont.chile.core.annotations.NumberFormat
import com.haulmont.cuba.core.entity.StandardEntity
import com.haulmont.cuba.core.entity.annotation.OnDelete
import com.haulmont.cuba.core.global.DeletePolicy
import java.time.YearMonth
import javax.persistence.*

@Table(name = "PFA_PRICE_LIST")
@javax.persistence.Entity(name = "pfa_PriceList")
open class PriceList : StandardEntity() {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ACCOUNT_ID")
    var account: Account? = null

    @Column(name = "RECORD_TYPE")
    private var recordType: String? = null

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "priceList")
    var details: MutableList<PriceListDetail>? = mutableListOf()

    @NumberFormat(pattern = "####")
    @Column(name = "YEAR_")
    var year: Int? = null

    //    @NumberFormat(pattern = "0#")
    @Column(name = "MONTH_")
    var month: Int? = null

    @Transient
    @MetaProperty(related = ["month", "year"], datatype = "yearMonth")
    private var yearMonth: String? = null

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
        private const val serialVersionUID = 8349344481821215196L
    }
}