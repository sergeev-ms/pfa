package com.borets.pfa.entity.account.marketdata

import com.borets.pfa.entity.account.Account
import com.borets.pfa.entity.activity.RecordType
import com.haulmont.chile.core.annotations.MetaProperty
import com.haulmont.chile.core.annotations.NumberFormat
import com.haulmont.cuba.core.entity.StandardEntity
import java.math.BigDecimal
import java.time.YearMonth
import javax.persistence.*

@Table(name = "PFA_MARKET_DATA")
@javax.persistence.Entity(name = "pfa_MarketData")
open class MarketData : StandardEntity() {
    companion object {
        private const val serialVersionUID = 1579333132019517442L
    }

    @Column(name = "YEAR_")
    var year: Int? = null

    @Column(name = "MONTH_")
    var month: Int? = null

    @Transient
    @MetaProperty(related = ["month", "year"], datatype = "yearMonth")
    private var yearMonth: String? = null

    @Column(name = "RECORD_TYPE")
    private var recordType: String? = null

    @Column(name = "RUNS_NUMBER")
    private var runsNumber: String? = RunsNumber.ONE.id

    @Column(name = "F_RUN_DURATION")
    var firstRunDuration: Int? = null

    @Column(name = "S_RUN_DURATION")
    var secondRunDuration: Int? = null

    @Column(name = "TH_RUN_DURATION")
    var thirdRunDuration: Int? = null

    @Column(name = "THP_RUN_DURATION")
    var thirdPlusRunDuration: Int? = null

    @Column(name = "TRL")
    var trl: Int? = null

    @Column(name = "ARL")
    var arl: Int? = null

    @Column(name = "WELL_COUNT")
    var wellCount: Int? = null

    @NumberFormat(pattern = "#%")
    @Column(name = "CONVERSION_RATE")
    var conversionRate: BigDecimal? = null

    @Column(name = "OIL_PERMITS")
    var oilPermits: Int? = null

    @Column(name = "RIG_QTY")
    var rigQty: Int? = null

    @Column(name = "DUC_QTY")
    var ducQty: Int? = null

    @NumberFormat(pattern = "#%")
    @Column(name = "COMPLETION")
    var completion: BigDecimal? = null

    @NumberFormat(pattern = "#%")
    @Column(name = "ACTIVITY_RATE")
    var activityRate: BigDecimal? = null

    @Column(name = "BUDGET")
    var budget: Int? = null

    @NumberFormat(pattern = "#%")
    @Column(name = "B_SHARE")
    var bShare: BigDecimal? = null

    @Column(name = "IS_WELL_MONITOR")
    var isWellMonitor: Boolean? = false
        private set

    @Column(name = "WELL_MONITOR_QTY")
    var wellMonitorQty: Int? = null

    @Column(name = "B_WELL_COUNT")
    var bWellCount: Int? = null

    @Column(name = "RENTAL_CAPEX")
    var rentalCapex: Int? = null

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ACCOUNT_ID")
    var account: Account? = null

    @Column(name = "NEW_WELL_YEAR")
    var newWellYear: Int? = null

    @NumberFormat(pattern = "#%")
    @Column(name = "WELL_CHECK_RATE")
    var wellCheckRate: BigDecimal? = null

    @Column(name = "ESP_LT_TARGET")
    var espLtTarget: Int? = null

    @Column(name = "MARKET_SHARE_TARGET")
    var marketShareTarget: Int? = null

    @Column(name = "BORETS_RUN_LIFE")
    var boretsRunLife: Int? = null

    @Column(name = "DELAY_FACTOR")
    var delayFactor: Int? = null

    @MetaProperty
    fun getRunbackInYear(): Int? = null  // TODO

    @MetaProperty
    fun getPullsInYear(): Int? = null // TODO

    @MetaProperty
    fun getTotalPullsInYear(): Int? = null // TODO

    @MetaProperty
    fun getTotalInstallInYear(): Int? = null // TODO

    fun getRecordType(): RecordType? = recordType?.let { RecordType.fromId(it) }

    fun setRecordType(recordType: RecordType?) {
        this.recordType = recordType?.id
    }

    fun getRunsNumber(): RunsNumber? = runsNumber?.let { RunsNumber.fromId(it) }

    fun setRunsNumber(runsNumber: RunsNumber?) {
        this.runsNumber = runsNumber?.id
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
}