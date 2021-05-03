package com.borets.pfa.entity.account.system

import com.borets.pfa.entity.account.appdata.ApplicationData
import com.haulmont.chile.core.annotations.Composition
import com.haulmont.cuba.core.entity.StandardEntity
import com.haulmont.cuba.core.entity.annotation.OnDelete
import com.haulmont.cuba.core.global.DeletePolicy
import javax.persistence.*

@Table(name = "PFA_SYSTEM_STD")
@javax.persistence.Entity(name = "pfa_SystemStd")
open class SystemStd : StandardEntity() {
    @Column(name = "SYSTEM_ID")
    var systemId: String? = null

    @Column(name = "CASING_SIZE")
    private var casingSize: String? = null

    @Column(name = "CASING_WEIGHT")
    private var casingWeight: String? = null

    @Column(name = "PUMP")
    var pump: String? = null

    @Column(name = "MOTOR_POWER")
    var motorPower: Int? = null

    @Column(name = "HEAD")
    var head: Int? = null

    @Column(name = "MOTOR")
    var motor: String? = null

    @Column(name = "COMMENT_")
    var comment: String? = null

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "system")
    var details: MutableList<SystemDetail>? = mutableListOf()

    @JoinTable(
        name = "PFA_APPLICATION_DATA_SYSTEM_STD_LINK",
        joinColumns = [JoinColumn(name = "SYSTEM_STD_ID")],
        inverseJoinColumns = [JoinColumn(name = "APPLICATION_DATA_ID")]
    )
    @ManyToMany
    var applicationDetails: MutableList<ApplicationData>? = mutableListOf()

    fun getCasingWeight(): WellCasingWeight? = casingWeight?.let { WellCasingWeight.fromId(it) }

    fun setCasingWeight(casingWeight: WellCasingWeight?) {
        this.casingWeight = casingWeight?.id
    }

    fun getCasingSize(): WellCasingSize? = casingSize?.let { WellCasingSize.fromId(it) }

    fun setCasingSize(casingSize: WellCasingSize?) {
        this.casingSize = casingSize?.id
    }

    companion object {
        private const val serialVersionUID = -833801125462163260L
    }
}