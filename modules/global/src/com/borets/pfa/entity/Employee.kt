package com.borets.pfa.entity

import com.haulmont.chile.core.annotations.NamePattern
import com.haulmont.cuba.core.entity.StandardEntity
import javax.persistence.Column
import javax.persistence.Table

@NamePattern(value = "%s|name")
@Table(name = "PFA_EMPLOYEE")
@javax.persistence.Entity(name = "pfa_Employee")
open class Employee : StandardEntity() {
    @Column(name = "FIRST_NAME")
    var firstName: String? = null

    @Column(name = "LAST_NAME")
    var lastName: String? = null

    @Column(name = "NAME")
    var name: String? = null

    companion object {
        private const val serialVersionUID = -7416490462670014346L
    }
}