package com.borets.pfa.entity.analytic

import com.borets.pfa.entity.activity.JobType
import com.borets.pfa.entity.activity.WellEquip
import com.borets.pfa.entity.activity.WellTag
import com.haulmont.chile.core.annotations.NamePattern
import com.haulmont.cuba.core.entity.StandardEntity
import com.haulmont.cuba.core.global.AppBeans
import com.haulmont.cuba.core.global.Messages
import java.util.*
import javax.persistence.Column
import javax.persistence.Table

@NamePattern(value = "#getNameOfInstance|jobType,wellEquip,wellTag")
@Table(name = "PFA_ANALYTIC_SET")
@javax.persistence.Entity(name = "pfa_AnalyticSet")
open class AnalyticSet : StandardEntity() {
    companion object {
        private const val serialVersionUID = -9094885344023962029L
    }

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

    fun getNameOfInstance() : String {
        val messages = AppBeans.get(Messages.NAME, Messages::class.java)
        return listOfNotNull(getJobType(), getWellEquip(), getWellTag())
            .map { messages.getMessage(it, Locale.getDefault()) }
            .joinToString("-")
    }
}