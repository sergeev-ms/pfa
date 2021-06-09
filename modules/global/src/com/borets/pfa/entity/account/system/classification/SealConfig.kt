package com.borets.pfa.entity.account.system.classification

import com.haulmont.chile.core.annotations.NamePattern
import com.haulmont.cuba.core.entity.StandardEntity
import javax.persistence.Column
import javax.persistence.Table

@NamePattern(value = "%s|name")
@Table(name = "PFA_SEAL_CONFIG")
@javax.persistence.Entity(name = "pfa_SealConfig")
open class SealConfig : StandardEntity() {
    @Column(name = "NAME")
    var name: String? = null

    companion object {
        private const val serialVersionUID = 5206918592308683045L
    }
}