package com.borets.pfa.entity.customer

import com.borets.pfa.entity.account.Account
import com.haulmont.chile.core.annotations.MetaProperty
import com.haulmont.chile.core.annotations.NamePattern
import com.haulmont.cuba.core.entity.StandardEntity
import com.haulmont.cuba.core.entity.annotation.SystemLevel
import java.math.BigDecimal
import javax.persistence.*

@NamePattern(value = "%s|name")
@Table(name = "PFA_CUSTOMER")
@Entity(name = "pfa_Customer")
open class Customer : StandardEntity() {
    companion object {
        private const val serialVersionUID = -3413487657047562556L
    }

    @Column(name = "NAME")
    var name: String? = null

    @SystemLevel
    @Column(name = "DIM_CUSTOMER_ID", precision = 7, scale = 0)
    var dimCustomerId: BigDecimal? = null

    @Transient
    @MetaProperty(related = ["dimCustomerId"])
    var dimCustomer: DimCustomers? = null

    @ManyToMany(mappedBy = "customers")
    var accounts: MutableList<Account>? = mutableListOf()
}