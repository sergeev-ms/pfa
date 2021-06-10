package com.borets.pfa.entity.account.system.classification

import com.haulmont.chile.core.annotations.NamePattern
import com.haulmont.cuba.core.entity.StandardEntity
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table

@NamePattern(value = "%s|name")
@Table(name = "PFA_MATERIALS")
@Entity(name = "pfa_Materials")
abstract class Materials : StandardEntity() {
    @Column(name = "NAME")
    var name: String? = null

    companion object {
        private const val serialVersionUID = 4077360172419445865L
    }
}