package com.borets.pfa.entity.customer

import com.haulmont.chile.core.annotations.NamePattern
import com.haulmont.cuba.core.entity.BaseDbGeneratedIdEntity
import com.haulmont.cuba.core.global.DbView
import com.haulmont.cuba.core.global.DdlGeneration
import java.math.BigDecimal
import javax.persistence.Column
import javax.persistence.Id
import javax.persistence.Table

@NamePattern(value = "%s|customerName")
@DbView
@DdlGeneration(value = DdlGeneration.DbScriptGenerationMode.CREATE_ONLY)
@Table(name = "DimCustomers_vw")
@javax.persistence.Entity(name = "pfa_DimCustomers")
open class DimCustomers : BaseDbGeneratedIdEntity<BigDecimal>() {

    @Id
    @Column(name = "CUSTOMER_NO", nullable = false, precision = 7, scale = 0)
    var customerNo: BigDecimal? = null

    @Column(name = "CUSTOMER_NAME", nullable = false, length = 30)
    var customerName: String? = null

    @Column(name = "COUNTRY", length = 30)
    var country: String? = null

    @Column(name = "TAG", nullable = false, length = 20)
    var tag: String? = null

    override fun setDbGeneratedId(dbId: BigDecimal?) {
        this.customerNo = dbId
    }

    override fun getDbGeneratedId(): BigDecimal? {
        return customerNo;
    }

    companion object {
        private const val serialVersionUID = 4292803121765772757L
    }
}