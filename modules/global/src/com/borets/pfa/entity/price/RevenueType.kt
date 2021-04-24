package com.borets.pfa.entity.price

import com.haulmont.chile.core.annotations.NamePattern
import com.haulmont.cuba.core.entity.StandardEntity
import javax.persistence.Column
import javax.persistence.Table

@NamePattern(value = "%s|name")
@Table(name = "PFA_REVENUE_TYPE")
@javax.persistence.Entity(name = "pfa_RevenueType")
open class RevenueType : StandardEntity() {
    @Column(name = "NAME")
    var name: String? = null

    companion object {
        private const val serialVersionUID = -6915504526183386564L
    }
}