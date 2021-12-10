package com.borets.pfa.entity.setting

import com.borets.pfa.entity.analytic.AnalyticSet
import com.borets.pfa.entity.price.RevenueType
import com.haulmont.cuba.core.entity.StandardEntity
import javax.persistence.*

@Table(name = "PFA_COUNTRY_SETTING_REVENUE_TYPE")
@javax.persistence.Entity(name = "pfa_CountrySettingRevenueType")
open class CountrySettingRevenueType : StandardEntity() {
    companion object {
        private const val serialVersionUID = -370318111377816625L
    }

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "COUNTRY_SETTING_ID")
    var countrySetting: CountrySetting? = null


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REVENUE_TYPE_ID")
    var revenueType: RevenueType? = null

    @JoinTable(
        name = "PFA_COUNTRY_SETTING_REVENUE_TYPE_ANALYTIC_SET_LINK",
        joinColumns = [JoinColumn(name = "COUNTRY_SETTING_REVENUE_TYPE_ID")],
        inverseJoinColumns = [JoinColumn(name = "ANALYTIC_SET_ID")]
    )
    @ManyToMany
    var analyticSets: MutableList<AnalyticSet>? = mutableListOf()
}