package com.borets.pfa.entity.account.system

import com.borets.pfa.entity.account.appdata.EquipmentType
import com.haulmont.cuba.core.entity.StandardEntity
import javax.persistence.*

@Table(name = "PFA_SYSTEM_DETAIL")
@javax.persistence.Entity(name = "pfa_SystemDetail")
open class SystemDetail : StandardEntity() {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EQUIPMENT_TYPE_ID")
    var equipmentType: EquipmentType? = null

    @Column(name = "PART_NUMBER")
    var partNumber: String? = null

    @Column(name = "QTY")
    var qty: Int? = null

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "SYSTEM_ID")
    var system: SystemStd? = null

    companion object {
        private const val serialVersionUID = 5553881343254490045L
    }
}