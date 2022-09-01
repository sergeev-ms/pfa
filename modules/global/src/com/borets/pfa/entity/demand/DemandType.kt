package com.borets.pfa.entity.demand

import com.haulmont.chile.core.annotations.NamePattern
import com.haulmont.cuba.core.entity.StandardEntity
import javax.persistence.Column
import javax.persistence.Table

@NamePattern(value = "%s|name")
@Table(name = "PFA_DEMAND_TYPE")
@javax.persistence.Entity(name = "pfa_DemandType")
open class DemandType : StandardEntity() {
    @Column(name = "NAME")
    var name: String? = null

    companion object {
        private const val serialVersionUID = 6444608433529939529L
    }
}