package com.borets.pfa.entity.account.system.classification

import com.haulmont.chile.core.annotations.NamePattern
import com.haulmont.cuba.core.entity.StandardEntity
import javax.persistence.Column
import javax.persistence.Table

@NamePattern(value = "%s|name")
@Table(name = "PFA_PUMP_CONFIG")
@javax.persistence.Entity(name = "pfa_PumpConfig")
open class PumpConfig : StandardEntity() {
    @Column(name = "NAME")
    var name: String? = null

    companion object {
        private const val serialVersionUID = -5632032803755410651L
    }
}