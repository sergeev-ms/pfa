package com.borets.pfa.entity.account.system

import com.borets.addon.mu.datatypes.Length
import com.borets.addon.pn.entity.Part
import com.borets.pfa.entity.account.appdata.EquipmentType
import com.haulmont.chile.core.annotations.MetaProperty
import com.haulmont.cuba.core.entity.StandardEntity
import java.math.BigDecimal
import javax.persistence.*

@Table(name = "PFA_SYSTEM_DETAIL")
@javax.persistence.Entity(name = "pfa_SystemDetail")
open class SystemDetail : StandardEntity() {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EQUIPMENT_TYPE_ID")
    var equipmentType: EquipmentType? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PART_NUMBER_ID")
    var partNumber: Part? = null

    @MetaProperty(datatype = Length.NAME)
    @Column(name = "LENGTH")
    var length: BigDecimal? = null

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "SYSTEM_ID")
    var system: SystemStd? = null

    companion object {
        private const val serialVersionUID = 5553881343254490045L
    }
}