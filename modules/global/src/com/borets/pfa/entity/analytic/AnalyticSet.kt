package com.borets.pfa.entity.analytic

import com.borets.pfa.entity.activity.ContractType
import com.borets.pfa.entity.activity.JobType
import com.borets.pfa.entity.activity.WellEquip
import com.borets.pfa.entity.activity.WellTag
import com.haulmont.cuba.core.entity.StandardEntity
import javax.persistence.Column
import javax.persistence.Table

@Table(name = "PFA_ANALYTIC_SET")
@javax.persistence.Entity(name = "pfa_AnalyticSet")
open class AnalyticSet : StandardEntity() {
    companion object {
        private const val serialVersionUID = -9094885344023962029L
    }

    @Column(name = "CONTRACT_TYPE")
    private var contractType: String? = null

    @Column(name = "JOB_TYPE")
    private var jobType: String? = null

    @Column(name = "WELL_EQUIP")
    private var wellEquip: String? = null

    @Column(name = "WELL_TAG")
    private var wellTag: String? = null

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
}