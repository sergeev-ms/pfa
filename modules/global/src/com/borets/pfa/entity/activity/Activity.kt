package com.borets.pfa.entity.activity

import com.borets.pfa.entity.account.Account
import com.haulmont.chile.core.annotations.Composition
import com.haulmont.chile.core.annotations.NamePattern
import com.haulmont.cuba.core.entity.StandardEntity
import com.haulmont.cuba.core.entity.annotation.OnDelete
import com.haulmont.cuba.core.global.DeletePolicy
import javax.persistence.*

@NamePattern(value = "%s - %s|account,year")
@Table(name = "PFA_ACTIVITY")
@javax.persistence.Entity(name = "pfa_Activity")
open class Activity : StandardEntity() {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ACCOUNT_ID")
    var account: Account? = null

    @Column(name = "YEAR_")
    var year: Int? = null

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "activity")
    var details: MutableList<ActivityDetail>? = mutableListOf()

    companion object {
        private const val serialVersionUID = -6451509841681104317L
    }
}