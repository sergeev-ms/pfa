package com.borets.pfa.entity.project

import com.borets.pfa.entity.account.Account
import com.borets.pfa.entity.customer.DimCustomers
import com.haulmont.chile.core.annotations.MetaProperty
import com.haulmont.chile.core.annotations.NamePattern
import com.haulmont.cuba.core.entity.StandardEntity
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse
import com.haulmont.cuba.core.entity.annotation.SystemLevel
import com.haulmont.cuba.core.global.DeletePolicy
import java.math.BigDecimal
import javax.persistence.*

@Table(name = "PFA_PROJECT")
@Entity(name = "pfa_Project")
@NamePattern("%s - %s (%s)|well,wellId,region")
open class Project : StandardEntity() {
    @OnDeleteInverse(DeletePolicy.UNLINK)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ACCOUNT_ID")
    var account: Account? = null

    @Column(name = "CUSTOMER_NO")
    var customerNo: Int? = null

    @SystemLevel
    @Column(name = "CUSTOMER_ID", precision = 7, scale = 0)
    var customerId: BigDecimal? = null

    @Transient
    @MetaProperty(related = ["customerId"])
    var customer: DimCustomers? = null

    @Column(name = "REGION")
    var region: String? = null

    @Column(name = "WELL_ID")
    var wellId: String? = null

    @Column(name = "WELL")
    var well: String? = null

    @Column(name = "WELL_API")
    var wellApi: String? = null

    companion object {
        private const val serialVersionUID = -6802373636661990336L
    }
}