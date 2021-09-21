package com.borets.pfa.entity.account.supplementary

import com.borets.pfa.entity.account.Account
import com.haulmont.chile.core.annotations.Composition
import com.haulmont.cuba.core.entity.StandardEntity
import com.haulmont.cuba.core.entity.annotation.OnDelete
import com.haulmont.cuba.core.global.DeletePolicy
import javax.persistence.*

@Table(name = "PFA_SUPPLEMENTARY")
@javax.persistence.Entity(name = "pfa_Supplementary")
open class Supplementary : StandardEntity() {
    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "supplementary")
    var details: MutableList<SupplementaryDetail>? = mutableListOf()

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ACCOUNT_ID")
    var account: Account? = null

    companion object {
        private const val serialVersionUID = 6575997675956512185L
    }
}