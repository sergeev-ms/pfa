package com.borets.pfa.entity.account.system

import com.borets.addon.country.entity.Country
import com.borets.pfa.entity.account.appdata.SystemAllocation
import com.borets.pfa.entity.account.system.classification.*
import com.haulmont.chile.core.annotations.Composition
import com.haulmont.cuba.core.entity.StandardEntity
import com.haulmont.cuba.core.entity.annotation.OnDelete
import com.haulmont.cuba.core.global.DeletePolicy
import javax.persistence.*

@Table(name = "PFA_SYSTEM_STD")
@Entity(name = "pfa_SystemStd")
open class SystemStd : StandardEntity() {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COUNTRY_ID")
    var country: Country? = null

    @Column(name = "SYSTEM_ID")
    var systemId: String? = null

    @Column(name = "CASING_SIZE")
    private var casingSize: String? = null

    @Column(name = "CASING_WEIGHT")
    private var casingWeight: String? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PUMP_TYPE_ID")
    var pumpModel: PumpType? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DEPTH_ID")
    var depth: Depth? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MOTOR_TYPE_ID")
    var motorType: MotorType? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "INTAKE_CONFIG_ID")
    var intakeConfig: IntakeConfig? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "VAPRO_CONFIG_ID")
    var vaproConfig: VaproConfig? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SEAL_CONFIG_ID")
    var sealConfig: SealConfig? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PUMP_CONFIG_ID")
    var pumpConfig: PumpConfig? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PUMP_MATERIALS_ID")
    var pumpMaterials: PumpMaterials? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SEAL_MATERIALS_ID")
    var sealMaterials: OtherMaterials? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MOTOR_MATERIALS_ID")
    var motorMaterials: OtherMaterials? = null

    @Column(name = "COMMENT_")
    var comment: String? = null

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "system")
    var details: MutableList<SystemDetail>? = mutableListOf()

    @OnDelete(DeletePolicy.CASCADE)
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "system")
    var allocation: SystemAllocation? = null

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