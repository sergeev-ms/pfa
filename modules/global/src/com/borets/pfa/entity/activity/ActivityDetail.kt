package com.borets.pfa.entity.activity

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

    @Column(name = "CONTRACT_TYPE")
    private var contractType: String? = null

    @Column(name = "JOB_TYPE")
    private var jobType: String? = null

    @Column(name = "WELL_EQUIP")
    private var wellEquip: String? = null

    @Column(name = "WELL_TAG")
    private var wellTag: String? = null

    @NumberFormat(pattern = "####")
    @Column(name = "YEAR_")
    var year: Int? = null

    @NumberFormat(pattern = "0#")
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


    fun getWellTag(): WellTag? = wellTag?.let { WellTag.fromId(it) }

    fun setWellTag(wellTag: WellTag?) {
        this.wellTag = wellTag?.id
    }

    fun getWellEquip(): WellEquip? = wellEquip?.let { WellEquip.fromId(it) }

    fun setWellEquip(wellEquip: WellEquip?) {
        this.wellEquip = wellEquip?.id
    }

    fun getJobType(): JobType? = jobType?.let { JobType.fromId(it) }

    fun setJobType(jobType: JobType?) {
        this.jobType = jobType?.id
    }

    fun getContractType(): ContractType? = contractType?.let { ContractType.fromId(it) }

    fun setContractType(contractType: ContractType?) {
        this.contractType = contractType?.id
    }

    fun getRecordType(): RecordType? = recordType?.let { RecordType.fromId(it) }

    fun setRecordType(recordType: RecordType?) {
        this.recordType = recordType?.id
    }

    companion object {
        private const val serialVersionUID = 5034053048652285158L
    }
}