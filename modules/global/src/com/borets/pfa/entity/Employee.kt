package com.borets.pfa.entity

import com.borets.addon.country.entity.Country
import com.haulmont.chile.core.annotations.NamePattern
import com.haulmont.cuba.core.entity.StandardEntity
import com.haulmont.cuba.security.entity.User
import javax.persistence.*

@NamePattern(value = "%s|name")
@Table(name = "PFA_EMPLOYEE")
@javax.persistence.Entity(name = "pfa_Employee")
open class Employee : StandardEntity() {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COUNTRY_ID")
    var country: Country? = null

    @Column(name = "FIRST_NAME")
    var firstName: String? = null

    @Column(name = "LAST_NAME")
    var lastName: String? = null

    @Column(name = "NAME")
    var name: String? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    var user: User? = null

    companion object {
        private const val serialVersionUID = -7416490462670014346L
    }
}