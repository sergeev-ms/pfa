package com.borets.pfa.entity.setting

import com.borets.pfa.entity.analytic.AnalyticSet
import com.haulmont.cuba.core.entity.StandardEntity
import javax.persistence.*

@Table(name = "PFA_COUNTRY_SETTING_ANALYTIC_DETAIL")
@Entity(name = "pfa_CountrySettingAnalyticDetail")
open class CountrySettingAnalyticDetail : StandardEntity() {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "COUNTRY_SETTING_ID")
    var countrySetting: CountrySetting? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ANALYTIC_SET_ID")
    var analyticSet: AnalyticSet? = null

    @Column(name = "PRICE_LIST")
    var priceList: Boolean? = true

    @Column(name = "ACTIVITY_PLAN")
    var activityPlan: Boolean? = true

    @Column(name = "ORDER_")
    var order: Int? = null

    companion object {
        private const val serialVersionUID = -8218465495030508187L
    }
}