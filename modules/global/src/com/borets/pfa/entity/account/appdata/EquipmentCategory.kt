package com.borets.pfa.entity.account.appdata

import com.haulmont.chile.core.annotations.NamePattern
import com.haulmont.cuba.core.entity.StandardEntity
import javax.persistence.Column
import javax.persistence.Table

@NamePattern(value = "%s|name")
@Table(name = "PFA_EQUIPMENT_CATEGORY")
@javax.persistence.Entity(name = "pfa_EquipmentCategory")
open class EquipmentCategory : StandardEntity() {
    @Column(name = "NAME")
    var name: String? = null

    companion object {
        private const val serialVersionUID = -2546378179873938165L
    }
}