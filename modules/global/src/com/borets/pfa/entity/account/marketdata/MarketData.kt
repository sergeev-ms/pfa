package com.borets.pfa.entity.account.marketdata

import com.borets.pfa.entity.account.Account
import com.borets.pfa.entity.activity.ContractType
import com.haulmont.chile.core.annotations.MetaProperty
import com.haulmont.cuba.core.entity.StandardEntity
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


    @Column(name = "CONTRACT_TYPE")
    private var contractType: String? = null

    @Column(name = "APPLICATION_TYPE")
    private var applicationType: String? = null

    @Column(name = "FIELD_TYPE")
    private var fieldType: String? = null

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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ACCOUNT_ID")
    var account: Account? = null

    fun getRunsNumber(): RunsNumber? = runsNumber?.let { RunsNumber.fromId(it) }

    fun setRunsNumber(runsNumber: RunsNumber?) {
        this.runsNumber = runsNumber?.id
    }

    fun getFieldType(): FieldType? = fieldType?.let { FieldType.fromId(it) }

    fun setFieldType(fieldType: FieldType?) {
        this.fieldType = fieldType?.id
    }

    fun getApplicationType(): ApplicationType? = applicationType?.let { ApplicationType.fromId(it) }

    fun setApplicationType(applicationType: ApplicationType?) {
        this.applicationType = applicationType?.id
    }

    fun getContractType(): ContractType? = contractType?.let { ContractType.fromId(it) }

    fun setContractType(contractType: ContractType?) {
        this.contractType = contractType?.id
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