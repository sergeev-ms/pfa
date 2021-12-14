package com.borets.pfa.entity.account.directsale

import com.borets.addon.pn.entity.Part
import com.haulmont.chile.core.annotations.MetaProperty
import com.haulmont.cuba.core.entity.StandardEntity
import java.math.BigDecimal
import javax.persistence.*

@Table(name = "PFA_DIRECT_SALE_DETAIL")
@javax.persistence.Entity(name = "pfa_DirectSaleDetail")
open class DirectSaleDetail : StandardEntity() {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "DIRECT_SALE_ID")
    var directSale: DirectSale? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PART_ID")
    var part: Part? = null

//    @Column(name = "QTY")
//    var qty: Int? = null

    @MetaProperty(datatype = "Length")
    @Column(name = "LENGTH")
    var length: BigDecimal? = BigDecimal.ZERO

    @Column(name = "PRICE")
    var price: Int? = 0

    companion object {
        private const val serialVersionUID = 3874006177258997456L
    }
}