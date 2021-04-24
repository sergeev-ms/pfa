package com.borets.pfa.entity.price

import com.borets.pfa.entity.analytic.AnalyticSet
import com.haulmont.cuba.core.entity.StandardEntity
import java.math.BigDecimal
import javax.persistence.*

@Table(name = "PFA_PRICE_LIST_DETAIL")
@javax.persistence.Entity(name = "pfa_PriceListDetail")
open class PriceListDetail : StandardEntity() {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PRICE_LIST_ID")
    var priceList: PriceList? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ANALYTIC_ID")
    var analytic: AnalyticSet? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REVENUE_TYPE_ID")
    var revenueType: RevenueType? = null

    @Column(name = "VALUE_")
    var value: BigDecimal? = null

    companion object {
        private const val serialVersionUID = 7044971976756800438L
    }
}