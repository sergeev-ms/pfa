package com.borets.pfa.entity.account

import com.borets.pfa.entity.customer.DimCustomers
import com.haulmont.chile.core.annotations.MetaProperty
import com.haulmont.chile.core.annotations.NamePattern
import com.haulmont.cuba.core.entity.StandardEntity
import com.haulmont.cuba.core.entity.annotation.SystemLevel
import java.math.BigDecimal
import javax.persistence.*

@NamePattern(value = "%s|name")
@Table(name = "PFA_ACCOUNT")
@javax.persistence.Entity(name = "pfa_Account")
open class Account : StandardEntity() {
    @Column(name = "NAME")
    var name: String? = null

    @Column(name = "TYPE_")
    private var type: String? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_ID")
    var parent: Account? = null

    @SystemLevel
    @Column(name = "CUSTOMER_ID", precision = 7, scale = 0)
    var customerId: BigDecimal? = null

    @Transient
    @MetaProperty(related = ["customerId"])
    var customer: DimCustomers? = null

    @Transient
    @MetaProperty
    var customers: MutableList<DimCustomers>? = mutableListOf()

    fun getType(): Type? = type?.let { Type.fromId(it) }

    fun setType(type: Type?) {
        this.type = type?.id
    }

    companion object {
        private const val serialVersionUID = -5049196871514871532L
    }
}