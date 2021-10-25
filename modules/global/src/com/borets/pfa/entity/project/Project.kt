package com.borets.pfa.entity.project

import com.borets.pfa.entity.customer.DimCustomers
import com.haulmont.chile.core.annotations.MetaProperty
import com.haulmont.chile.core.annotations.NamePattern
import com.haulmont.cuba.core.entity.StandardEntity
import com.haulmont.cuba.core.entity.annotation.SystemLevel
import java.math.BigDecimal
import javax.persistence.*

@Table(name = "PFA_PROJECT")
@Entity(name = "pfa_Project")
@NamePattern(value = "%s - %s (%s)|well,wellId,region")
open class Project : StandardEntity() {

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

    @OneToMany(mappedBy = "project")
    var assignments: MutableList<ProjectAssignment>? = mutableListOf()

    companion object {
        private const val serialVersionUID = -6802373636661990336L
    }
}