package com.borets.pfa.entity.account

import com.haulmont.chile.core.annotations.NamePattern
import com.haulmont.cuba.core.entity.StandardEntity
import javax.persistence.Column
import javax.persistence.Table

@NamePattern(value = "%s|name")
@Table(name = "PFA_ACCOUNT")
@javax.persistence.Entity(name = "pfa_Account")
open class Account : StandardEntity() {
    @Column(name = "NAME")
    var name: String? = null

    companion object {
        private const val serialVersionUID = -5049196871514871532L
    }
}