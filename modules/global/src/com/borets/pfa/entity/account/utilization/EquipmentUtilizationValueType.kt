package com.borets.pfa.entity.account.utilization

import com.haulmont.chile.core.annotations.NamePattern
import com.haulmont.cuba.core.entity.StandardEntity
import javax.persistence.Column
import javax.persistence.Table

@NamePattern(value = "%s|name")
@Table(name = "PFA_EQUIPMENT_UTILIZATION_VALUE_TYPE")
@javax.persistence.Entity(name = "pfa_EquipmentUtilizationValueType")
open class EquipmentUtilizationValueType : StandardEntity() {
    @Column(name = "NAME")
    var name: String? = null

    companion object {
        private const val serialVersionUID = 8955668344592233460L
    }
}