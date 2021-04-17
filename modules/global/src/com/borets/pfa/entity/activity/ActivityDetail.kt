package com.borets.pfa.entity.activity

import com.haulmont.cuba.core.entity.StandardEntity
import java.math.BigDecimal
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

    @Column(name = "YEAR_")
    var year: Int? = null

    @Column(name = "MONTH_")
    var month: Int? = null

    @Column(name = "VALUE_")
    var value: BigDecimal? = null

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ACTIVITY_ID")
    var activity: Activity? = null

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